package fi.helsinki.cs.titotrainer.framework.config;

/**
 * An exception to be thrown when a configuration has
 * an invalid or missing value.
 */
public class InvalidConfigException extends Exception {

    public InvalidConfigException() {
        super();
    }

    public InvalidConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidConfigException(String message) {
        super(message);
    }

    public InvalidConfigException(Throwable cause) {
        super(cause);
    }
    
}
