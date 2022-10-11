package it.usna.util.sql;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: RecordDuplicator</p>
 * <p>Duplicate a set of records identified by a query.</p>
 * <p>Copyright (c) 2004</p>
 * <p>Company: USNA</p>
 * @author Antonio Flaccomio
 * @version 1.0
 */
public class RecordDuplicator implements Closeable {

  private int columnsNumber;
  private ResultSet selRS;
  private PreparedStatement insStatement;
  private Map<String, Integer> columnsMap;
  //private Map<Integer, Integer> mappaTipi;
  private int columnsTypes[];

  public RecordDuplicator() {}

  public PreparedStatement prepare(final Connection cn, final String table, final String query) throws SQLException {
	  return prepare(cn, cn, table, query);
  }

  public PreparedStatement prepare(final Connection cnIn, final Connection cnOut, final String table, final String query) throws SQLException {
    final Statement selStatement = cnIn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    selRS = selStatement.executeQuery(query);
    final ResultSetMetaData md = selRS.getMetaData();
    columnsNumber = md.getColumnCount();
    columnsMap = new HashMap<String,Integer>();
    columnsTypes = new int[columnsNumber + 1];
    final StringBuilder sb = new StringBuilder("Insert Into ");
    String nomeColonna = md.getColumnName(1);
    sb.append(table).append(" (").append(nomeColonna);
    columnsMap.put(nomeColonna, 1);
    columnsTypes[1] = md.getColumnType(1);
    for (int i = 2; i <= columnsNumber; i++) {
      nomeColonna = md.getColumnName(i);
      sb.append(',').append(nomeColonna);
      columnsMap.put(nomeColonna, i);
      columnsTypes[i] = md.getColumnType(i);
    }
    sb.append(") VALUES (?");
    for (int i = 2; i <= columnsNumber; i++) {
      sb.append(",?");
    }
    sb.append(')');
    insStatement = cnOut.prepareStatement(sb.toString());
    return insStatement;
  }

  public int getColumnIndex(final String nomeColonna) {
    return columnsMap.get(nomeColonna);
  }

  public int getColumnType(final int index) {
	  return columnsTypes[index];
  }

  /**
   * Load next record values into the PreparedStatement
   * @return boolean
   * @throws SQLException
   */
  public boolean next() throws SQLException {
	  if (selRS.next()) {
		  for (int i = 1; i <= columnsNumber; i++) {
			  final Object val = selRS.getObject(i);
			  if(val != null) {
				  insStatement.setObject(i, val);
			  } else {
				  insStatement.setNull(i, columnsTypes[i]);
			  }
		  }
		  return true;
	  }
	  else {
		  return false;
	  }
  }

  /**
   * Release resources
   */
  @Override
  public void close() {
    columnsMap = null;
    try {
      selRS.getStatement().close();
    }
    catch (Exception ex) {}
    try {
      insStatement.close();
    }
    catch (Exception ex) {}
  }
}
