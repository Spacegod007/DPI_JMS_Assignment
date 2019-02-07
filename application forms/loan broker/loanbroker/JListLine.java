package loanbroker;

import model.loan.*;
import model.bank.*;

/**
 * This class represents one line in the JList in Loan Broker.
 * This class stores all objects that belong to one LoanRequest:
 *    - LoanRequest,
 *    - BankInterestRequest, and
 *    - BankInterestReply.
 *  Use objects of this class to add them to the JList.
 *    
 * @author 884294
 *
 */
class JListLine {
	
	private LoanRequest loanRequest;
	private BankInterestRequest bankRequest;
	private BankInterestReply bankReply;

	public JListLine(LoanRequest loanRequest) {
		this.setLoanRequest(loanRequest);
	}

	public LoanRequest getLoanRequest() {
		return loanRequest;
	}

	public void setLoanRequest(LoanRequest loanRequest) {
		this.loanRequest = loanRequest;
	}

	public BankInterestRequest getBankRequest() {
		return bankRequest;
	}

	public void setBankRequest(BankInterestRequest bankRequest) {
		this.bankRequest = bankRequest;
	}

	public BankInterestReply getBankReply() {
		return bankReply;
	}

	public void setBankReply(BankInterestReply bankReply) {
		this.bankReply = bankReply;
	}

	@Override
	public String toString() {
		return loanRequest.toString() + " || " + ((bankReply != null) ? bankReply.toString() : "waiting for reply...");
	}

}
