package ms.net.producer;

import ms.net.EventProducer;
import ms.net.IoReadEvent;
import ms.net.IoSession;
import ms.net.IoWriteEvent;
import ms.net.event.RegistryReadEvent;
import ms.net.event.RegistryWriteEvent;

import java.nio.ByteBuffer;

/**
 * Handles world server registry.
 *
 * @author Emperor
 */
public final class RegistryEventProducer implements EventProducer {

    @Override
    public IoReadEvent produceReader(IoSession session, ByteBuffer buffer) {
        return new RegistryReadEvent(session, buffer);
    }

    @Override
    public IoWriteEvent produceWriter(IoSession session, Object context) {
        return new RegistryWriteEvent(session, context);
    }

}