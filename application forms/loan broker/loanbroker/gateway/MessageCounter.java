package loanbroker.gateway;

import loanbroker.event.RequiredMessagesReceivedEventListener;
import model.bank.BankInterestReply;

import java.util.ArrayList;
import java.util.List;

public class MessageCounter
{
    private final int requiredMessages;
    private final String correlationId;
    private final List<BankInterestReply> replies;

    private List<RequiredMessagesReceivedEventListener> listeners;

    public MessageCounter(int requiredMessages, String correlationId)
    {
        this.requiredMessages = requiredMessages;
        this.correlationId = correlationId;

        replies = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public void MessageReceived(BankInterestReply reply)
    {
        replies.add(reply);
        System.out.println("Message received, now at: " + replies.size() + " out of " + requiredMessages);
        if (replies.size() == requiredMessages)
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
            listener.Fire(replies, correlationId);
        }
    }
}
