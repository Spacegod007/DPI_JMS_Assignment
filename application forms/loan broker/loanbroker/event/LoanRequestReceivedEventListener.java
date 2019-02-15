package loanbroker.event;

import event.IGatewayEventListener;
import model.loan.LoanRequest;

public interface LoanRequestReceivedEventListener extends IGatewayEventListener<LoanRequest>
{
}
