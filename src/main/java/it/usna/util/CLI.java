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
	 * Verify existance of an entry (and mark it as used)
	 * @param entry name
	 * @return entry index or -1
	 */
	public int hasEntry(String name) {
		int idx = indexOf(name);
		if(idx >= 0) {
			used[idx] = true;
			return idx;
		}
		return -1;
	}
	
	/**
	 * Verify existance of an entry - accepts synonyms (and mark it as used)
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
	
	public int rejectEntry(String name) {
		int ind = indexOf(name);
		if(ind >= 0) {
			used[ind] = false;
		}
		return ind;
	}
	
	public int rejectEntry(String ... synonyms) {
		for(String name: synonyms) {
			int idx = rejectEntry(name);
			if(idx >= 0) {
				return idx;
			}
		}
		return -1;
	}
	
	/**
	 * index of "name"
	 * @param entry name
	 * @return entry index or -1
	 */
	private int indexOf(String name) {
		for(int i = 0; i < args.length; i++) {
			if(args[i].equals(name)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * get the string following given entry (and mark it as used)
	 * @param entryIndex
	 * @return parameter value, null if parameter does not exists
	 */
	public String getParameter(int entryIndex) {
		if(args.length > entryIndex + 1 && used[entryIndex + 1] == false) {
			used[entryIndex + 1] = true;
			return args[entryIndex + 1];
		} else {
			return null;
		}
	}
	
	/**
	 * get the parIndex-th (base is 0) string following given entry
	 * @param entryIndex
	 * @param parIndex; 0 for first parameter
	 * @return parameter value, null if parameter doues not exists
	 */
	public String getParameter(int entryIndex, int parIndex) {
		int idx = entryIndex + parIndex + 1;
		if(args.length > idx && used[idx] == false) {
			used[idx] = true;
			return args[idx];
		} else {
			return null;
		}
	}
	
	public void rejectParameter(int entryIndex) {
		used[entryIndex + 1] = false;
	}
	
	public void rejectParameter(int entryIndex, int parIndex) {
		used[entryIndex + parIndex + 1] = false;
	}
	
	/**
	 * Get unused entries
	 * @return an array of unused (no call to hasEntry(...) or getParameter(..))
	 * <pre>
	 * if(cli.unused().length > 0) {
	 *	System.err.println("Wrong parameter(s): " + String.join("; ", cli.unused()));
	 *	System.exit(1);
	 * }
	 * </pre>
	 */
	public String[] unused() {
		ArrayList<String> u = new ArrayList<>();
		for(int i = 0; i < args.length; i++) {
			if(used[i] == false) {
				u.add(args[i]);
			}
		}
		return u.toArray(/*String[]::new*/new String[u.size()]);
	}
	
	public String get(int idx) {
		return args[idx];
	}
	
	public int size() {
		return args.length;
	}
}