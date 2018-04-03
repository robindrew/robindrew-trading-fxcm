package com.robindrew.trading.provider.fxcm.data.tick;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.robindrew.common.io.Files;
import com.robindrew.trading.provider.fxcm.FxcmInstrument;

public class FxcmTickFileConverter {

	private static final Logger log = LoggerFactory.getLogger(FxcmTickFileConverter.class);

	public static void main(String[] args) {

		File inputDir = new File("C:\\development\\data\\FXCM\\tick\\");
		File outputDir = new File("C:\\development\\data\\FXCM\\pif\\");

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

		for (File file : Files.listFiles(inputDir, false)) {
			log.info("Converting File: {}", file);
			
			convertInstrument(instrument, file, outputDir);
		}
	}

	public void convertInstrument(FxcmInstrument instrument, File file, File outputDir) {
		
	}

}
