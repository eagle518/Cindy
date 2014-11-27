package co.mindie.cindy.webservice.utils;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Random;

public class Token {

	////////////////////////
	// VARIABLES
	////////////////

	private static Random random = new SecureRandom();

	private byte[] randomBytes;
	private int entropy;
	private int id;
	private int randomBytesHashCode;
	private String stringRepresentation;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Token(int id, int entropy) {
		if (entropy % 8 != 0) {
			throw new IllegalArgumentException("The entropy must be a multiple of 8");
		}

		byte[] randomBytes = new byte[entropy / 8];
		random.nextBytes(randomBytes);

		this.randomBytesHashCode = computeHashCode(randomBytes);
		this.randomBytes = randomBytes;
		this.id = id;
		this.entropy = entropy;
		this.stringRepresentation = generateTokenString(id, randomBytesHashCode, randomBytes);
	}

	public Token(String stringRepresentation) {
		byte[] bytes = Base58.decode(stringRepresentation);

		if (bytes.length < 8) {
			throw new IllegalArgumentException("Invalid stringRepresentation");
		}

		ByteBuffer buffer =  ByteBuffer.wrap(bytes);

		this.id =  buffer.getInt();
		this.randomBytesHashCode = buffer.getInt();

		byte[] randomSalt = new byte[bytes.length - 8];
		buffer.get(randomSalt);


		int actualHashCode = computeHashCode(randomSalt);

		if (this.randomBytesHashCode != actualHashCode) {
			throw new IllegalArgumentException("Invalid stringRepresentation (hashcode does not match)");
		}

		this.randomBytes = randomSalt;
		this.stringRepresentation = stringRepresentation;
		this.entropy = (bytes.length - 8) * 8;
	}

	////////////////////////
	// METHODS
	////////////////

	private static int computeHashCode(byte a[]) {
		int result = 1;

		for (int i = 0; i < a.length; i++) {
			result = 31 * result + a[i];
		}

		return result;
	}

	public static String generateTokenString(int id, int randomBytesHashCode, byte[] randomBytes) {
		ByteBuffer buffer = ByteBuffer.allocate(8 + randomBytes.length);
		buffer.putInt(id);
		buffer.putInt(randomBytesHashCode);
		buffer.put(randomBytes);

		return Base58.encode(buffer.array());
	}

	@Override
	public String toString() {
		return this.getTokenString();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public String getTokenString() {
		return this.stringRepresentation;
	}

	public int getId() {
		return id;
	}

	public int getEntropy() {
		return entropy;
	}

	public int getRandomBytesHashCode() {
		return randomBytesHashCode;
	}

	public byte[] getRandomBytes() {
		return randomBytes;
	}
}
