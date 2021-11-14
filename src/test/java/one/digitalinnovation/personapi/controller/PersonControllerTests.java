package one.digitalinnovation.personapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import one.digitalinnovation.personapi.dto.PersonDTO;
import one.digitalinnovation.personapi.entity.Person;
import one.digitalinnovation.personapi.exception.ConstraintViolationException;
import one.digitalinnovation.personapi.exception.ResourceNotFoundException;
import one.digitalinnovation.personapi.factory.PersonDTOFactory;
import one.digitalinnovation.personapi.factory.PersonFactory;
import one.digitalinnovation.personapi.mapper.PersonMapper;
import one.digitalinnovation.personapi.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
public class PersonControllerTests {

    private PageImpl<PersonDTO> pagePersonDto;
    private Person validPerson;
    private PersonDTO personDto;
    private PersonDTO personDtoWithInvalidCPF;
    private Long existingId;
    private Long nonExistingId;
    private String jsonBodyPersonDto;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception{
        pagePersonDto = new PageImpl<>(List.of(PersonMapper.INSTANCE.toDto(PersonFactory.createPerson())));
        validPerson = PersonFactory.createPerson();
        existingId = 1L;
        validPerson.setId(existingId);
        personDto = PersonDTOFactory.createPersonDTO();
        personDtoWithInvalidCPF = PersonDTOFactory.createPersonDTO();
        personDtoWithInvalidCPF.setCpf("");
        jsonBodyPersonDto = objectMapper.writeValueAsString(personDto);
        nonExistingId = 2L;
    }

    @Test
    public void insertShouldReturnPersonTdoWhenGivenBodyPersonDto() throws Exception {
        Mockito.when(personService.insert(personDto)).thenReturn(personDto);

        mockMvc.perform(post("/api/v1/person")
                        .content(jsonBodyPersonDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.firstName").exists());


    }

    @Test
    public void insertShouldThrowConstraintViolationExceptionWhenBodyPersonDtoWithInvalidCPF() throws Exception {
        jsonBodyPersonDto = objectMapper.writeValueAsString(personDtoWithInvalidCPF);

        Mockito.doThrow(ConstraintViolationException.class).when(personService).insert(personDtoWithInvalidCPF);

        mockMvc.perform(post("/api/v1/person")
                        .content(jsonBodyPersonDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void findByIdShouldTrowNotFoundWhenNomExistingId() throws Exception {
        Mockito.doThrow(ResourceNotFoundException.class).when(personService).findById(nonExistingId);

        mockMvc.perform(get("/api/v1/person/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findByIdShouldReturnPersonDtoWhenExistingId() throws Exception {
        Mockito.when(personService.findById(existingId)).thenReturn(personDto);

        mockMvc.perform(get("/api/v1/person/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").exists());

    }

    @Test
    public void findAllShouldReturnPageOfPersonDtoWhenGivenPageable() throws Exception {
        Mockito.when(personService.findAll((Pageable) ArgumentMatchers.any())).thenReturn(pagePersonDto);

        mockMvc.perform(get("/api/v1/person")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteShouldReturnPersonDtoWhenExistingId() throws Exception {
        Mockito.when(personService.delete(existingId)).thenReturn(personDto);

        mockMvc.perform(delete("/api/v1/person/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteShouldThrowNotFoundWhenNomExistingId() throws Exception {
        Mockito.doThrow(ResourceNotFoundException.class).when(personService).delete(nonExistingId);

        mockMvc.perform(delete("/api/v1/person/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnPersonDtoWhenGivenBodyandExistingId() throws Exception {
        Mockito.when(personService.update(personDto, existingId)).thenReturn(personDto);

        mockMvc.perform(put("/api/v1/person/{id}", existingId)
                        .content(jsonBodyPersonDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").exists());
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenGivenBodyandNonExistingId() throws Exception {
        Mockito.when(personService.update(personDto, nonExistingId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/api/v1/person/{id}", nonExistingId)
                        .content(jsonBodyPersonDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
