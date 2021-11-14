package one.digitalinnovation.personapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import one.digitalinnovation.personapi.dto.PersonDTO;
import one.digitalinnovation.personapi.factory.PersonDTOFactory;
import one.digitalinnovation.personapi.repository.PersonRepository;
import one.digitalinnovation.personapi.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PersonControllerIT {

    private Long existingId;
    private Long nonExistingId;
    private PersonDTO personDTO;
    private PersonDTO personDtoWithInvalidCPF;
    private PageRequest pageRequest;
    private String jsonBodyPersonDto;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception{
        nonExistingId = 1000000L;
        personDTO = PersonDTOFactory.createPersonDTO();
        personDtoWithInvalidCPF = PersonDTOFactory.createPersonDTO();
        personDtoWithInvalidCPF.setCpf("");
        pageRequest = PageRequest.of(0,1);
        jsonBodyPersonDto = objectMapper.writeValueAsString(personDTO);
    }

    @Test
    public void insertShouldReturnPersonTdoWhenBodyPersonDto() throws Exception {
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

        mockMvc.perform(post("/api/v1/person")
                        .content(jsonBodyPersonDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void findByIdShouldReturnPersonDtoWhenExistingId() throws Exception {
        existingId = personService.insert(personDTO).getId();

        mockMvc.perform(get("/api/v1/person/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").exists());
    }

    @Test
    public void findByIdShouldTrowNotFoundWhenNomExistingId() throws Exception {
        mockMvc.perform(get("/api/v1/person/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findAllShouldReturnSortedPageWhenRequestPageSortedByFirstName() throws Exception {
        pageRequest = PageRequest.of(0,2, Sort.by("firstName"));
        personService.insert(personDTO);
        personDTO.setCpf("82737002320");
        personDTO.setFirstName("A-secondName");
        personService.insert(personDTO);
        Page<PersonDTO> personDTOPage = personService.findAll(pageRequest);

        mockMvc.perform(get("/api/v1/person?page=0&size=2&sort=firstName,asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(personRepository.count()))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content[0].firstName").value("A-secondName"))
                .andExpect(jsonPath("$.content[1].firstName").value("FirstName"));
    }

    @Test
    public void deleteShouldReturnPersonDtoWhenExistingId() throws Exception {
        existingId = personService.insert(personDTO).getId();

        mockMvc.perform(delete("/api/v1/person/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteShouldThrowNotFoundWhenNomExistingId() throws Exception {
        mockMvc.perform(delete("/api/v1/person/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnPersonDTOWhenGivenPersonDTOAndExistingId() throws Exception {
        PersonDTO personDTOSaved = personService.insert(personDTO);
        existingId = personDTOSaved.getId();
        String firstNameSaved = personDTOSaved.getFirstName();
        mockMvc.perform(put("/api/v1/person/{id}", existingId)
                        .content(jsonBodyPersonDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.firstName").value(firstNameSaved));
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenGivenBodyandNonExistingId() throws Exception {
        PersonDTO personDTOSaved = personService.insert(personDTO);
        jsonBodyPersonDto = objectMapper.writeValueAsString(personDTOSaved);

        mockMvc.perform(put("/api/v1/person/{id}", nonExistingId)
                        .content(jsonBodyPersonDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
