package core.net.packet;

/**
 * Represents an outgoing packet.
 * @author Emperor
 * @param <Context>> The context type.
 */
public interface OutgoingPacket<Context> {

    /**
     * Send.
     *
     * @param context the context
     */
    public void send(Context context);

}