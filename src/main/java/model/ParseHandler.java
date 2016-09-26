package model;

/**
 * {@link ParseHandler} interface creates a hook that can be used where a byte[] is passed
 * in and a Message is produced.
 * <p>
 * Created by nathaniel on 8/29/16.
 */
public interface ParseHandler {

    /**
     * Produces a message from a valid byte[] array.
     * Exceptions are thrown otherwise.
     *
     * @param bytes
     * @return Message
     */
    Message parse(byte[] bytes);
}
