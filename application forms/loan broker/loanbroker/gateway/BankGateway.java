package loanbroker.gateway;

import event.GatewayEventContainer;
import loanbroker.event.BankReplyReceivedEventListener;
import messaging.MessageReceiver;
import messaging.MessageSender;
import model.StaticNames;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;
import model.serialize.BankInterestSerializer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class BankGateway extends GatewayEventContainer<BankReplyReceivedEventListener, BankInterestReply>
{
    private MessageSender messageSender;
    private MessageReceiver messageReceiver;
    private BankInterestSerializer bankInterestSerializer;

    private Map<String, BankInterestRequest> bankInterestRequestById;

    /**
     * Constructs the object
     */
    public BankGateway()
    {
        super();
        bankInterestRequestById = new HashMap<>();
        messageSender = new MessageSender(StaticNames.ABN_AMRO_BANK_DESTINATION);
        messageReceiver = new MessageReceiver(StaticNames.BROKER_FROM_BANK_DESTINATION);
        bankInterestSerializer = new BankInterestSerializer();

        messageReceiver.PrepareReceiveMessage(this::messageReceived);
    }

    /**
     * Gets triggered when a message is received
     * @param message The received message
     */
    private void messageReceived(Message message)
    {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try
        {
            String serializedObject = (String) objectMessage.getObject();
            BankInterestReply bankInterestReply = bankInterestSerializer.DeSerializeReply(serializedObject);
            Fire(bankInterestReply, message.getJMSCorrelationID());
        }
        catch (JMSException e)
        {
            LOGGER.log(Level.SEVERE, StaticNames.LOGGER_ERROR_RECEIVING_MESSAGE);
        }
    }

    /**
     * Sends a BankInterestRequest
     * @param request The request to send
     * @param correlationId The The id to refer to this message in advance
     */
    public void SendBankInterestRequest(BankInterestRequest request, String correlationId)
    {
        String serializedObject = bankInterestSerializer.SerializeRequest(request);
        bankInterestRequestById.put(correlationId, request);
        messageSender.SendMessage(serializedObject, correlationId);
    }
}