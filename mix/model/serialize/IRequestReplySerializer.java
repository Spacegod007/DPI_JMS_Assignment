package model.serialize;

public interface IRequestReplySerializer<REQ, REP>
{
    String SerializeRequest(REQ request);
    REQ DeSerializeRequest(String requestText);

    String SerializeReply(REP reply);
    REP DeSerializeReply(String replyText);
}
