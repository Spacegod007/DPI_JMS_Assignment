package loanbroker.gateway;

import event.GatewayEventContainer;
import loanbroker.event.LoanRequestReceivedEventListener;
import messaging.MessageReceiver;
import messaging.MessageSender;
import model.StaticNames;
import model.loan.LoanReply;
import model.loan.LoanRequest;
import model.serialize.LoanSerializer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.util.logging.Level;

public class LoanClientGateway extends GatewayEventContainer<LoanRequestReceivedEventListener, LoanRequest>
{
    private MessageSender messageSender;
    private MessageReceiver messageReceiver;
    private LoanSerializer loanSerializer;

    public LoanClientGateway()
    {
        super();
        messageSender = new MessageSender(StaticNames.CLIENT_DESTINATION);
        messageReceiver = new MessageReceiver(StaticNames.BROKER_FROM_CLIENT_DESTINATION);
        loanSerializer = new LoanSerializer();

        messageReceiver.PrepareReceiveMessage(this::messageReceived);
    }

    private void messageReceived(Message message)
    {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try
        {
            String serializedObject = (String) objectMessage.getObject();
            LoanRequest loanRequest = loanSerializer.DeSerializeRequest(serializedObject);
            String correlationId = message.getJMSMessageID();
            Fire(loanRequest, correlationId);
        }
        catch (JMSException e)
        {
            LOGGER.log(Level.SEVERE, StaticNames.LOGGER_ERROR_RECEIVING_MESSAGE);
        }
    }

    public void SendLoanReply(LoanReply loanReply, String correlationId)
    {
        String serializedReply = loanSerializer.SerializeReply(loanReply);
        messageSender.SendMessage(serializedReply, correlationId);
    }
}
