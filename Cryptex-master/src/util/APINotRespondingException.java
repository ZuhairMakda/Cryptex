package util;

/**
 * An exception that is thrown if the API does not respond
 * @author Somar Aani
 *
 */
public class APINotRespondingException extends Exception {
	
	public APINotRespondingException() {
		super();
	}

	public APINotRespondingException(Throwable t) {
		super(t);
	}
	
	public APINotRespondingException(String error) {
		super(error);
	}

}
