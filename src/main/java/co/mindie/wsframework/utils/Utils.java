/////////////////////////////////////////////////
// Project : webservice
// Package : com.ever.webservice.api.utils
// Utils.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 16, 2013 at 12:52:08 PM
////////

package co.mindie.wsframework.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////

	public static <T> boolean arrayContains(T[] array, T object) {
		for (T arrayObject : array) {
			if (arrayObject.equals(object)) {
				return true;
			}
		}

		return false;
	}

	public static <T> List<T> createReversedList(List<T> input) {
		ArrayList<T> reversedList = new ArrayList<T>();

		for (int i = input.size() - 1; i >= 0; i--) {
			reversedList.add(input.get(i));
		}

		return reversedList;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] addItem(T[] array, T object) {
		final T[] newArray = (T[]) Array.newInstance(object.getClass(), array.length + 1);

		System.arraycopy(array, 0, newArray, 0, array.length);
		newArray[array.length] = object;

		return newArray;
	}

	public static String getObjectDescription(Object object) {
		StringBuilder sb = new StringBuilder();

		if (object == null) {
			sb.append("null");
		} else if (object instanceof String) {
			sb.append("{String}:" + (String) object);
		} else if (object instanceof Object[]) {
			Object[] array = (Object[]) object;
			sb.append("[");

			boolean isFirst = true;
			for (Object obj : array) {
				if (!isFirst) {
					sb.append(", ");
				}
				sb.append(getObjectDescription(obj));
				isFirst = false;
			}

			sb.append("]");
		} else {
			sb.append(object.getClass().getSimpleName() + ":" + object.toString());
		}

		return sb.toString();
	}

	public static String randomString(int minSize, int maxSize) {
		final Random random = new Random();

		int stringSize = random.nextInt(maxSize - minSize) + minSize;

		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < stringSize; i++) {
			char nextChar = (char) (random.nextInt((int) '~' - (int) '!') + ('!'));
			sb.append(nextChar);
		}

		return sb.toString();
	}

	public static String firstCharUpperCase(String str) {
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
