package com.robindrew.trading.fxcm.tool.downloader;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.robindrew.common.http.HttpResponseException;

public abstract class FxcmDownloader {

	private static final Logger log = LoggerFactory.getLogger(FxcmDownloader.class);

	public static final Set<String> AVAILABLE_INSTRUMENTS = getAvailableInstruments();

	private static Set<String> getAvailableInstruments() {
		Set<String> set = new TreeSet<>();
		set.add("AUDCAD");
		set.add("AUDCHF");
		set.add("AUDJPY");
		set.add("AUDNZD");
		set.add("CADCHF");
		set.add("EURAUD");
		set.add("EURCHF");
		set.add("EURGBP");
		set.add("EURJPY");
		set.add("EURUSD");
		set.add("GBPCHF");
		set.add("GBPJPY");
		set.add("GBPNZD");
		set.add("GBPUSD");
		set.add("NZDCAD");
		set.add("NZDCHF");
		set.add("NZDJPY");
		set.add("NZDUSD");
		set.add("USDCAD");
		set.add("USDCHF");
		set.add("USDJPY");
		return ImmutableSet.copyOf(set);
	}

	private final File outputDirectory;

	public FxcmDownloader(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public File getOutputDirectory() {
		return outputDirectory;
	}

	public void downloadAll(String instrument) {

		// Initial date
		LocalDateTime date = LocalDateTime.now();

		while (downloadYear(instrument, date)) {
			date = date.minusYears(1);
		}
	}

	public boolean downloadYear(String instrument, LocalDateTime date) {
		int count = 0;
		int year = date.getYear();
		for (int week = 1; week <= 53; week++) {

			File file = getOutputFile(instrument, year, week);
			if (file.exists()) {
				count++;
				continue;
			}

			try {
				if (downloadToFile(instrument, year, week, file)) {
					count++;
				} else {
					if (count > 0) {
						break;
					}
				}
			} catch (HttpResponseException hre) {
				int statusCode = hre.getStatusCode();
				if (statusCode == 404) {
					log.warn("File Not Found - Skipping download (year=" + year + ", week=" + week + ")");
					if (count > 0) {
						break;
					}
				} else {
					log.warn("Failed to download (year=" + year + ", week=" + week + ")", hre);
				}
			} catch (Exception e) {
				log.warn("Failed to download year=" + year + ", week=" + week, e);
				break;
			}
		}
		return count > 0;
	}

	protected abstract boolean downloadToFile(String instrument, int year, int week, File file);

	protected abstract File getOutputFile(String instrument, int year, int week);
}
