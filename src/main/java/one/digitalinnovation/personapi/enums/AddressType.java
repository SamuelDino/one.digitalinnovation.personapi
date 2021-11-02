package one.digitalinnovation.personapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AddressType {

    HOME("Home"),
    COMMERCIAL("Commercial");

    private String description;
}
