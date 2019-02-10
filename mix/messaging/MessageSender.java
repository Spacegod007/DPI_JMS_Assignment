package messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;

/**
 * A class used to send messages to different systems
 */
public class MessageSender extends BaseMessageSendReceiver
{
    private MessageProducer producer;

    /**
     * Constructs the MessageSender object
     * @param destination The destination/channel to which messages will be send
     * @throws NamingException
     */
    public MessageSender(String destination) throws NamingException
    {
        super(destination);
    }

    /**
     * Sends a message
     * @param serializableObject The object to send
     * @param correlationID An id to refer to the message at a later date
     * @return Returns the correlation Id
     */
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

    /**
     * Sends a message
     * @param serializableObject The object to send
     * @return Returns an Id to refer to the message at a later date
     */
    public String SendMessage(Serializable serializableObject)
    {
        String uuid = UUID.randomUUID().toString();
        return SendMessage(serializableObject, uuid);
    }
}
