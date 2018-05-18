package com.robindrew.trading.fxcm.platform.api.java.gateway;

import static com.robindrew.trading.fxcm.platform.api.java.FxcmJava.toFxcmInstrument;
import static com.robindrew.trading.fxcm.platform.api.java.FxcmJava.toPriceCandle;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fxcm.fix.pretrade.MarketDataSnapshot;
import com.robindrew.common.util.Check;
import com.robindrew.common.util.Threads;
import com.robindrew.trading.fxcm.IFxcmInstrument;
import com.robindrew.trading.price.candle.IPriceCandle;
import com.robindrew.trading.price.candle.ITickPriceCandle;

public class MarketDataSnapshotListener extends Thread implements AutoCloseable {

	private static final Logger log = LoggerFactory.getLogger(MarketDataSnapshotListener.class);

	private final BlockingDeque<MarketDataSnapshot> queue = new LinkedBlockingDeque<>();
	private final AtomicBoolean shutdown = new AtomicBoolean(false);
	private volatile IFxcmGatewayTickHandler tickHandler;

	public void run() {
		while (!isShutdown()) {

			// Handle snapshots
			handleSnapshotsAsync();

			// Sleep
			if (queue.isEmpty()) {
				Threads.sleep(50, MILLISECONDS);
			}
		}
	}

	private void handleSnapshotsAsync() {
		try {
			List<MarketDataSnapshot> snapshots = new ArrayList<>();
			queue.drainTo(snapshots);

			for (MarketDataSnapshot snapshot : snapshots) {
				handleSnapshotAsync(snapshot);
			}

		} catch (Exception e) {
			log.warn("Error handling snapshots", e);
			Threads.sleep(10, SECONDS);
		}
	}

	private void handleSnapshotAsync(MarketDataSnapshot snapshot) throws Exception {

		// Parse the tick
		IPriceCandle candle = toPriceCandle(snapshot);
		if (candle instanceof ITickPriceCandle) {

			// Resolve the instrument
			IFxcmInstrument instrument = toFxcmInstrument(snapshot.getInstrument());

			// Handle the tick
			if (tickHandler != null) {
				tickHandler.handleTick(instrument, (ITickPriceCandle) candle);
			}
		}
	}

	public void setTickHandler(IFxcmGatewayTickHandler handler) {
		this.tickHandler = Check.notNull("handler", handler);
	}

	public void handleSnapshot(MarketDataSnapshot snapshot) {
		queue.addLast(snapshot);
	}

	public void shutdown() {
		shutdown.set(true);
	}

	public boolean isShutdown() {
		return shutdown.get();
	}

	@Override
	public void close() {
		shutdown();
	}

}
