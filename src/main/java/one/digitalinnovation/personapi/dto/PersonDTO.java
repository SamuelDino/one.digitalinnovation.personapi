package one.digitalinnovation.personapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String cpf;

    private LocalDate birthDate;

    private Set<AddressDTO> addresses;

    private Set<PhoneDTO> phones;
}
