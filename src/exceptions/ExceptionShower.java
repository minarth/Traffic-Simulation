package exceptions;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Class for showing exception in a dialog
 * @author minarth
 */
public class ExceptionShower {
    public static void showExceptions(Exception ex, JFrame frame) {
        
        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Chyba v souboru", JOptionPane.ERROR_MESSAGE);
    }
}
