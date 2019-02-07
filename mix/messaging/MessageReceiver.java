package messaging;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.naming.NamingException;
import java.util.logging.Level;

public class MessageReceiver extends BaseMessageSendReceiver
{
    private MessageConsumer consumer;

    public MessageReceiver(String destination) throws NamingException
    {
        super(destination);
    }

    public boolean PrepareReceiveMessage(MessageListener messageListener)
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
            return false;
        }

        return true;
    }
}
