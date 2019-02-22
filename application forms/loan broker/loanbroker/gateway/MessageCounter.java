package loanbroker.gateway;

import loanbroker.event.RequiredMessagesReceivedEventListener;
import model.bank.BankInterestReply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageCounter
{
    private final int requiredMessages;
    private final String correlationId;

    private final Map<String, BankInterestReply> bankInterestReplyById;
    private final List<String> messageIds;

    private List<RequiredMessagesReceivedEventListener> listeners;

    MessageCounter(List<String> messageIds, String correlationId)
    {
        this.correlationId = correlationId;
        this.messageIds = messageIds;
        this.requiredMessages = messageIds.size();

        listeners = new ArrayList<>();
        bankInterestReplyById = new HashMap<>();
    }

    void MessageReceived(String aggrigationId, BankInterestReply reply)
    {
        if (messageIds.contains(aggrigationId))
        {
            bankInterestReplyById.put(aggrigationId, reply);

            checkForRequiredMessageAmount();
        }
    }

    private void checkForRequiredMessageAmount()
    {
        if (bankInterestReplyById.size() == requiredMessages)
        {
            Fire();
        }
    }

    public void AddListener(RequiredMessagesReceivedEventListener listener)
    {
        listeners.add(listener);
    }

    public void RemoveListener(RequiredMessagesReceivedEventListener listener)
    {
        listeners.remove(listener);
    }

    public void Fire()
    {
        for (RequiredMessagesReceivedEventListener listener : listeners)
        {
            listener.Fire(new ArrayList<>(bankInterestReplyById.values()), correlationId);
        }
    }
}
