package one.digitalinnovation.personapi.controller;

import one.digitalinnovation.personapi.dto.PersonDTO;
import one.digitalinnovation.personapi.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value ="/api/v1/person")
public class PersonController {

    @Autowired
    PersonService personService;

    @PostMapping
    public ResponseEntity<PersonDTO> insert(@RequestBody @Valid PersonDTO personDTO){
        personDTO = personService.insert(personDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(personDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(personDTO);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PersonDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(personService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PersonDTO>> findByAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "6") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "firstName") String orderBy
            ){

            PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);

            return ResponseEntity.ok(personService.findByAll(pageRequest));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<PersonDTO> delete(@PathVariable Long id){
        return ResponseEntity.ok(personService.delete(id));
    }

    @PutMapping(value = "/{id}")
    public  ResponseEntity<PersonDTO> update(@RequestBody @Valid PersonDTO personDTO, @PathVariable Long id){
        return ResponseEntity.ok(personService.update(personDTO, id));
    }

}
