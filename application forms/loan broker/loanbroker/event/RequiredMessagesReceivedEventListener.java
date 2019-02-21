package loanbroker.event;

import model.bank.BankInterestReply;

import java.util.List;

public interface RequiredMessagesReceivedEventListener
{
    void Fire(List<BankInterestReply> bankInterestReplies, String correlationId);
}
