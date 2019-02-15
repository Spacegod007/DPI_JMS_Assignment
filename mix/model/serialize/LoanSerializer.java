package model.serialize;

import com.owlike.genson.Genson;
import model.loan.LoanReply;
import model.loan.LoanRequest;

/**
 * Serializes Loan requests and reply
 */
public class LoanSerializer implements IRequestReplySerializer<LoanRequest, LoanReply>
{
    private Genson genson;

    /**
     * Constructs the object
     */
    public LoanSerializer()
    {
        genson = new Genson();
    }

    @Override
    public String SerializeRequest(LoanRequest request)
    {
        return genson.serialize(request);
    }

    @Override
    public LoanRequest DeSerializeRequest(String requestText)
    {
        return genson.deserialize(requestText, LoanRequest.class);
    }

    @Override
    public String SerializeReply(LoanReply reply)
    {
        return genson.serialize(reply);
    }

    @Override
    public LoanReply DeSerializeReply(String replyText)
    {
        return genson.deserialize(replyText, LoanReply.class);
    }
}
