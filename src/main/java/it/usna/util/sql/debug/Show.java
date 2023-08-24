package it.usna.util.sql.debug;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Show {
	/**
	 * Una rappresentazione dell'oggetto viene mandata sullo standard output
	 * @param o Object
	 */
	public static void show(final Object o) {
		System.out.println(toString(o));
	}

	/**
	 * Ritorna una rappresentazione dell'oggetto convertito in stringa
	 * @param o Object
	 * @return String
	 */
	public static String toString(final Object o) {
		if (o instanceof ResultSet) {
			return resultSetToString((ResultSet) o);
		} else {
			return it.usna.util.debug.Show.toString(o);
		}
	}

	/**
	 * Render full ResultSet; the ResultSet is consumed.
	 * @param rs
	 * @return
	 */
	public static String fullResultSetToString(final ResultSet rs) {
		try {
			String res = resultSetHeader(rs) + "\n";
			final int count = rs.getMetaData().getColumnCount();
			while(rs.next()) {
				for (int i = 1; i <= count; i++) {
					res += rs.getObject(i) + "\t";
				}
				res += "\n";
			}
			return res;
		} catch (SQLException e) {
			return e.toString();
		}
	}

	private static String resultSetHeader(final ResultSet rs) throws SQLException {
		String res = "";
		final ResultSetMetaData rsmd = rs.getMetaData();
		final int count = rsmd.getColumnCount();
		for (int i = 1; i <= count; i++) {
			res += rsmd.getColumnName(i) + "\t";
		}
		return res;
	}
	
	private static String resultSetToString(final ResultSet rs) {
		try {
			String res = resultSetHeader(rs) + "\n";
			final int count = rs.getMetaData().getColumnCount();
			for (int i = 1; i <= count; i++) {
				res += rs.getObject(i) + "\t";
			}
			return res;
		} catch (SQLException e) {
			return e.toString();
		}
	}
}
