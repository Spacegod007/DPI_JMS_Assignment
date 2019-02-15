package event;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A container used to automate the trigger of events for gateways
 * @param <EventListener> The type of event which should get fired
 * @param <Object> The object which should be parsed through
 */
public abstract class GatewayEventContainer<EventListener extends IGatewayEventListener<Object>, Object>
{
    protected final Logger LOGGER = Logger.getLogger(GatewayEventContainer.class.getName());

    /**
     * The listeners of which get fired when a trigger occurs
     */
    private final List<EventListener> listeners;

    /**
     * Constructs the object
     */
    protected GatewayEventContainer()
    {
        listeners = new ArrayList<>();
    }

    /**
     * Adds a listener
     * @param listener The listener to add
     */
    public void AddListener(EventListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Removes a listener
     * @param listener the listener to remove
     */
    public void RemoveListener(EventListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * Fires the event on all listeners
     * @param object The object which should be parsed through
     * @param correlationId An id which refers to this trigger
     */
    protected void Fire(Object object, String correlationId)
    {
        for (EventListener listener : listeners)
        {
            listener.Fire(object, correlationId);
        }
    }
}
