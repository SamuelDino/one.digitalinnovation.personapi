package one.digitalinnovation.personapi.factory;

import lombok.AllArgsConstructor;
import one.digitalinnovation.personapi.entity.Address;
import one.digitalinnovation.personapi.entity.Person;
import one.digitalinnovation.personapi.entity.Phone;
import one.digitalinnovation.personapi.enums.PhoneType;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class PersonFactory {

    public static Person createPerson(){
        Address address = new Address(null,"Rua",11,"city","state","1111111");
        Set<Address> addressSet = new HashSet<>();

        Phone phone = new Phone(null,"99999999999", PhoneType.HOME);
        Set<Phone> phoneSet = new HashSet<>();

        addressSet.add(address);
        phoneSet.add(phone);

        Person person = new Person(null,"FirstName","lastName","44453213387",null,addressSet,phoneSet);

        return person;
    }
}
