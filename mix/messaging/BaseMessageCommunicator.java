package messaging;

import model.StaticNames;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseMessageCommunicator
{
    protected static final Logger LOGGER = Logger.getLogger(BaseMessageCommunicator.class.getName());

    private String destinationChannelName;
    private String replyDestinationChannelName;

    private Context jndiContext;
    private ConnectionFactory factory;

    Connection connection;
    Session session;

    Destination destination;

    /**
     * Constructs the base communicator
     * @param destination The destination to send/listen
     */
    BaseMessageCommunicator(String destination)
    {
        this(destination, "");
    }

    /**
     * Constructs the base communicator
     * @param destination The destination to send/listen
     * @param replyDestination A reply destination if a reply message is expected
     */
    BaseMessageCommunicator(String destination, String replyDestination)
    {
        destinationChannelName = destination;
        replyDestinationChannelName = replyDestination;

        try
        {
            jndiContext = new InitialContext(buildPropertySet());
            factory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
            ((ActiveMQConnectionFactory) factory).setTrustAllPackages(true);
        }
        catch (NamingException e)
        {
            LOGGER.log(Level.SEVERE, StaticNames.LOGGER_ERROR_BUILDING_CONNECTION);
        }
    }

    /**
     * Builds the set of properties
     * @return The set of build properties containing the required properties
     */
    private Properties buildPropertySet()
    {
        Properties properties = new Properties();
        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        properties.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
        properties.put(("queue." + destinationChannelName), destinationChannelName);

        if (!replyDestinationChannelName.equals(""))
        {
            properties.put(("queue." + replyDestinationChannelName), replyDestinationChannelName);
        }

        return properties;
    }

    /**
     * Builds the requirements for a connection
     */
    void BuildConnection()
    {
        try
        {
            connection = factory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            destination = (Destination) jndiContext.lookup(destinationChannelName);
        }
        catch (JMSException | NamingException e)
        {
            LOGGER.log(Level.SEVERE, StaticNames.LOGGER_ERROR_BUILDING_CONNECTION, e);
        }
    }

    /**
     * Get the reply destination if one has been set
     * @return Returns the reply destination if one is present otherwise null
     */
    Destination GetReplyDestination()
    {
        if (!replyDestinationChannelName.isEmpty())
        {
            try
            {
                return (Destination) jndiContext.lookup(replyDestinationChannelName);
            }
            catch (NamingException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }
}
