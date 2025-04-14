package core.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.ExecutorService;

/**
 * Handles I/O events for network communication using Java NIO.
 * <p>
 * This class manages the life cycle of non-blocking I/O connections, including accepting
 * new connections, reading and writing data, and handling disconnections.
 */
public class IoEventHandler {

    /**
     * The executor service used to handle asynchronous I/O operations.
     */
    protected final ExecutorService service;

    /**
     * Constructs a new {@code IoEventHandler} with the specified executor service.
     *
     * @param service the executor service to use for handling I/O tasks.
     */
    public IoEventHandler(ExecutorService service) {
        this.service = service;
    }

    /**
     * Handles a connection event. This method is intended to be overridden by subclasses.
     *
     * @param key the selection key representing the connection event.
     * @throws IOException if an I/O error occurs.
     */
    public void connect(SelectionKey key) throws IOException {
        // Optional implementation by subclasses
    }

    /**
     * Accepts a new incoming connection and registers it for reading.
     *
     * @param key      the selection key associated with the server socket channel.
     * @param selector the selector to register the new connection with.
     * @throws IOException if an I/O error occurs during acceptance or registration.
     */
    public void accept(SelectionKey key, Selector selector) throws IOException {
        SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
        sc.configureBlocking(false);
        sc.socket().setTcpNoDelay(true);
        sc.register(selector, SelectionKey.OP_READ);
    }

    /**
     * Reads data from a channel and delegates processing to a reader event.
     * If the session is not yet established, it initializes a new {@link IoSession}.
     *
     * @param key the selection key associated with the readable channel.
     * @throws IOException if an I/O error occurs during reading.
     */
    public void read(SelectionKey key) throws IOException {
        ReadableByteChannel channel = (ReadableByteChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(100_000);
        IoSession session = (IoSession) key.attachment();
        try {
            if (channel.read(buffer) == -1) {
                if (session != null) {
                    session.disconnect();
                }
                key.cancel();
                return;
            }
        } catch (IOException e) {
            if (e.getMessage().contains("reset by peer") && session != null) {
                session.disconnect();
            } else {
                key.cancel();
                return;
            }
        }

        buffer.flip();

        if (session == null) {
            key.attach(session = new IoSession(key, service));
        }

        service.execute(session.getProducer().produceReader(session, buffer));
    }

    /**
     * Handles a write event for the associated session.
     * Removes the write interest from the selection key to prevent busy waiting.
     *
     * @param key the selection key representing the writable channel.
     */
    public void write(SelectionKey key) {
        IoSession session = (IoSession) key.attachment();
        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
        session.write();
    }

    /**
     * Handles disconnection of a client session, optionally logging the cause of the disconnect.
     *
     * @param key the selection key representing the disconnected channel.
     * @param t   the cause of the disconnect, may be {@code null}.
     */
    public void disconnect(SelectionKey key, Throwable t) {
        try {
            IoSession session = (IoSession) key.attachment();
            String cause = "" + t;
            if (t != null && !(t instanceof ClosedChannelException
                    || cause.contains("De externe host")
                    || cause.contains("De software op uw")
                    || cause.contains("An established connection was aborted")
                    || cause.contains("An existing connection")
                    || cause.contains("AsynchronousClose"))) {
                t.printStackTrace();
            }

            if (session != null) {
                session.disconnect();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
