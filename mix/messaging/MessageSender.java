package messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.logging.Level;

public class MessageSender extends BaseMessageSendReceiver
{
    private MessageProducer producer;

    public MessageSender(String destination) throws NamingException
    {
        super(destination);
    }

    public String SendMessage(Serializable serializableObject)
    {
        try {
            buildConnection();
            producer = session.createProducer(destination);

            Message message = session.createObjectMessage(serializableObject);

            producer.send(message);
            return message.getJMSMessageID();
        }
        catch (JMSException e)
        {
            LOGGER.log(Level.SEVERE, "Something went wrong while sending the message", e);
            return null;
        }
    }
}
