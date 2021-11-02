package one.digitalinnovation.personapi.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StandardError {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
