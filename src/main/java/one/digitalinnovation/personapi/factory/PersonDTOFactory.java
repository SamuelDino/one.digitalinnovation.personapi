package one.digitalinnovation.personapi.factory;

import lombok.AllArgsConstructor;
import one.digitalinnovation.personapi.dto.AddressDTO;
import one.digitalinnovation.personapi.dto.PersonDTO;
import one.digitalinnovation.personapi.dto.PhoneDTO;
import one.digitalinnovation.personapi.entity.Address;
import one.digitalinnovation.personapi.entity.Phone;
import one.digitalinnovation.personapi.enums.PhoneType;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class PersonDTOFactory {

    public static PersonDTO createPersonDTO(){
        AddressDTO addressDTO = new AddressDTO(null,"Rua",11,"city","state","1111111");
        Set<AddressDTO> addressSetDTO = new HashSet<>();

        PhoneDTO phoneDTO = new PhoneDTO(null,"99999999999", PhoneType.HOME);
        Set<PhoneDTO> phoneSetDTO = new HashSet<>();

        addressSetDTO.add(addressDTO);
        phoneSetDTO.add(phoneDTO);

        PersonDTO personDTO = new PersonDTO(null,"FirstName","lastName","44453213387",null,addressSetDTO, phoneSetDTO);

        return personDTO;
    }
}
