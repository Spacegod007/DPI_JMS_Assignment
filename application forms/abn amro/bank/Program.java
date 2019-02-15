package bank;

import model.StaticNames;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Program
{
    private static final Logger LOGGER = Logger.getLogger(Program.class.getName());

    /**
     * Launches the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                JMSBankFrame frame = new JMSBankFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, StaticNames.LOGGER_ERROR_APPLICATION_EXECUTION, e);
            }
        });
    }
}
