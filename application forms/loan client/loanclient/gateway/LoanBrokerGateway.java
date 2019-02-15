package loanclient.gateway;

import event.GatewayEventContainer;
import loanclient.event.LoanReplyReceivedEventListener;
import messaging.MessageReceiver;
import messaging.MessageSender;
import model.StaticNames;
import model.loan.LoanReply;
import model.loan.LoanRequest;
import model.serialize.LoanSerializer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class LoanBrokerGateway extends GatewayEventContainer<LoanReplyReceivedEventListener, LoanReply>
{
    private MessageSender messageSender;
    private MessageReceiver messageReceiver;
    private LoanSerializer loanSerializer;

    private Map<String, LoanRequest> requestById;

    public LoanBrokerGateway()
    {
        requestById = new HashMap<>();
        messageSender = new MessageSender(StaticNames.BROKER_FROM_CLIENT_DESTINATION, StaticNames.CLIENT_DESTINATION);
        messageReceiver = new MessageReceiver(StaticNames.CLIENT_DESTINATION);
        loanSerializer = new LoanSerializer();

        messageReceiver.PrepareReceiveMessage(this::messageReceived);
    }

    public void ApplyForLoan(LoanRequest request)
    {
        String serializedRequest = loanSerializer.SerializeRequest(request);
        String correlationId = messageSender.SendMessage(serializedRequest);
        requestById.put(correlationId, request);
    }

    public LoanRequest GetLoanRequestById(String id)
    {
        return requestById.get(id);
    }

    private void messageReceived(Message message)
    {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try
        {
            String serializedObject = (String) objectMessage.getObject();
            LoanReply loanReply = loanSerializer.DeSerializeReply(serializedObject);
            Fire(loanReply, message.getJMSCorrelationID());
        }
        catch (JMSException e)
        {
            LOGGER.log(Level.SEVERE, StaticNames.LOGGER_ERROR_RECEIVING_MESSAGE);
        }
    }
}
