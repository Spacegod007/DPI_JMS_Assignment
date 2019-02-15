package messaging;

import model.StaticNames;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import java.io.Serializable;
import java.util.logging.Level;

public class MessageSender extends BaseMessageCommunicator
{
    private MessageProducer messageProducer;

    public MessageSender(String destination)
    {
        super(destination);
    }

    public MessageSender(String destination, String replyDestination)
    {
        super(destination, replyDestination);
    }

    public String SendMessage(Serializable serializableObject)
    {
        return SendMessage(serializableObject, "");
    }

    public String SendMessage(Serializable serializableObject, String correlationId)
    {
        BuildConnection();

        try
        {
            messageProducer = session.createProducer(destination);
            Message message = session.createObjectMessage(serializableObject);

            message.setJMSCorrelationID(correlationId);

            Destination replyDestination = GetReplyDestination();
            if (replyDestination != null)
            {
                message.setJMSReplyTo(replyDestination);
            }

            messageProducer.send(message);
            return message.getJMSMessageID();
        }
        catch (JMSException e)
        {
            LOGGER.log(Level.SEVERE, StaticNames.LOGGER_ERROR_SENDING_MESSAGE);
        }

        return null;
    }
}
