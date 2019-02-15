package event;

public interface IGatewayEventListener<Object>
{
    void Fire(Object object, String correlationId);
}
