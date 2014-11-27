/////////////////////////////////////////////////
// Project : Ever WebService
// Package : com.ever.wsframework.utils
// ID.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Sep 18, 2013 at 2:01:00 PM
////////

package co.mindie.cindy.webservice.utils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ID {

	////////////////////////
	// VARIABLES
	////////////////

	private final static ID instance = new ID();
	private static char[] allowedChars;
	private Random random;

	static {
		List<Character> characters = new ArrayList<>();

		for (char c = 'a'; c <= 'z'; c++) {
			characters.add(c);
		}

		for (char c = 'A'; c <= 'Z'; c++) {
			characters.add(c);
		}
		for (char c = '0'; c <= '9'; c++) {
			characters.add(c);
		}
		allowedChars = new char[characters.size()];

		for (int i = 0; i < characters.size(); i++) {
			allowedChars[i] = characters.get(i);
		}
	}

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ID() {
		this.random = new Random(DateTime.now().getMillis());
	}

	////////////////////////
	// METHODS
	////////////////

	public static String generateID(int size) {
		return instance.nextID(size);
	}

	public String nextID(int size) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < size; i++) {
			int index = this.random.nextInt(allowedChars.length);

			sb.append(allowedChars[index]);
		}

		return sb.toString();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
