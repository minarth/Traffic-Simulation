package exceptions;

/**
 * Exception for empty line in input file
 * @author munchmar
 */
public class EmptyLineException extends Exception {

    public EmptyLineException() {
        super();
    }

    public EmptyLineException(String msg) {
        super(msg);
    }

    
}
