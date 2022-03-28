package io.uluru.springboot.discord4j.exception;

public class MissingTokenConfiguration extends RuntimeException {

	private static final long serialVersionUID = -3511032226398232763L;

	public MissingTokenConfiguration() {
        super();
    }
	
	public MissingTokenConfiguration(String message) {
        super(message);
    }
	
	public MissingTokenConfiguration(String message, Throwable cause) {
        super(message, cause);
    }
	
	public MissingTokenConfiguration(Throwable cause) {
        super(cause);
    }
	
}
