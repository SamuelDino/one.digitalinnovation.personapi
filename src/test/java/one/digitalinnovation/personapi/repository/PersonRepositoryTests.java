package one.digitalinnovation.personapi.repository;

import one.digitalinnovation.personapi.dto.PersonDTO;
import one.digitalinnovation.personapi.entity.Person;
import one.digitalinnovation.personapi.factory.PersonFactory;
import one.digitalinnovation.personapi.mapper.PersonMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import java.util.Optional;

@DataJpaTest
public class PersonRepositoryTests {

    private Person validPerson;
    private Person notValidPerson;
    private PersonDTO personDto;
    private Long existingId;
    private Long nonExistingId;

    @Autowired
    PersonRepository personRepository;

    @BeforeEach
    void setUp() throws Exception{
        nonExistingId = 10000L;
        notValidPerson = new Person();
        validPerson = PersonFactory.createPerson();
        validPerson = personRepository.save(validPerson);
        existingId = validPerson.getId();
        personDto = new PersonDTO();
        personDto = PersonMapper.INSTANCE.toDto(validPerson);
    }

    @Test
    public void findByIdShouldThrowInvalidDataAccessApiUsageExceptionWhenNullId(){
        Assertions.assertThrows(InvalidDataAccessApiUsageException.class, ()->{
            Optional<Person> personOpt = personRepository.findById(null);
        });
    }

    @Test
    public void findByIdShouldReturnOptionalEmptyWhenNonExistingId(){
        Optional<Person> personOpt = personRepository.findById(nonExistingId);
        Assertions.assertTrue(personOpt.isEmpty());
    }

    @Test
    public void findByIdShouldFindWhenExistingId(){
        Optional<Person> personOpt = personRepository.findById(existingId);
        Assertions.assertTrue(personOpt.isPresent());
    }

    @Test
    public void saveShouldThrowInvalidDataAccessApiUsageExceptionWhenNullEntity(){
        Assertions.assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            personRepository.save(null);
        });
    }

    @Test
    public void saveShouldThrowDataIntegrityViolationExceptionWhenNoValidEntity(){
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            personRepository.save(notValidPerson);
        });
    }

    @Test
    public void saveShouldSaveAndReturnPersonWhenValidEntity(){
        validPerson = personRepository.save(validPerson);
        Assertions.assertEquals(existingId, validPerson.getId());
    }

    @Test
    public void deleteByIdShouldDeletePersonWhenIdExists(){
        personRepository.deleteById(existingId);
        Optional<Person> optionalPerson = personRepository.findById(existingId);
        Assertions.assertFalse(optionalPerson.isPresent());
    }

    @Test
    public void deleteByIdShouldThrowResourceNotFoundExceptionWhenIdNotExists(){
        Assertions.assertThrows(EmptyResultDataAccessException.class,
                () -> {personRepository.deleteById(nonExistingId);
        });
    }

}
