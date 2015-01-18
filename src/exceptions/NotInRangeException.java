package exceptions;

/**
 * Exception for integer not in a expected range
 * @author munchmar
 */
public class NotInRangeException extends Exception {
    
    
    public NotInRangeException() {
        super();
    }

    public NotInRangeException(String msg) {
        super(msg);
    }
}