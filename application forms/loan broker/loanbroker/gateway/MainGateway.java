package loanbroker.gateway;

import loanbroker.event.BankReplyReceivedEventListener;
import loanbroker.event.LoanRequestReceivedEventListener;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;
import model.loan.LoanReply;
import model.loan.LoanRequest;

import java.util.HashMap;
import java.util.Map;

public class MainGateway
{
    private LoanClientGateway loanClientGateway;
    private BankGateway bankGateway;

    private Map<String, LoanRequest> loanRequestById;
    private Map<String, BankInterestRequest> bankInterestRequestById;

    /**
     * Constructs the object
     */
    public MainGateway()
    {
        loanRequestById = new HashMap<>();
        bankInterestRequestById = new HashMap<>();

        loanClientGateway = new LoanClientGateway();
        bankGateway = new BankGateway();

        AddLoanRequestReceivedEventListener(this::loanRequestReceived);
        AddBankReplyReceivedEventListener(this::bankReplyReceived);
    }

    /**
     * Adds a reply-listener to fire when a BankInterestReply is received
     * @param listener The listener to add
     */
    public void AddBankReplyReceivedEventListener(BankReplyReceivedEventListener listener)
    {
        bankGateway.AddListener(listener);
    }

    /**
     * Adds a request-listener to fire when a LoanRequest is received
     * @param listener The listener to add
     */
    public void AddLoanRequestReceivedEventListener(LoanRequestReceivedEventListener listener)
    {
        loanClientGateway.AddListener(listener);
    }

    /**
     * Gets a LoanRequest by the given Id
     * @param id The id to find the Request by
     * @return Returns a LoanRequest
     */
    public LoanRequest GetLoanRequestById(String id)
    {
        return loanRequestById.get(id);
    }

    /**
     * Gets a BankInterestRequest by the given id
     * @param id The id to find the Request by
     * @return Returns a BankInterestRequest
     */
    public BankInterestRequest GetBankInterestRequestById(String id)
    {
        return bankInterestRequestById.get(id);
    }

    /**
     * Gets fired when a LoanRequest is received
     * @param request The received request
     * @param correlationId The correlationId which was linked to the message
     */
    private void loanRequestReceived(LoanRequest request, String correlationId)
    {
        BankInterestRequest bankInterestRequest = bankInterestRequestFromLoanRequest(request);
        loanRequestById.put(correlationId, request);
        bankInterestRequestById.put(correlationId, bankInterestRequest);
        bankGateway.SendBankInterestRequest(bankInterestRequest, correlationId);
    }

    /**
     * Gets fired when a BankInterestReply is received
     * @param bankInterestReply The received reply
     * @param correlationId The correlationId which was linked to the message
     */
    private void bankReplyReceived(BankInterestReply bankInterestReply, String correlationId)
    {
        LoanReply loanReply = loanReplyFromBankInterestReply(bankInterestReply);
        loanClientGateway.SendLoanReply(loanReply, correlationId);
    }

    /**
     * Converts a LoanRequest to a BankInterestRequest which is fit to send to banks
     * @param loanRequest The LoanRequest to convert
     * @return A new BankInterestRequest
     */
    private BankInterestRequest bankInterestRequestFromLoanRequest(LoanRequest loanRequest) {
        return new BankInterestRequest(loanRequest.getAmount(), loanRequest.getTime());
    }

    /**
     * Converts a BankInterestReply to a LoanReply which is fit to send to clients
     * @param bankInterestReply The BankInterestReply to convert
     * @return A new LoanReply
     */
    private LoanReply loanReplyFromBankInterestReply(BankInterestReply bankInterestReply) {
        return new LoanReply(bankInterestReply.getInterest(), bankInterestReply.getQuoteId());
    }
}
