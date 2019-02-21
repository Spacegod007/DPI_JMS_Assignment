package loanbroker.gateway;

import event.GatewayEventContainer;
import loanbroker.event.BankReplyReceivedEventListener;
import messaging.MessageReceiver;
import messaging.MessageSender;
import model.StaticNames;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;
import model.serialize.BankInterestSerializer;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class BankGateway extends GatewayEventContainer<BankReplyReceivedEventListener, BankInterestReply>
{
    private static final String ING       = "#{amount} <= 100000 && #{time} <= 10";
    private static final String ABN_AMRO  = "#{amount} >= 200000 && #{amount} <= 300000  && #{time} <= 20";
    private static final String RABO_BANK = "#{amount} <= 250000 && #{time} <= 15";

    private MessageSender abnAmroMessageSender;
    private MessageSender ingMessageSender;
    private MessageSender rabobankMessageSender;

    private MessageReceiver messageReceiver;
    private BankInterestSerializer bankInterestSerializer;

    private Map<String, MessageCounter> messageCounterById;

    /**
     * Constructs the object
     */
    public BankGateway()
    {
        super();
        messageCounterById = new HashMap<>();

        abnAmroMessageSender = new MessageSender(StaticNames.ABN_AMRO_BANK_DESTINATION);
        ingMessageSender = new MessageSender(StaticNames.ING_BANK_DESTINATION);
        rabobankMessageSender = new MessageSender(StaticNames.RABOBANK_BANK_DESTINATION);

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
            String correlationId = message.getJMSCorrelationID();
            MessageCounter messageCounter = messageCounterById.get(correlationId);
            messageCounter.MessageReceived(bankInterestReply);
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

        Evaluator evaluator = new Evaluator();
        evaluator.putVariable("amount", Integer.toString(request.getAmount()));
        evaluator.putVariable("time", Integer.toString(request.getTime()));

        int messagesSend = 0;

        try
        {
            if (evaluator.getBooleanResult(ING))
            {
                ingMessageSender.SendMessage(serializedObject, correlationId);
                messagesSend++;
            }

            if (evaluator.getBooleanResult(RABO_BANK))
            {
                rabobankMessageSender.SendMessage(serializedObject, correlationId);
                messagesSend++;
            }

            if (evaluator.getBooleanResult(ABN_AMRO))
            {
                abnAmroMessageSender.SendMessage(serializedObject, correlationId);
                messagesSend++;
            }

            if (messagesSend != 0)
            {
                MessageCounter messageCounter = new MessageCounter(messagesSend, correlationId);
                messageCounter.AddListener(this::RequiredMessagesReceived);
                messageCounterById.put(correlationId, messageCounter);
            }
            else
            {
                BankInterestReply noInterestedBankReply = new BankInterestReply(0, "No bank is interested");
                List<BankInterestReply> replies = new ArrayList<>();
                replies.add(noInterestedBankReply);
                RequiredMessagesReceived(replies, correlationId);
            }
        }
        catch (EvaluationException e)
        {
            LOGGER.log(Level.WARNING, "Error while getting result of evaluator", e);
        }
    }

    private void RequiredMessagesReceived(List<BankInterestReply> bankInterestReplies, String correlationId)
    {
        BankInterestReply bestReply = bankInterestReplies.get(0);

        for (int i = 1; i < bankInterestReplies.size(); i++)
        {
            BankInterestReply reply = bankInterestReplies.get(i);
            if (reply.getInterest() < bestReply.getInterest())
            {
                bestReply = reply;
            }
        }

        Fire(bestReply, correlationId);
    }
}
