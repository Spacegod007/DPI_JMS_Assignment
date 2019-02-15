package loanbroker.event;

import event.IGatewayEventListener;
import model.loan.LoanRequest;

/**
 * A Gateway event listener used to fire an event when a LoanRequest is received
 */
public interface LoanRequestReceivedEventListener extends IGatewayEventListener<LoanRequest>
{
}
