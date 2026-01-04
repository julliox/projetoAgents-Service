package br.com.octopus.projectA.Util;

public class TeamValidationException extends RuntimeException {
    
    public TeamValidationException(String message) {
        super(message);
    }
    
    public TeamValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

