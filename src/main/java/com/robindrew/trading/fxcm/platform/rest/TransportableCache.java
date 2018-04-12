package com.robindrew.trading.fxcm.platform.rest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.fxcm.messaging.ITransportable;
import com.robindrew.common.util.Check;
import com.robindrew.common.util.Threads;

public class TransportableCache {

	private final Map<String, ITransportable> cache = new ConcurrentHashMap<>();

	public void put(String requestId, ITransportable response) {
		Check.notEmpty("requestId", requestId);
		Check.notNull("response", response);
		cache.put(requestId, response);
	}

	public <T extends ITransportable> TransportableFuture<T> get(String requestId) {
		Check.notEmpty("requestId", requestId);
		return new TransportableFuture<>(requestId);
	}

	public class TransportableFuture<T extends ITransportable> implements Future<T> {

		private final String requestId;

		public TransportableFuture(String requestId) {
			this.requestId = requestId;
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			return cache.containsKey(requestId);
		}

		@SuppressWarnings("unchecked")
		@Override
		public T get() {
			while (true) {
				ITransportable response = cache.get(requestId);
				if (response != null) {
					return (T) response;
				}
				Threads.sleep(100);
			}
		}

		@Override
		public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			throw new UnsupportedOperationException();
		}

	}
}
