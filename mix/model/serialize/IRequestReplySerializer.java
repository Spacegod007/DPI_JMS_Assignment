package model.serialize;

/**
 * An interface to serialize both a request and a reply
 * @param <REQ> The request type to serialize
 * @param <REP> The reply type to serialize
 */
public interface IRequestReplySerializer<REQ, REP>
{
    String SerializeRequest(REQ request);
    REQ DeSerializeRequest(String requestText);

    String SerializeReply(REP reply);
    REP DeSerializeReply(String replyText);
}
