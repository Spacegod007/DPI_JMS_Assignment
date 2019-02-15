package messaging;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Logger;

public abstract class BaseMessageCommunicator
{
    protected static final Logger LOGGER = Logger.getLogger(BaseMessageCommunicator.class.getName());

    private String destinationChannelName;
    private String replyDestinationChannelName;

    Context jndiContext;
    ConnectionFactory factory;
    Connection connection;
    Session session;

    Destination destination;

    public BaseMessageCommunicator(String destination)
    {
        this(destination, "");
    }

    public BaseMessageCommunicator(String destination, String replyDestination)
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
            e.printStackTrace();
        }
    }

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

    public void BuildConnection()
    {
        try
        {
            connection = factory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            destination = (Destination) jndiContext.lookup(destinationChannelName);
        }
        catch (NamingException e)
        {
            e.printStackTrace();
        }
        catch (JMSException e)
        {
            e.printStackTrace();
        }
    }

    public Destination GetReplyDestination()
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
