package loanclient;

import model.StaticNames;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Program
{
    private static final Logger LOGGER = Logger.getLogger(Program.class.getName());

    /**
     * Launches the application
     * @param args
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LoanClientFrame frame = new LoanClientFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, StaticNames.LOGGER_ERROR_APPLICATION_EXECUTION, e);
            }
        });
    }
}
