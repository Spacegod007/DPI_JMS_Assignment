package messaging;

import model.StaticNames;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import java.util.logging.Level;

public class MessageReceiver extends BaseMessageCommunicator
{
    private MessageConsumer messageConsumer;

    public MessageReceiver(String destination)
    {
        super(destination);
    }

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
