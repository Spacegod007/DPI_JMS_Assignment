package event;

import loanclient.gateway.LoanBrokerGateway;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class GatewayEventContainer<EventListener extends IGatewayEventListener<Object>, Object>
{
    protected final Logger LOGGER = Logger.getLogger(GatewayEventContainer.class.getName());

    private final List<EventListener> listeners;

    protected GatewayEventContainer()
    {
        listeners = new ArrayList<>();
    }

    public void AddListener(EventListener listener)
    {
        listeners.add(listener);
    }

    public void RemoveListener(EventListener listener)
    {
        listeners.remove(listener);
    }

    protected void Fire(Object object, String correlationId)
    {
        for (EventListener listener : listeners)
        {
            listener.Fire(object, correlationId);
        }
    }
}
