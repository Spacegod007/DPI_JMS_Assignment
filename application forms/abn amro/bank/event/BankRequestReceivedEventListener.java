package bank.event;

import event.IGatewayEventListener;
import model.bank.BankInterestRequest;

public interface BankRequestReceivedEventListener extends IGatewayEventListener<BankInterestRequest>
{
}
