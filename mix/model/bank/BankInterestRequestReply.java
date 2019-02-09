package model.bank;

import messaging.requestreply.RequestReply;

public class BankInterestRequestReply extends RequestReply<BankInterestRequest, BankInterestReply>
{
    public BankInterestRequestReply(BankInterestRequest bankInterestRequest, BankInterestReply bankInterestReply)
    {
        super(bankInterestRequest, bankInterestReply);
    }
}
