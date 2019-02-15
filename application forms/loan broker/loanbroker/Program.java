package loanbroker;

import model.StaticNames;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Program
{
    private static final Logger LOGGER = Logger.getLogger(Program.class.getName());

    /**
     * Loaunches the application
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LoanBrokerFrame frame = new LoanBrokerFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, StaticNames.LOGGER_ERROR_APPLICATION_EXECUTION, e);
            }
        });
    }
}
