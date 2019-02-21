package ing.bank.gateway;

import event.GatewayEventContainer;
import ing.bank.event.BankRequestReceivedEventListener;
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

public class LoanBrokerGateway extends GatewayEventContainer<BankRequestReceivedEventListener, BankInterestRequest>
{
    private MessageSender messageSender;
    private MessageReceiver messageReceiver;
    private BankInterestSerializer bankInterestSerializer;

    private Map<BankInterestRequest, String> idByRequest;

    /**
     * Constructs the object
     */
    public LoanBrokerGateway()
    {
        idByRequest = new HashMap<>();

        messageSender = new MessageSender(StaticNames.BROKER_FROM_BANK_DESTINATION);
        messageReceiver = new MessageReceiver(StaticNames.ING_BANK_DESTINATION);
        bankInterestSerializer = new BankInterestSerializer();

        messageReceiver.PrepareReceiveMessage(this::messageReceived);
    }

    /**
     * Triggers when a message is received
     * @param message The received message
     */
    private void messageReceived(Message message)
    {
        ObjectMessage receivedMessage = (ObjectMessage) message;
        try
        {
            String serializedObject = (String) receivedMessage.getObject();
            BankInterestRequest bankInterestRequest = bankInterestSerializer.DeSerializeRequest(serializedObject);
            String correlationId = message.getJMSCorrelationID();
            idByRequest.put(bankInterestRequest, correlationId);
            Fire(bankInterestRequest, correlationId);
        }
        catch (JMSException e)
        {
            LOGGER.log(Level.SEVERE, StaticNames.LOGGER_ERROR_RECEIVING_MESSAGE);
        }
    }

    /**
     * Sends an BankInterestReply which is linked to a request
     * @param request The request to which the reply is linked
     * @param reply The reply to send
     */
    public void SendBankInterestReply(BankInterestRequest request, BankInterestReply reply)
    {
        String id = idByRequest.get(request);
        String serializedReply = bankInterestSerializer.SerializeReply(reply);
        messageSender.SendMessage(serializedReply, id);
    }
}
