package messaging;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

class BaseMessageSendReceiver
{
    static final Logger LOGGER = Logger.getLogger(BaseMessageSendReceiver.class.getName());

    final Context jndiContext;
    private final ConnectionFactory factory;
    private final String destinationString;

    Connection connection;
    Session session;
    Destination destination;

    BaseMessageSendReceiver(String destination) throws NamingException
    {
        destinationString = destination;
        Properties properties = new Properties();
        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        properties.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
        properties.put(("queue." + destination), destination);

        jndiContext = new InitialContext(properties);
        factory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");

        ((ActiveMQConnectionFactory) factory).setTrustAllPackages(true);


    }

    void buildConnection() throws JMSException
    {
        connection = factory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        try {
            destination = (Destination) jndiContext.lookup(destinationString);
        }
        catch (NamingException e)
        {
            LOGGER.log(Level.SEVERE, "Destination lookup failed", e);
        }

    }
}
