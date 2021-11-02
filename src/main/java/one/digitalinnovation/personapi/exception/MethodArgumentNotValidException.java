package one.digitalinnovation.personapi.exception;

public class MethodArgumentNotValidException extends RuntimeException{
    public MethodArgumentNotValidException(String msg) {
        super(msg);
    }
}
