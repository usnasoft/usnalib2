package it.usna.util.sql;

import java.sql.*;
import java.util.*;

public class Sql {

	private Sql() {}

	/**
	 * Esegue la query richiesta e restituisce il primo dei valori del primo
	 * campo specificato o null se la query non da risultato.
	 * 
	 * @param query la definizione della query
	 * @param cn la conessione al db
	 * @return il risultato
	 * @throws SQLException
	 */
	public static Object executeQuery(final Connection cn, final CharSequence query) throws SQLException {
		Statement st = null;
		try {
			st = cn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			final ResultSet rs = st.executeQuery(query.toString());
			if (rs.next()) {
				return rs.getObject(1);
			} else {
				return null;
			}
		} finally {
			try {
				st.close();
			} catch (Exception ex) {}
		}
	}

	/**
	 * Esegue l'update (o insert, o delete) richiesto.
	 * 
	 * @param query la definizione della query
	 * @param cn la conessione al db
	 * @return il risultato
	 * @throws SQLException
	 */
	public static int executeUpdate(final Connection cn, final CharSequence query) throws SQLException {
		Statement st = null;
		try {
			st = cn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			return st.executeUpdate(query.toString());
		} finally {
			try {
				st.close();
			} catch (Exception ex) {}
		}
	}

	/**
	 * Execute a command (create, alter, ...).
	 * 
	 * @param query the SQL command
	 * @param cn DB connection
	 * @throws SQLException
	 */
	public static void executeCommand(final Connection cn, final CharSequence query) throws SQLException {
		Statement st = null;
		try {
			st = cn.createStatement();
			st.execute(query.toString());
		} finally {
			try {
				st.close();
			} catch (Exception ex) {}
		}
	}

	/**
	 * Esegue la query richiesta e restituisce una List contentente i valori
	 * (del primo campo specificato).
	 * <p>
	 * Non e' efficiente ma puo' essere molto comoda; occorre valutare se
	 * adoperarla di caso in caso.
	 * 
	 * @param query la definizione della query
	 * @param cn la conessione al db
	 * @return la lista dei valori
	 * @throws SQLException
	 */
	public static List<Object> executeListQuery(final Connection cn, final CharSequence query) throws SQLException {
		final List<Object> listaRit = new ArrayList<Object>();
		Statement st = null;
		try {
			st = cn.createStatement();
			ResultSet rs = st.executeQuery(query.toString());
			while (rs.next()) {
				listaRit.add(rs.getObject(1));
			}
		} finally {
			try {
				st.close();
			} catch (Exception ex) {}
		}
		return listaRit;
	}

	/**
	 * Esegue la query richiesta e restituisce una Mappa ordinata contentente i
	 * valori (chiave: primo campo specificato; valore: secondo campo
	 * specificato).
	 * <p>
	 * Non e' efficiente ma puo' essere molto comoda; occorre valutare se
	 * adoperarla di caso in caso.
	 * 
	 * @param query la definizione della query
	 * @param cn la conessione al db
	 * @return la lista dei valori
	 * @throws SQLException
	 */
	public static Map<Object, Object> executeMapQuery(final Connection cn, final CharSequence query) throws SQLException {
		final Map<Object, Object> ris = new LinkedHashMap<Object, Object>();
		Statement st = null;
		try {
			st = cn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			final ResultSet rs = st.executeQuery(query.toString());
			while (rs.next()) {
				ris.put(rs.getObject(1), rs.getObject(2));
			}
			return ris;
		} finally {
			try {
				st.close();
			} catch (Exception ex) {}
		}
	}

	/**
	 * Esegue una insert definita dal nome della tabella e da una mappai cui
	 * valori sono coppie campo/valore.
	 * 
	 * @param cn Connection
	 * @param tab nome della tabella
	 * @param mappaCV mappa campo/valore
	 * @throws SQLException
	 */
	public static void executeInsert(final Connection cn, final CharSequence tab, final Map<Object, Object> mappaCV) throws SQLException {
		// Costruisco un prepared statement e ci metto i valori dopo cosi'
		// non mi devo preoccupare dei diversi formati dei tipi di dato.
		final List<Object> vals = new ArrayList<Object>(mappaCV.size());
		final Iterator<Map.Entry<Object, Object>> it = mappaCV.entrySet().iterator();
		Map.Entry<Object, Object> entry = it.next();
		final StringBuilder campi = new StringBuilder(entry.getKey().toString());
		final StringBuilder valori = new StringBuilder("?");
		vals.add(entry.getValue());
		while (it.hasNext()) {
			entry = it.next();
			campi.append(',').append(entry.getKey());
			valori.append(",?");
			vals.add(entry.getValue());
		}
		final StringBuilder ins = new StringBuilder("Insert Into ");
		ins.append(tab).append(" (").append(campi).append(") Values (").append(valori).append(')');
		PreparedStatement ps = null;
		try {
			ps = cn.prepareStatement(ins.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			for (int i = 0; i < vals.size(); i++) {
				ps.setObject(i + 1, vals.get(i));
			}
			ps.executeUpdate();
		} finally {
			try {
				ps.close();
			} catch (Exception ex) {}
		}
	}

	public static void close(final Statement st) {
		try {
			st.close();
		} catch (Exception e) {}
	}

	public static void close(final Connection cn) {
		try {
			cn.close();
		} catch (Exception e) {}
	}
}
