package com.robindrew.trading.fxcm.platform.rest;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.fxcm.messaging.ITransportable;
import com.robindrew.common.util.Check;
import com.robindrew.common.util.Java;

public class TransportableCache {

	private final Map<String, ITransportable> cache = new HashMap<>();

	private final Lock lock = new ReentrantLock(true);
	private final Condition condition = lock.newCondition();

	public void put(String requestId, ITransportable response) {
		Check.notEmpty("requestId", requestId);
		Check.notNull("response", response);

		lock.lock();
		try {
			cache.put(requestId, response);
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public <T extends ITransportable> TransportableFuture<T> get(String requestId) {
		Check.notEmpty("requestId", requestId);
		return new TransportableFuture<>(requestId);
	}

	public class TransportableFuture<T extends ITransportable> implements Future<T> {

		private final String requestId;
		private volatile ITransportable response;

		public TransportableFuture(String requestId) {
			this.requestId = requestId;
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			lock.lock();
			try {
				return cache.containsKey(requestId);
			} finally {
				lock.unlock();
			}
		}

		@Override
		public T get() {
			try {
				return awaitResponse();
			} catch (Exception e) {
				throw Java.propagate(e);
			}
		}

		@SuppressWarnings("unchecked")
		private T awaitResponse() throws InterruptedException {
			while (true) {
				lock.lock();
				try {
					response = cache.remove(requestId);
					if (response != null) {
						return (T) response;
					}
					condition.await();
				} finally {
					lock.unlock();
				}
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			long waitUntilTime = System.currentTimeMillis() + unit.toMillis(timeout);
			while (true) {
				lock.lock();
				try {
					response = cache.remove(requestId);
					if (response != null) {
						return (T) response;
					}

					long timeRemaining = waitUntilTime - System.currentTimeMillis();
					if (timeRemaining <= 0) {
						throw new TimeoutException();
					}
					condition.await(timeRemaining, MILLISECONDS);
				} finally {
					lock.unlock();
				}
			}
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			throw new UnsupportedOperationException();
		}

	}
}
