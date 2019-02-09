package model.loan;

import messaging.requestreply.RequestReply;

public class LoanRequestReply extends RequestReply<LoanRequest, LoanReply>
{
    public LoanRequestReply(LoanRequest loanRequest, LoanReply loanReply)
    {
        super(loanRequest, loanReply);
    }
}
