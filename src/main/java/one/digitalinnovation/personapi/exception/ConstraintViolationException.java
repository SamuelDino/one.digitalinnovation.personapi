package one.digitalinnovation.personapi.exception;

public class ConstraintViolationException extends RuntimeException{

    public ConstraintViolationException(String msg) {
        super(msg);
    }
}