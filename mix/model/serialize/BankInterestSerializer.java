package model.serialize;

import com.owlike.genson.Genson;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;

/**
 * Serializes BankInterest requests and replies
 */
public class BankInterestSerializer implements IRequestReplySerializer<BankInterestRequest, BankInterestReply>
{
    private Genson genson;

    /**
     * Constructs the serializer
     */
    public BankInterestSerializer()
    {
        genson = new Genson();
    }

    @Override
    public String SerializeRequest(BankInterestRequest request)
    {
        return genson.serialize(request);
    }

    @Override
    public BankInterestRequest DeSerializeRequest(String requestText)
    {
        return genson.deserialize(requestText, BankInterestRequest.class);
    }

    @Override
    public String SerializeReply(BankInterestReply reply)
    {
        return genson.serialize(reply);
    }

    @Override
    public BankInterestReply DeSerializeReply(String replyText)
    {
        return genson.deserialize(replyText, BankInterestReply.class);
    }
}
