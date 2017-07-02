package org.apache.ibatis.exceptions;

/**
 * Created by adam on 7/2/17.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException() {}

    public EntityNotFoundException(String message) {super(message);}

}
