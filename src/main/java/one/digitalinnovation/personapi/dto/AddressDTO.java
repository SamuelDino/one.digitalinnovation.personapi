package one.digitalinnovation.personapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {

    private Long id;

    private int number;

    private String street;

    private String city;

    private String state;

    private String zipCode;

}
