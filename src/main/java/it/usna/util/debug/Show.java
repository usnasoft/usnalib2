package it.usna.util.debug;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>Varoius objects rendering</p>
 * <p>Copyright (c) 2003</p>
 * <p>Company: USNA</p>
 * @author Antonio Flaccomio
 * @version 1.0
 */
public class Show {
	/**
	 * Object representation on standard input standard output
	 * @param o Object
	 */
	public static void show(final Object o) {
		System.out.println(toString(o));
	}

	/**
	 * Object representation as String
	 * @param o Object
	 * @return String
	 */
	public static String toString(final Object o) {
		if (o == null) {
			return ">null<";
			/*
			 * } else if (o instanceof HttpServletRequest) { // Oggetti j2ee
			 * return requestToString((HttpServletRequest) o); } else if (o
			 * instanceof ServletRequest) { return
			 * requestToString((ServletRequest) o); } else if (o instanceof
			 * ServletContext) { return contextToString((ServletContext) o);
			 */
		} else if (o instanceof Enumeration) { // Altri oggetti
			return enumerationToString((Enumeration<?>) o);
		} else if (o instanceof Iterator) {
			return iteratorToString((Iterator<?>) o);
		} /*else if (o.getClass().getComponentType() != null) {
			return arrayToString(o);
		}*/ else if (o instanceof Collection) {
			return collectionToString((Collection<?>) o);
		} else if (o instanceof Map) {
			return mapToString((Map<?, ?>) o);
		} else if (o instanceof Class) {
			return classToString((Class<?>) o);
		} else {
			return o.toString();
		}
	}

	/*
	 * private static String requestToString(final ServletRequest req) {
	 * Enumeration en = req.getAttributeNames(); String res = "Attributi:\n";
	 * while (en.hasMoreElements()) { final String name = (String)
	 * en.nextElement(); res += name + " = " + req.getAttribute(name) + " \n"; }
	 * en = req.getParameterNames(); res += "Parametri:\n"; while
	 * (en.hasMoreElements()) { final String name = (String) en.nextElement();
	 * res += name + " = " + arrayToString(req.getParameterValues(name)) + "
	 * \n"; } return res; }
	 * 
	 * private static String requestToString(final HttpServletRequest req) {
	 * return req.getRequestURI() + " \n" + requestToString((ServletRequest)
	 * req); }
	 * 
	 * private static String contextToString(final ServletContext context) {
	 * final Enumeration en = context.getAttributeNames(); String res =
	 * "Attributi:\n"; while (en.hasMoreElements()) { final String name =
	 * (String) en.nextElement(); res += name + " = " +
	 * context.getAttribute(name) + " \n"; } return res; }
	 */

	private static String enumerationToString(final Enumeration<?> en) {
		String res = "";
		while (en.hasMoreElements()) {
			res += en.nextElement().toString() + " \n";
		}
		return res;
	}

	private static String iteratorToString(final Iterator<?> it) {
		String res = "";
		while (it.hasNext()) {
			res += toString(it.next()) + " \n";
		}
		return res;
	}

	private static String mapToString(final Map<?, ?> map) {
		String res = "";
		final Iterator<?> it = map.entrySet().iterator();
		while (it.hasNext()) {
			final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) it.next();
			res += entry.getKey() + " = " + entry.getValue() + " \n";
		}
		return res;
	}

	private static String collectionToString(final Collection<?> coll) {
		return iteratorToString(coll.iterator());
	}

	/*private static String arrayToString(final Object array) {
		String res = "";
		for (int i = 0; i < Array.getLength(array); i++) {
			res += Array.get(array, i) + ", ";
		}
		return res + "[" + Array.getLength(array) + "]";
	}*/

	private static String classToString(final Class<?> cl) {
		String res = cl + " \nCampi: \n";
		final Field f[] = cl.getDeclaredFields();
		for (int i = 0; i < f.length; i++) {
			res += f[i];
			try {
				final Object val = f[i].get(null);
				res += " = " + val;
			} catch (Exception ex) {}
			res += " \n";
		}
		res += "Metodi: \n";
		final Method m[] = cl.getDeclaredMethods();
		for (int i = 0; i < m.length; i++) {
			res += m[i] + " \n";
		}
		return res;
	}
}
