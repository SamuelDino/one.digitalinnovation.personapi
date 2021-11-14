package one.digitalinnovation.personapi.service;

import one.digitalinnovation.personapi.dto.PersonDTO;
import one.digitalinnovation.personapi.entity.Person;
import one.digitalinnovation.personapi.exception.ConstraintViolationException;
import one.digitalinnovation.personapi.exception.ResourceNotFoundException;
import one.digitalinnovation.personapi.factory.PersonDTOFactory;
import one.digitalinnovation.personapi.factory.PersonFactory;
import one.digitalinnovation.personapi.mapper.PersonMapper;
import one.digitalinnovation.personapi.repository.PersonRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PersonServiceTests {

    private Person validPerson;
    private Person notValidPerson;
    private PersonDTO personDto;
    private Long existingId;
    private Long nonExistingId;
    private Pageable pageable;
    private PageImpl<Person> personPage;
    private PageImpl<PersonDTO> personDTOPage;
    private Person personNonValidCPF;

    @InjectMocks
    private PersonService personService;

    @Mock
    private PersonRepository personRepository;

    @BeforeEach
    void setUp() throws Exception{
        nonExistingId = 2L;
        notValidPerson = new Person();
        validPerson = PersonFactory.createPerson();
        existingId = 1L;
        validPerson.setId(existingId);
        personDto = new PersonDTO();
        personDto = PersonMapper.INSTANCE.toDto(validPerson);
        personNonValidCPF = PersonFactory.createPerson();
        personNonValidCPF.setCpf("");
        pageable = PageRequest.of(0,1);
        personPage = new PageImpl<>(List.of(PersonFactory.createPerson()));
        personDTOPage = new PageImpl<>(List.of(PersonDTOFactory.createPersonDTO()));
    }

    @Test
    public void insertShoulThrowConstraintViolationExceptionWhenNonInvalidCPF(){
        doThrow(javax.validation.ConstraintViolationException.class).when(personRepository).save(personNonValidCPF);

        Assertions.assertThrows(ConstraintViolationException.class, ()->{
            personService.insert(PersonMapper.INSTANCE.toDto(personNonValidCPF));
        });
    }

    @Test
    public void insertShouldReturnPersonDtoWhenGivenPersonDto(){
        when(personRepository.save(validPerson)).thenReturn(validPerson);

        Assertions.assertEquals(personDto, personService.insert(personDto));
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenNonExistingId(){
        when(personRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        doThrow(ResourceNotFoundException.class).when(personRepository).findById(nonExistingId);

        Assertions.assertThrows(ResourceNotFoundException.class, ()->{
            personService.findById(nonExistingId);
        });
    }

    @Test
    public void findByIdShouldReturnPersonDTOWhenIdExisting(){
        when(personRepository.findById(existingId)).thenReturn(Optional.of(validPerson));

        Assertions.assertEquals(personDto, personService.findById(existingId));
    }

    @Test
    public void findAllShoulReturnPageOfPersonDtoWhenGivenPageable(){
        when(personRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(personPage);

        Assertions.assertEquals(personDTOPage, personService.findAll(pageable));
    }

    @Test
    public void deleteShouldThrowEmptyResultDataAccessExceptionWhenNonExistingId(){
        doThrow(EmptyResultDataAccessException.class).when(personRepository).deleteById(nonExistingId);

        Assertions.assertThrows(ResourceNotFoundException.class, ()->{
            personService.delete(nonExistingId);
        });
    }


    @Test
    public void deleteShouldReturnPersonDtoWhenIdExisting(){
        when(personRepository.findById(existingId)).thenReturn(Optional.of(validPerson));
        doNothing().when(personRepository).deleteById(existingId);

        Assertions.assertEquals(personDto, personService.delete(existingId));
    }

    @Test
    public void updateShouldReturnPersonDtoWhenGivenPersonTdoAndIdExisting(){
        when(personRepository.save(validPerson)).thenReturn(validPerson);

        Assertions.assertEquals(personDto, personService.update(personDto, existingId));
    }

}
