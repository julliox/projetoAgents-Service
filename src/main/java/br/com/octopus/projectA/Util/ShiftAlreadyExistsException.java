package br.com.octopus.projectA.Util;

public class ShiftAlreadyExistsException extends RuntimeException {
    public ShiftAlreadyExistsException(String message) {
        super(message);
    }
}
