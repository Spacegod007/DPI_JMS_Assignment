package event;

/**
 * A generic event listener used to trigger events from a gateway
 * @param <Object> The object which should be parsed through
 */
public interface IGatewayEventListener<Object>
{
    /**
     * Fires the event
     * @param object The object to receive
     * @param correlationId Id to refer to this trigger
     */
    void Fire(Object object, String correlationId);
}
