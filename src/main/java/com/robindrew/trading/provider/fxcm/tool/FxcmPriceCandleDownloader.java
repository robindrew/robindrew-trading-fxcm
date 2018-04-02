package com.robindrew.trading.provider.fxcm.tool;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.robindrew.common.http.Http;
import com.robindrew.common.io.Files;

public class FxcmPriceCandleDownloader {

	private static final Logger log = LoggerFactory.getLogger(FxcmPriceCandleDownloader.class);

	private static final Set<String> AVAILABLE_INSTRUMENTS = getAvailableInstruments();

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
		return set;
	}

	public static void main(String[] args) {
		
		for (String instrument : AVAILABLE_INSTRUMENTS) {
			FxcmPriceCandleDownloader downloader = new FxcmPriceCandleDownloader(instrument);

			// Initial date
			LocalDateTime date = LocalDateTime.now();

			while (downloader.downloadYear(date)) {
				date = date.minusYears(1);
			}
		}
	}

	private final String instrument;
	private String interval = "m1";

	public FxcmPriceCandleDownloader(String instrument) {
		this.instrument = instrument;
	}

	public boolean downloadYear(LocalDateTime date) {
		int count = 0;
		int year = date.getYear();
		for (int week = 1; week < 53; week++) {

			File dir = new File("c:/temp/fxcm/" + interval + "/" + instrument);
			dir.mkdirs();
			File file = new File(dir, year + "-" + week + ".csv.gz");
			if (file.exists()) {
				count++;
				continue;
			}

			try {
				String url = createUrl(interval, year, week);

				log.info("Downloading {}", url);
				byte[] content = Http.getBytes(url);
				count++;

				Files.writeFromBytes(file, content);

			} catch (Exception e) {
				log.warn("Failed to download year=" + year + ", week=" + week, e);
				break;
			}
		}
		return count > 0;
	}

	private String createUrl(String interval, int year, int week) {
		StringBuilder url = new StringBuilder();
		url.append("https://candledata.fxcorporate.com/");
		url.append(interval);
		url.append("/");
		url.append(instrument);
		url.append("/");
		url.append(year);
		url.append("/");
		url.append(week);
		url.append(".csv.gz");
		return url.toString();
	}
}
