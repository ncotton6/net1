package model;

/**
 * Created by nathaniel on 8/29/16.
 */
public interface ParseHandler {

    Message parse(byte[] bytes);
}
