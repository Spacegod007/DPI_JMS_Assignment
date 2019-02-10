package messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;

public class MessageSender extends BaseMessageSendReceiver
{
    private MessageProducer producer;

    public MessageSender(String destination) throws NamingException
    {
        super(destination);
    }

    public String SendMessage(Serializable serializableObject, String correlationID)
    {
        try {
            buildConnection();
            producer = session.createProducer(destination);

            Message message = session.createObjectMessage(serializableObject);

            message.setJMSCorrelationID(correlationID);

            producer.send(message);
            return correlationID;
        }
        catch (JMSException e)
        {
            LOGGER.log(Level.SEVERE, "Something went wrong while sending the message", e);
        }
        return null;
    }

    public String SendMessage(Serializable serializableObject)
    {
        String uuid = UUID.randomUUID().toString();
        return SendMessage(serializableObject, uuid);
    }
}
