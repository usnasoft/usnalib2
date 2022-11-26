package it.usna.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * <p>Title: AppProperties</p>
 * <p>File management utilities.
 * <p>Copyright (c) 2009</p>
 * <p>Company: USNA</p>
 * @author Antonio Flaccomio
 * @version 1.0
 */
public class IOFile {
	private final static String lineSeparator = System.getProperty("line.separator");
	private final static int MAX_EXTENSION_LEN = 6;
	
	public static void fileCopy(final String srFile, final String dtFile) throws IOException {
		final File f1 = new File(srFile);
		final File f2 = new File(dtFile);
		fileCopy(f1, f2);
	}
	
	public static void fileCopy(final File srFile, final File dtFile) throws IOException {
		try (
				InputStream in = new BufferedInputStream(new FileInputStream(srFile));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(dtFile))) {
			final byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		}
	}
	
	/**
	 * Read a text file and return its content in a string.
	 * Use carefully; a file usually should not be read in a single big string.
	 * @param source file
	 * @return the file content
	 * @throws IOException
	 */
	public static String readFile(final File srFile) throws IOException {
//		String ret = "";
//		try (BufferedReader r = new BufferedReader(new FileReader(srFile))) {
//			String line;
//			while((line = r.readLine()) != null) {
//				ret += line + '\n';
//			}
//		}
//		return ret;
		return readFile(srFile.toPath());
	}
	
	public static String readFile(final Path srFile) throws IOException {
		return Files.newBufferedReader(srFile).lines().collect(Collectors.joining("\n"));
//		String ret = "";
//		try(BufferedReader r = Files.newBufferedReader(srFile)) {
//			String line;
//			while((line = r.readLine()) != null) {
//				ret += line + '\n';
//			}
//		}
//		return ret;
	}
	
	public static void writeFile(final File dtFile, final String txt) throws IOException {
		writeFile(dtFile.toPath(), txt);
	}
	
	public static void writeFile(final Path dtFile, final String txt) throws IOException {
		try(BufferedWriter writer = Files.newBufferedWriter(dtFile)) {
			writer.write(txt.replace("\n", lineSeparator)); // SO specific
		}
	}

	/**
	 * Given a file name return the file extension. Multiple extensions
	 * (eg.: xxx.pdf.p7m) are managed as described below:
	 * extension is the sum of at most 2 extensions; max extension length (for each extension) is defined.
	 */
	public static String getExtension(final String fileName) {
		if (fileName.length() > 0) {
			final int ultimoDot = fileName.lastIndexOf('.');
			if (ultimoDot > 0 && fileName.length() - ultimoDot <= MAX_EXTENSION_LEN + 1) {
				// Ok, e' un'estensione valida.
				final int penultimoDot = fileName.lastIndexOf('.', ultimoDot - 1);
				if (penultimoDot > 0 && ultimoDot - penultimoDot <= MAX_EXTENSION_LEN + 1) {
					return fileName.substring(penultimoDot + 1);
				} else {
					return fileName.substring(ultimoDot + 1);
				}
			}
		}
		return "";
	}
}