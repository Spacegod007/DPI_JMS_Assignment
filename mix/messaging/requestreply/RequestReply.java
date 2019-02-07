package messaging.requestreply;

/**
 * This class stores pairs Request-Reply. We will use this class in Loan Client and ABN Amro applications
 *  in order to make it easier for us to store pairs Request-Reply as items in a GUI JList.
 *  For example, in Loan Client application it will be RequestReply<LoanRequest,LoanReply>, and
 *  in ABN Amro application it will be RequestReply<BankInterestRequest, BankInterestReply>.
 * @author 884294
 *
 * @param <REQUEST>
 * @param <REPLY>
 */
public class RequestReply<REQUEST,REPLY> {
	
	private REQUEST request;
	private REPLY reply;
	
	public RequestReply(REQUEST request,  REPLY reply) {
		setRequest(request);
		setReply(reply);
	}	
	
	public REQUEST getRequest() {
		return request;
	}
	
	private void setRequest(REQUEST request) {
		this.request = request;
	}
	
	public REPLY getReply() {
		return reply;
	}
	
	public void setReply(REPLY reply) {
		this.reply = reply;
	}
	
	@Override
	public String toString() {
	   return request.toString() + "  --->  " + ((reply!=null)?reply.toString():"waiting for reply...");
	}
	
}
