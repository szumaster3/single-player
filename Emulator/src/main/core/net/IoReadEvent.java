package core.net;

import java.nio.ByteBuffer;

/**
 * An abstract class representing a read event from an {@link IoSession}.
 *
 * <p>This event is executed when incoming data needs to be processed. It handles optional
 * queueing of partial reads and delegates specific data processing to the implementing subclass
 * via the {@link #read(IoSession, ByteBuffer)} method.</p>
 */
public abstract class IoReadEvent implements Runnable {

	/** The associated session for this read event. */
	private final IoSession session;

	/** The buffer containing the data to read. */
	private ByteBuffer buffer;

	/** Whether a queued buffer was used during this read event. */
	protected boolean usedQueuedBuffer;

	/**
	 * Constructs a new {@code IoReadEvent}.
	 *
	 * @param session The session associated with this event.
	 * @param buffer The buffer containing data to process.
	 */
	public IoReadEvent(IoSession session, ByteBuffer buffer) {
		this.session = session;
		this.buffer = buffer;
	}

	/**
	 * Executes the read event. This method checks if there is a queued buffer from
	 * a previous partial read, and if so, it merges it with the current data. Then it
	 * calls the {@link #read(IoSession, ByteBuffer)} method to handle the actual data processing.
	 */
	@Override
	public void run() {
		try {
			if (session.getReadingQueue() != null) {
				buffer = session.getReadingQueue().put(buffer);
				buffer.flip();
				session.setReadingQueue(null);
				usedQueuedBuffer = true;
			}
			read(session, buffer);
		} catch (Throwable t) {
			t.printStackTrace();
			session.disconnect();
		}
	}

	/**
	 * Queues the current buffer for further reading, optionally prepending header data.
	 *
	 * @param data Optional data (e.g. opcode or size header) to prepend to the buffer.
	 */
	public void queueBuffer(int... data) {
		ByteBuffer queue = ByteBuffer.allocate(data.length + buffer.remaining() + 100_000);
		for (int value : data) {
			queue.put((byte) value);
		}
		queue.put(buffer);
		session.setReadingQueue(queue);
	}

	/**
	 * Reads and processes the data from the given buffer for the specified session.
	 * This method must be implemented by subclasses to define how the data should be handled.
	 *
	 * @param session The session this data is associated with.
	 * @param buffer The buffer containing the data to read.
	 */
	public abstract void read(IoSession session, ByteBuffer buffer);
}
