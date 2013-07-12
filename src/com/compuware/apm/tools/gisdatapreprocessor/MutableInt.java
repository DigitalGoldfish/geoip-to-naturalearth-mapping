package com.compuware.apm.tools.gisdatapreprocessor;

/**
 * Small utility class to implement an efficient way to increment values
 * stored in a map. Reduces object churn in comparison to the use of immutable
 * integers.
 *
 * see http://stackoverflow.com/questions/81346
 * 				/most-efficient-way-to-increment-a-map-value-in-java
 */
public class MutableInt {
	private int value = 1; // note that we start at 1 since we're counting

	public void increment () {
		++value;
	}

	public int get() {
		return value;
	}
}