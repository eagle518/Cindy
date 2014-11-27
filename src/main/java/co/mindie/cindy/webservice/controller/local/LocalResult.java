package co.mindie.cindy.webservice.controller.local;

/**
 * Created by simoncorsin on 17/11/14.
 */
public class LocalResult<T, T2> {

	////////////////////////
	// VARIABLES
	////////////////

	private T response;
	private T2 error;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public LocalResult(T response, T2 error) {
		this.response = response;
		this.error = error;
	}

	////////////////////////
	// METHODS
	////////////////


	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public T getResponse() {
		return response;
	}

	public void setResponse(T response) {
		this.response = response;
	}

	public T2 getError() {
		return error;
	}

	public void setError(T2 error) {
		this.error = error;
	}

	public boolean isSuccess() {
		return this.response != null;
	}
}
