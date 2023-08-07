package it.usna.util;

import java.util.ArrayList;

/**
 * CLI arguments manager
 * @author a.flaccomio
 *
 */
public class CLI {
	private String[] args;
	private boolean[] used;

	public CLI(String[] args) {
		this.args = args;
		this.used = new boolean[args.length];
	}
	
	/**
	 * Verify existance of an entry
	 * @param entry name
	 * @return entry index or -1
	 */
	public int hasEntry(String name) {
		for(int i = 0; i < args.length; i++) {
			if(args[i].equals(name)) {
				used[i] = true;
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Verify existance of an entry (accepts synonyms)
	 * @param entry name
	 * @return entry index or -1
	 */
	public int hasEntry(String ... synonyms) {
		for(String name: synonyms) {
			int idx = hasEntry(name);
			if(idx >= 0) {
				return idx;
			}
		}
		return -1;
	}
	
	/**
	 * get the string following given entry
	 * @param entryIndex
	 * @return
	 */
	public String getParameter(int entryIndex) {
		if(args.length > entryIndex + 1) {
			used[entryIndex + 1] = true;
			return args[entryIndex + 1];
		} else {
			return null;
		}
	}
	
	/**
	 * get the parIndex-th (base is 0) string following given entry
	 * @param entryIndex
	 * @return
	 */
	public String getParameter(int entryIndex, int parIndex) {
		if(args.length > entryIndex + parIndex + 1) {
			used[entryIndex + parIndex + 1] = true;
			return args[entryIndex + parIndex + 1];
		} else {
			return null;
		}
	}
	
	/**
	 * return an array of unused (no hasEntry(...) or getParameter(..)) entries
	 * @return
	 */
	public String[] unused() {
		ArrayList<String> u = new ArrayList<>();
		for(int i = 0; i < args.length; i++) {
			if(used[i] == false) {
				u.add(args[i]);
			}
		}
		return u.toArray(new String[u.size()]);
	}
	
	public String get(int idx) {
		return args[idx];
	}
	
	public int size() {
		return args.length;
	}
}