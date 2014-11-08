package co.mindie.cindy.token;

import co.mindie.cindy.utils.Token;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Random;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class TokenTest {

	////////////////////////
	// VARIABLES
	////////////////

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////

	@Test
	public void token_is_serializable_and_deserializable() {
		int id = new Random().nextInt();
		int bitsEntropy = 2048;

		String key = new Token(id, bitsEntropy).toString();

		Token token = new Token(key);

		assertEquals(token.getId(), id);
		assertEquals(token.toString(), key);
		assertEquals(token.getEntropy(), bitsEntropy);
	}

	@Test
	public void token_detects_invalid_key() {
		int id = new Random().nextInt();
		int bitsEntropy = 512;

		String key = new Token(id, bitsEntropy).toString();

		this.expectedException.expect(IllegalArgumentException.class);

		new Token(key + "w");
	}

	@Test
	public void token_detects_invalid_hashcode() {
		this.expectedException.expect(IllegalArgumentException.class);

		new Token(Token.generateTokenString(4242, 4242, new byte[8]));
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////


}
