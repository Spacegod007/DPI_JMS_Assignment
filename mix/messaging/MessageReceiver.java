package messaging;

import model.StaticNames;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import java.util.logging.Level;

/**
 * Receives messages over the given destination
 */
public class MessageReceiver extends BaseMessageCommunicator
{
    private MessageConsumer messageConsumer;

    /**
     * Constructs the object
     * @param destination The channel to receive messages over
     */
    public MessageReceiver(String destination)
    {
        super(destination);
    }

    /**
     * Prepares to receive messages
     * @param messageListener The listener event to trigger when a message is received
     */
    public void PrepareReceiveMessage(MessageListener messageListener)
    {
        BuildConnection();

        try
        {
            messageConsumer = session.createConsumer(destination);
            connection.start();

            messageConsumer.setMessageListener(messageListener);
        }
        catch (JMSException e)
        {
            LOGGER.log(Level.SEVERE, StaticNames.LOGGER_ERROR_PREPARING_TO_RECEIVE_MESSAGES);
        }
    }
}
