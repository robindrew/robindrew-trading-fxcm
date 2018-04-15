package com.robindrew.trading.fxcm.platform.rest.response;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.fxcm.messaging.ITransportable;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder.SetMultimapBuilder;
import com.robindrew.common.util.Check;
import com.robindrew.common.util.Java;

/**
 * The Gateway Response Cache handles the asynchronous responses received for gateway requests.
 */
public class GatewayResponseCache {

	private final Multimap<String, ITransportable> cache = SetMultimapBuilder.hashKeys().arrayListValues().build();

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

	public GatewayResponse get(String requestId) {
		Check.notEmpty("requestId", requestId);
		return new GatewayResponse(requestId);
	}

	@SuppressWarnings("unchecked")
	public <T extends ITransportable> T getAndClose(String requestId) {
		Check.notEmpty("requestId", requestId);
		try (GatewayResponse future = new GatewayResponse(requestId)) {
			List<ITransportable> responses = future.get();
			return (T) responses.get(0);
		}
	}

	public class GatewayResponse implements Future<List<ITransportable>>, AutoCloseable {

		private final String requestId;

		public GatewayResponse(String requestId) {
			this.requestId = requestId;
		}

		@Override
		public void close() {
			cache.removeAll(requestId);
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<ITransportable> get() {
			try {
				return awaitResponse();
			} catch (Exception e) {
				throw Java.propagate(e);
			}
		}

		private List<ITransportable> awaitResponse() throws InterruptedException {
			lock.lock();
			try {
				while (true) {
					Collection<ITransportable> value = cache.get(requestId);
					if (!value.isEmpty()) {
						return new LinkedList<>(value);
					}
					condition.await();
				}
			} finally {
				lock.unlock();
			}
		}

		@Override
		public List<ITransportable> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			throw new UnsupportedOperationException();
		}
	}
}
