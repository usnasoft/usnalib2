package it.usna.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
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
 * @version 1.2
 */
public class AppProperties extends Properties {
	private static final long serialVersionUID = 1L;
	private final Path file;
	
	/**
	 * Constructor; read the configuration file
	 * @param file Path - configuration file
	 * @since 1.2
	 */
	public AppProperties(final Path file) {
		this.file = file;
	}

	/**
	 * Constructor; read the configuration file
	 * @param fileName String - configuration file name
	 */
	public AppProperties(final String fileName) {
		this.file = Paths.get(fileName);
	}

	public AppProperties() {
		this.file = Paths.get("properties.conf");
	}

	/**
	 * Save the properties
	 * @param alsoEmpty true: save empty properties, false: do not save empty properties
	 * @throws IOException
	 */
	public void store(final boolean alsoEmpty) throws IOException {
		if (size() > 0 || alsoEmpty) {
			store();
		}
	}

	/** Save the properties
	 * @throws IOException
	 */
	public void store() throws IOException {
		try (Writer w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
			super.store(w, null);
		}
	}

	public void load(final boolean acceptEmpty) throws IOException {
		try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			super.load(r);
		} catch(FileNotFoundException | NoSuchFileException e) {
			if(acceptEmpty == false) {
				throw e;
			}
		}
	}

	public String getFileName() {
		return file.toString();
	}
	
	public Path getFile() {
		return file;
	}
	
	public boolean changeProperty(final String key, final String value) {
		Object oldVal = setProperty(key, value);
		if(value == oldVal) { // null == null
			return false;
		} else {
			return value == null || value.equals(oldVal) == false;
		}
	}
	
	public void defaultProperty(final String key, final String value) {
		if(containsKey(key) == false) {
			put(key, value);
		}
	}

	/**
	 * Read an integer property
	 * @return Property value or defaultValue se la proprieta' non esiste o non e' un valido intero
	 */
	public int getIntProperty(final String key, final int defaultValue) {
		try {
			return Integer.parseInt(getProperty(key));
		} catch (RuntimeException e) { // NumberFormatException, NullPointerException
			return defaultValue;
		}
	}

	public int getIntProperty(final String key) {
		try {
			return Integer.parseInt(getProperty(key));
		} catch (RuntimeException e) { // NumberFormatException, NullPointerException
			return 0;
		}
	}
	
	public boolean setIntProperty(final String key, final int value) {
		String val = value + "";
		return val.equals(setProperty(key, val)) == false;
	}
	
	public void defaultIntProperty(final String key, final int value) {
		if(containsKey(key) == false) {
			put(key, value + "");
		}
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
		return val.equals(setProperty(key, val)) == false;
	}
	
	public void defaultBoolProperty(final String key, final boolean value) {
		if(containsKey(key) == false) {
			put(key, value ? "true" : "false");
		}
	}

	/** Return a String array from a single parameter using "sep" to split the value
	 * @param key the property key
	 * @param sep the separator char
	 * @return the Strings array or null if the property does not exists
	 */
	public String[] getMultipleProperty(final String key, final char sep) {
		final String val = getProperty(key);
		return (val == null) ? null : val.split(sep + "");
	}

	/**
	 * Set a Strings array as a single property entry
	 * @param key the property key
	 * @param array
	 * @param sep the separator char
	 */
	public boolean setMultipleProperty(final String key, final String[] array, final char sep) {
		String val = Arrays.stream(array).collect(Collectors.joining(sep + ""));
		return val.equals(setProperty(key, val)) == false;
	}
}