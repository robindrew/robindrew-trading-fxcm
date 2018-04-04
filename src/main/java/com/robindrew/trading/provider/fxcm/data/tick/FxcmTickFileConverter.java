package com.robindrew.trading.provider.fxcm.data.tick;

import static com.robindrew.trading.provider.TradeDataProvider.FXCM;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.robindrew.common.io.Files;
import com.robindrew.common.lang.Args;
import com.robindrew.trading.price.candle.format.pcf.source.file.PcfFileManager;
import com.robindrew.trading.price.tick.IPriceTick;
import com.robindrew.trading.price.tick.format.ptf.source.file.PtfFileStreamSink;
import com.robindrew.trading.price.tick.io.list.sink.IPriceTickListSink;
import com.robindrew.trading.price.tick.io.list.sink.PriceTickListToStreamSink;
import com.robindrew.trading.provider.fxcm.FxcmInstrument;

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
		directory.mkdirs();

		try (IPriceTickListSink sink = new PriceTickListToStreamSink(new PtfFileStreamSink(directory))) {

			// List and sort the files
			List<File> files = Files.listFiles(inputDir, false);
			Collections.sort(files, new YearWeekComparator());

			for (File file : files) {
				log.info("Converting File: {}", file);

				List<IPriceTick> ticks = new FxcmTickFile(file, instrument).readToList();
				sink.putNextTicks(ticks);
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
