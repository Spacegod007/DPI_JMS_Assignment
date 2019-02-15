package loanclient.event;

import event.IGatewayEventListener;
import model.loan.LoanReply;

/**
 * A Gateway event listener used to fire an event when a LoanReply is received
 */
public interface LoanReplyReceivedEventListener extends IGatewayEventListener<LoanReply>
{
}
