package com.robindrew.trading.fxcm.platform.rest.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.robindrew.common.util.Check;
import com.robindrew.common.util.Java;

public class ResponseFutureCache {

	private final Map<String, Object> cache = new HashMap<>();

	private final Lock lock = new ReentrantLock(true);
	private final Condition condition = lock.newCondition();

	public void put(String requestId, Object response) {
		Check.notEmpty("requestId", requestId);
		Check.notNull("response", response);

		lock.lock();
		try {
			// Special case: partial responses
			IResponseList list = (IResponseList) cache.get(requestId);
			if (list != null) {
				list.addResponse(response);
				if (list.isReady()) {
					condition.signalAll();
				}
				return;
			}

			cache.put(requestId, response);
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	public <T> Optional<T> getOptional(String requestId) {
		lock.lock();
		try {
			T response = (T) cache.get(requestId);
			return Optional.ofNullable(response);
		} finally {
			lock.unlock();
		}
	}

	public <T> ResponseFuture<T> get(String requestId) {
		Check.notEmpty("requestId", requestId);
		return new ResponseFuture<>(requestId);
	}

	public <T> T awaitAndGet(String requestId) {
		Check.notEmpty("requestId", requestId);
		ResponseFuture<T> future = new ResponseFuture<>(requestId);
		return future.get();
	}

	public class ResponseFuture<T> implements Future<T> {

		private final String requestId;
		private volatile T response;

		public ResponseFuture(String requestId) {
			this.requestId = requestId;
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
		public T get() {
			try {
				return awaitResponse();
			} catch (Exception e) {
				throw Java.propagate(e);
			}
		}

		private T awaitResponse() throws InterruptedException {
			while (true) {
				lock.lock();
				try {
					response = updateResponse();
					if (response != null) {
						return response;
					}
					condition.await();
				} finally {
					lock.unlock();
				}
			}
		}

		@SuppressWarnings("unchecked")
		private T updateResponse() {
			T response = (T) cache.remove(requestId);
			if (response == null) {
				return null;
			}

			// Special case
			if (response instanceof IResponseList) {
				IResponseList list = (IResponseList) response;
				if (!list.isReady()) {
					cache.put(requestId, response);
					return null;
				}
			}

			// Done!
			return response;
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
