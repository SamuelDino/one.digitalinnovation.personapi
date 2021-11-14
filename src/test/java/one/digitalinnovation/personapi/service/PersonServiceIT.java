package one.digitalinnovation.personapi.service;

import one.digitalinnovation.personapi.dto.PersonDTO;
import one.digitalinnovation.personapi.exception.ConstraintViolationException;
import one.digitalinnovation.personapi.exception.ResourceNotFoundException;
import one.digitalinnovation.personapi.factory.PersonDTOFactory;
import one.digitalinnovation.personapi.repository.PersonRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class PersonServiceIT {

    private PersonDTO personDto;
    private Long existingId;
    private Long nonExistingId;
    private PersonDTO invalidCPFPersonDto;
    private PageRequest pageRequest;
    private Long persorRepositoryCount;

    @Autowired
    PersonService personService;

    @Autowired
    PersonRepository personRepository;

    @BeforeEach
    void setUp() throws Exception{
        nonExistingId = 1000000L;
        personDto = PersonDTOFactory.createPersonDTO();
        invalidCPFPersonDto = PersonDTOFactory.createPersonDTO();
        invalidCPFPersonDto.setCpf("");
        persorRepositoryCount = 0L;
        pageRequest = PageRequest.of(0,1);
    }

    @Test
    public void insertShouldRetunsPersonDtoWhenValidPersonDto(){
       personService.insert(personDto);
        Assertions.assertEquals(++persorRepositoryCount,personRepository.count());
    }

    @Test
    public void insertShouldThrowConstraintViolationExceptionWhenInvalidCPF(){
        Assertions.assertThrows(ConstraintViolationException.class, ()-> {
            personService.insert(invalidCPFPersonDto);
        });
    }

    @Test
    public void findByIdShouldReturnPersonDTOWhenValidId(){
        existingId = personService.insert(personDto).getId();
        Assertions.assertEquals(existingId, personService.findById(existingId).getId());
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenNonValidId(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            personService.findById(nonExistingId);
        });
    }

    @Test
    public void findAllShouldReturnPersonDTOPage(){
        personService.insert(personDto);
        Page<PersonDTO> personDTOPage = personService.findAll(pageRequest);
        Assertions.assertFalse(personDTOPage.isEmpty());
        Assertions.assertEquals(0,personDTOPage.getNumber());
        Assertions.assertEquals(1,personDTOPage.getSize());
        Assertions.assertEquals(++persorRepositoryCount,personDTOPage.getTotalElements());
    }

    @Test
    public void findAllShouldReturnEmptyPageWhenPageRequestDoesNotExist(){
        Page<PersonDTO> personDTOPage = personService.findAll(pageRequest);
        Assertions.assertTrue(personDTOPage.isEmpty());
    }

    @Test
    public void findAllShouldReturnSortedPageWhenPageRequestSortedByFistName(){
        pageRequest = PageRequest.of(0,2, Sort.by("firstName"));
        personService.insert(personDto);
        personDto.setCpf("82737002320");
        personDto.setFirstName("A-secondName");
        personService.insert(personDto);
        Page<PersonDTO> personDTOPage = personService.findAll(pageRequest);
        Assertions.assertFalse(personDTOPage.isEmpty());
        Assertions.assertEquals("A-secondName", personDTOPage.getContent().get(0).getFirstName());
        Assertions.assertEquals("FirstName", personDTOPage.getContent().get(1).getFirstName());
    }

    @Test
    public void deleteShouldReturnPersonDtoWhenExistingId(){
        existingId = personService.insert(personDto).getId();
        Assertions.assertEquals(existingId, personService.delete(existingId).getId());
    }

    @Test
    public void deleteSholdThrowResourceNotFoundExceptionWhenNonExistingId(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            personService.delete(nonExistingId);
        });
    }

    @Test
    public void updateShouldReturnPersonDtoWhenGivenPersonDtoAndExistingId(){
        PersonDTO savedPersonDto = personService.insert(personDto);
        Assertions.assertEquals(savedPersonDto, personService.update(savedPersonDto, savedPersonDto.getId()));
    }

    @Test
    public void updateSholdThowsResourceNotFoundExceptionWhenNonExistingId(){
        PersonDTO savedPersonDto = personService.insert(personDto);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            personService.update(savedPersonDto, nonExistingId);
        });
    }

}
