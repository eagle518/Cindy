/////////////////////////////////////////////////
// Project : Ever WebService
// Package : com.ever.wsframework.utils
// StringUtils.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 2, 2013 at 1:40:18 PM
////////

package co.mindie.wsframework.utils;

import org.joda.time.DateTime;

import java.util.Random;

public class StringUtils {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////

	private static final String allowedChars = "azertyuiopqsdfghjklmwxcvbnAZERTYUIOPQSDFGHJKLMWXCVBN0123456789";

	public static String randomAlphaNumericString(int size) {
		char[] randomStr = new char[size];

		Random random = new Random(DateTime.now().getMillis());

		for (int i = 0; i < size; i++) {
			randomStr[i] = allowedChars.charAt(random.nextInt(allowedChars.length()));
		}

		return new String(randomStr);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
