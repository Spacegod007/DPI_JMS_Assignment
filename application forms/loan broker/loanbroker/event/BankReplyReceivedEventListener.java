package loanbroker.event;

import event.IGatewayEventListener;
import model.bank.BankInterestReply;

/**
 * A Gateway event listener used to fire an event when a BankInterestReply is received
 */
public interface BankReplyReceivedEventListener extends IGatewayEventListener<BankInterestReply>
{
}
