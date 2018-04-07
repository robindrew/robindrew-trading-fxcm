package com.robindrew.trading.fxcm.line;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.google.common.base.Charsets;
import com.robindrew.common.date.Dates;
import com.robindrew.common.text.tokenizer.CharDelimiters;

public abstract class FxcmLineParser {

	public static DateTimeFormatter DATE_FORMAT = ofPattern("MM/dd/yyyy");
	public static DateTimeFormatter TIME_FORMAT = ofPattern("HH:mm:ss.SSS");

	public static final CharDelimiters DELIMITERS = new CharDelimiters().whitespace().character(',');

	public static long toMillis(LocalDateTime date) {
		return Dates.toMillis(date, ZoneOffset.UTC);
	}

	private final int decimalPlaces;

	protected FxcmLineParser(int decimalPlaces) {
		this.decimalPlaces = decimalPlaces;
	}

	public int getDecimalPlaces() {
		return decimalPlaces;
	}

	public Charset getCharset() {
		return Charsets.UTF_8;
	}

	public boolean skipLine(String line) {
		return line.isEmpty() || line.startsWith("DateTime");
	}

}
