package rabobank.bank.event;

import event.IGatewayEventListener;
import model.bank.BankInterestRequest;

/**
 * A Gateway event listener used to fire an event when a BankInterestRequest is received
 */
public interface BankRequestReceivedEventListener extends IGatewayEventListener<BankInterestRequest>
{
}
