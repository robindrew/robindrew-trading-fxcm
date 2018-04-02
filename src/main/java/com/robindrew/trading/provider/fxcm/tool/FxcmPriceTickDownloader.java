package com.robindrew.trading.provider.fxcm.tool;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.robindrew.common.http.Http;
import com.robindrew.common.io.Files;
import com.robindrew.common.lang.Args;

public class FxcmPriceTickDownloader extends FxcmDownloader {

	private static final Logger log = LoggerFactory.getLogger(FxcmPriceTickDownloader.class);

	public static void main(String[] array) {
		Args args = new Args(array);

		File outputDirectory = args.getDirectory("-d", true);

		for (String instrument : AVAILABLE_INSTRUMENTS) {
			FxcmPriceTickDownloader downloader = new FxcmPriceTickDownloader(outputDirectory);
			downloader.downloadAll(instrument);
		}
	}

	private String interval = "tick";

	public FxcmPriceTickDownloader(File outputDirectory) {
		super(outputDirectory);
	}

	protected boolean downloadToFile(String instrument, int year, int week, File file) {
		String url = createUrl(instrument, year, week);

		log.info("Downloading {}", url);
		byte[] content = Http.getBytes(url);

		Files.writeFromBytes(file, content);
		return true;
	}

	protected File getOutputFile(String instrument, int year, int week) {
		File dir = new File(getOutputDirectory(), interval + "/" + instrument);
		dir.mkdirs();
		File file = new File(dir, year + "-" + week + ".csv.gz");
		return file;
	}

	private String createUrl(String instrument, int year, int week) {
		StringBuilder url = new StringBuilder();
		url.append("https://tickdata.fxcorporate.com/");
		url.append(instrument);
		url.append("/");
		url.append(year);
		url.append("/");
		url.append(week);
		url.append(".csv.gz");
		return url.toString();
	}
}
