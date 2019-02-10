package messaging;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A base class which contains values required for both sending and receiving messages
 */
class BaseMessageSendReceiver
{
    static final Logger LOGGER = Logger.getLogger(BaseMessageSendReceiver.class.getName());

    final Context jndiContext;
    private final ConnectionFactory factory;
    private final String destinationString;

    Connection connection;
    Session session;
    Destination destination;

    /**
     * Constructs the class
     * @param destination The destination/channel over which messages will be transported
     * @throws NamingException
     */
    BaseMessageSendReceiver(String destination) throws NamingException
    {
        destinationString = destination;
        Properties properties = buildPropertySet();

        jndiContext = new InitialContext(properties);
        factory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");

        ((ActiveMQConnectionFactory) factory).setTrustAllPackages(true);
    }

    /**
     * Builds a set of properties required to create a context for the messages
     * @return Returns a new Properties object
     */
    private Properties buildPropertySet()
    {
        Properties properties = new Properties();
        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        properties.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
        properties.put(("queue." + destinationString), destinationString);

        return properties;
    }

    /**
     * Builds the connection required for both sending and receiving messages
     * @throws JMSException
     */
    void buildConnection() throws JMSException
    {
        connection = factory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        try
        {
            destination = (Destination) jndiContext.lookup(destinationString);
        } catch (NamingException e)
        {
            LOGGER.log(Level.SEVERE, "Destination lookup failed", e);
        }
    }
}
