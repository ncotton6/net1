package model;

/**
 * File: ByteArray.java
 * Created by nathaniel on 8/29/16.
 *
 * All classes that implement ByteArray will provide a mechanism to turn the underlying
 * object into a byte[].  This can be easily used to transmit data across a network.
 */
public interface ByteArray {

    /**
     * A call to this method on an implementing class will turn the object into
     * a byte[]. This will make it easier for transmitting the data across a
     * network.
     *
     * @return byte[] representing the object.
     */
    byte[] getByteArray();

}
