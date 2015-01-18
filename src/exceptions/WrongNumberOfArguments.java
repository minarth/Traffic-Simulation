package exceptions;

/**
 * 
 * @author minarth
 */
public class WrongNumberOfArguments extends Exception {
    /**
     * Creates a new instance of
     * <code>WrongNumberOfArguments</code> without detail message.
     */
    public WrongNumberOfArguments() {
        super();
    }

    /**
     * Constructs an instance of
     * <code>WrongNumberOfArguments</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public WrongNumberOfArguments(String msg) {
        super(msg);
    }

}
