package it.usna.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * <p>Title: AppProperties</p>
 * <p>Advanced Properties management.
 * <p>Copyright (c) 2003</p>
 * <p>Company: USNA</p>
 * @author Antonio Flaccomio
 * @version 1.0
 */
public class AppProperties extends Properties {
	private static final long serialVersionUID = 1L;
	private final String fileName;

	/**
	 * Constructor; read the configuration file
	 * @param fileName String - configuration file name
	 * @throws IOException
	 */
	public AppProperties(final String fileName) {
		this.fileName = fileName;
	}

	public AppProperties() {
		this.fileName = "properties.conf";
	}

	/**
	 * Store properties on a file;
	 * @param alsoEmpty true: save empty properties, false: do not save empty properties
	 * @throws IOException
	 */
	public void store(final boolean alsoEmpty) throws IOException {
		if (size() > 0 || alsoEmpty) {
			store();
		}
	}

	/** Save the properties on the file */
	public void store() throws IOException {
		try (Writer w = Files.newBufferedWriter(Paths.get(fileName), StandardCharsets.UTF_8)) {
			super.store(w, null);
		}
	}

	public void load(final boolean acceptEmpty) throws IOException {
		try (Reader r = Files.newBufferedReader(Paths.get(fileName), StandardCharsets.UTF_8)) {
			super.load(r);
		} catch(FileNotFoundException | NoSuchFileException e) {
			if(acceptEmpty == false) {
				throw e;
			}
		}
	}

	public String getFileName() {
		return fileName;
	}
	
	public boolean changeProperty(final String key, final String value) {
		Object oldVal = super.setProperty(key, value);
		if(value == oldVal) { // null == null
			return false;
		} else {
			return value == null || value.equals(oldVal) == false;
		}
	}

	/**
	 * Read an integer property
	 * @return Property value or defaultValue se la proprieta' non esiste o non e' un valido intero
	 */
	public int getIntProperty(final String key, final int defaultValue) {
		try {
			final String val = getProperty(key);
			return Integer.parseInt(val);
		} catch (RuntimeException e) { // NumberFormatException, NullPointerException
			return defaultValue;
		}
	}

	public int getIntProperty(final String key) {
		try {
			final String val = getProperty(key);
			return Integer.parseInt(val);
		} catch (RuntimeException e) { // NumberFormatException, NullPointerException
			return 0;
		}
	}
	
	public boolean setIntProperty(final String key, final int value) {
		String val = value + "";
		return val.equals(super.setProperty(key, val)) == false;
	}
	
	public boolean getBoolProperty(final String key, final boolean defaultVal) {
		final String val = getProperty(key);
		return val == null ? defaultVal : "true".equals(val);
	}

	public boolean getBoolProperty(final String key) {
		return "true".equals(getProperty(key));
	}

	public boolean setBoolProperty(final String key, final boolean value) {
		String val = value ? "true" : "false";
		return val.equals(super.setProperty(key, val)) == false;
	}

	/** Legge una proprieta', la interpreta come un set di elementi e inserisce gli elementi in un array di String
	 * @param key il nome della chiave
	 * @param sep i separatori che delimitano gli elementi
	 * @return null se la proprieta' non esiste; un array di String altrimenti
	 */
	public String[] getMultipleProperty(final String key, final char sep) {
		final String val = getProperty(key);
		return (val == null) ? null : val.split(sep + "");
	}

	/**
	 * @param key
	 * @param array
	 * @param sep divider
	 */
	public boolean setMultipleProperty(final String key, final String[] array, final char sep) {
		String val = Arrays.stream(array).collect(Collectors.joining(sep + ""));
		return val.equals(super.setProperty(key, val)) == false;
	}
	
//	@Override
//	public Object setProperty(final String key, final String val) {
//		if(val != null) {
//			return super.setProperty(key, val);
//		} else {
//			return null;
//		}
//	}
}