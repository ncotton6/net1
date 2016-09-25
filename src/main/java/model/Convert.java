package model;

/**
 * This application requires that a great deal of data be turned from byte[] to
 * usable objects.  This interface provides a common hook that will convert an
 * array of bytes into an object.
 * <p>
 * Created by nathaniel on 9/7/16.
 */
public interface Convert {

    /**
     * Takes in an array of bytes that represents an object, and turns
     * those bytes into and object.
     *
     * @param bytes
     * @return Object
     */
    Object convert(byte[] bytes);

}
