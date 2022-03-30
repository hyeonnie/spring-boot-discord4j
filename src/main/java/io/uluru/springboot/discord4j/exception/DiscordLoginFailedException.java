package io.uluru.springboot.discord4j.exception;

public class DiscordLoginFailedException extends RuntimeException {

	private static final long serialVersionUID = -3511032226398232763L;

	public DiscordLoginFailedException() {
        super();
    }
	
	public DiscordLoginFailedException(String message) {
        super(message);
    }
	
	public DiscordLoginFailedException(String message, Throwable cause) {
        super(message, cause);
    }
	
	public DiscordLoginFailedException(Throwable cause) {
        super(cause);
    }

}
