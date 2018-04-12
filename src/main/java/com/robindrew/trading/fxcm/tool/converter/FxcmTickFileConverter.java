package com.robindrew.trading.fxcm.tool.converter;

import static com.robindrew.trading.provider.TradeDataProvider.FXCM;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.robindrew.common.io.Files;
import com.robindrew.common.lang.Args;
import com.robindrew.trading.fxcm.FxcmInstrument;
import com.robindrew.trading.fxcm.line.FxcmTickLineParser;
import com.robindrew.trading.price.candle.IPriceCandle;
import com.robindrew.trading.price.candle.format.pcf.source.file.PcfFileManager;
import com.robindrew.trading.price.candle.format.pcf.source.file.PcfFileStreamSink;
import com.robindrew.trading.price.candle.io.list.sink.IPriceCandleListSink;
import com.robindrew.trading.price.candle.io.list.sink.PriceCandleListToStreamSink;
import com.robindrew.trading.price.candle.line.parser.PriceCandleLineFile;

public class FxcmTickFileConverter {

	private static final Logger log = LoggerFactory.getLogger(FxcmTickFileConverter.class);

	public static void main(String[] array) {
		Args args = new Args(array);

		// The input directory containing FXCM Tick files
		File inputDir = args.getDirectory("-i", true);

		// The output directory for writing PCF files
		File outputDir = args.getDirectory("-o", true);

		FxcmTickFileConverter converter = new FxcmTickFileConverter();
		converter.convertInstruments(inputDir, outputDir);
	}

	public void convertInstruments(File inputDir, File outputDir) {
		for (File dir : Files.listFiles(inputDir, false)) {
			convertInstrument(dir, outputDir);
		}
	}

	public void convertInstrument(File inputDir, File outputDir) {

		// Get the instrument
		String name = inputDir.getName();
		FxcmInstrument instrument = FxcmInstrument.valueOf(name);
		log.info("Converting Instrument: {}", instrument);

		// Output directory
		File directory = PcfFileManager.getDirectory(FXCM, instrument, outputDir);
		if (directory.exists()) {
			log.info("Output directory already exists, skipping: {}", directory);
			return;
		}
		directory.mkdirs();

		try (IPriceCandleListSink sink = new PriceCandleListToStreamSink(new PcfFileStreamSink(directory))) {

			// List and sort the files
			List<File> files = Files.listFiles(inputDir, false);
			Collections.sort(files, new YearWeekComparator());

			for (File file : files) {
				log.info("Converting File: {}", file);

				FxcmTickLineParser parser = new FxcmTickLineParser(instrument);
				List<IPriceCandle> ticks = new PriceCandleLineFile(file, parser).toList();
				sink.putNextCandles(ticks);
			}
		}
	}

	private static class YearWeekComparator implements Comparator<File> {

		@Override
		public int compare(File file1, File file2) {
			String name1 = file1.getName();
			String name2 = file2.getName();

			// Compare year
			int year1 = Integer.parseInt(name1.substring(0, 4));
			int year2 = Integer.parseInt(name2.substring(0, 4));
			if (Integer.compare(year1, year2) != 0) {
				return Integer.compare(year1, year2);
			}

			// Compare week
			int week1 = Integer.parseInt(name1.substring(5, name1.indexOf('.')));
			int week2 = Integer.parseInt(name2.substring(5, name2.indexOf('.')));
			return Integer.compare(week1, week2);
		}

	}
}
