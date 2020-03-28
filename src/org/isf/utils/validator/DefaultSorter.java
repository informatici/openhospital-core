package org.isf.utils.validator;

import java.util.Comparator;

/**
 * DefaulSorter (implements {@Link Comparator}) in order to sort
 * by specifying a default element that will be on top
 * @author Nanni
 */
public class DefaultSorter implements Comparator<String> {

	private String defaultValue;

	/**
	 * DefaulSorter (implements {@Link Comparator})
	 * @param defaultValue - the default element that will be on top
	 */
	public DefaultSorter(String defaultValue) {
		super();
		this.defaultValue = defaultValue;
	}

	@Override
	public int compare(String o1, String o2) {
		if (o1.compareTo(defaultValue) == 0) {
			return -1;
		} else if (o2.compareTo(defaultValue) == 0)
			return 1;
		return o1.compareTo(o2);
	}
}