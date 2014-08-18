package co.mindie.wsframework.utils;
/////////////////////////////////////////////////
// Project : webservice
// Package : com.ever.wsframework.utils
// Password.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Mar 6, 2013 at 1:00:01 PM
////////

import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class Password {

	////////////////////////
	// VARIABLES
	////////////////

	public static final String ALGORITHM = "PBKDF2WithHmacSHA1";
	public static final int ITERATION_COUNT = 512;
	public static final int KEY_SIZE = 256;
	public static final int SALT_LENGTH = 32;

	final private String hash;
	final private byte[] salt;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Password(String password) {
		final byte[] saltBytes = generateSalt();

		this.salt = saltBytes;
		this.hash = encode(password, saltBytes);
	}

	public Password(String encoded, String salt) {
		this.hash = encoded;
		this.salt = Base64.decodeBase64(salt.getBytes());
	}

	////////////////////////
	// METHODS
	////////////////

	private static byte[] generateSalt() {
		byte[] salt = new byte[SALT_LENGTH];
		SecureRandom random = new SecureRandom();
		random.nextBytes(salt);
		return salt;
	}

	private static String encode(String password, byte[] salt) {
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_SIZE);
		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance(ALGORITHM);
			byte[] encoded = factory.generateSecret(spec).getEncoded();

			return new String(Base64.encodeBase64(encoded));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public boolean matches(String password) {
		final String otherHash = encode(password, this.salt);

		return this.hash.equals(otherHash);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public String getHash() {
		return hash;
	}

	public String getSaltAsString() {
		return new String(Base64.encodeBase64(this.salt));
	}
}
