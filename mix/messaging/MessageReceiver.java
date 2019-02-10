package messaging;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.naming.NamingException;
import java.util.logging.Level;

/**
 * A class used to receive messages from different systems
 */
public class MessageReceiver extends BaseMessageSendReceiver
{
    private MessageConsumer consumer;

    /**
     * Constructs a new Message Receiver object
     * @param destination the destination/channel over which messages will be received
     * @throws NamingException
     */
    public MessageReceiver(String destination) throws NamingException
    {
        super(destination);
    }

    /**
     * Prepares the system to receive messages
     * @param messageListener The listener object which will be fired when a message is received
     */
    public void PrepareReceiveMessage(MessageListener messageListener)
    {
        try
        {
            buildConnection();
            consumer = session.createConsumer(destination);
            connection.start();

            consumer.setMessageListener(messageListener);
        }
        catch (JMSException e)
        {
            LOGGER.log(Level.SEVERE, "Something went wrong while receiving the message", e);
        }
    }
}
