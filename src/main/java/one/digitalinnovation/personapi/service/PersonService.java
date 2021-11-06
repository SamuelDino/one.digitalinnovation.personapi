package one.digitalinnovation.personapi.service;

import one.digitalinnovation.personapi.dto.PersonDTO;
import one.digitalinnovation.personapi.entity.Person;
import one.digitalinnovation.personapi.exception.ConstraintViolationException;
import one.digitalinnovation.personapi.exception.ResourceNotFoundException;
import one.digitalinnovation.personapi.mapper.PersonMapper;
import one.digitalinnovation.personapi.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    PersonRepository personRepository;

    @Transactional
    public PersonDTO insert(PersonDTO personDTO){
        try {
            Person personSaved = personRepository.save(PersonMapper.INSTANCE.toEntity(personDTO));
            return PersonMapper.INSTANCE.toDto(personSaved);
        }
        catch (javax.validation.ConstraintViolationException e){
            throw new ConstraintViolationException("CPF inválido !");
        }
    }

    @Transactional(readOnly = true)
    public PersonDTO findById(Long id) {
        Optional<Person> personOpt = personRepository.findById(id);
        return PersonMapper.INSTANCE.toDto(personOpt.orElseThrow(()-> new ResourceNotFoundException("ID não encontrado !")));
    }

    @Transactional(readOnly = true)
    public Page<PersonDTO> findAll(Pageable pageable) {
        return personRepository.findAll(pageable).map(x->PersonMapper.INSTANCE.toDto(x));
    }

    @Transactional
    public PersonDTO delete(Long id){
        Optional<Person> personOpt = personRepository.findById(id);
        if (personRepository.existsById(id)){
            personRepository.delete(personOpt.get());
        }
        return PersonMapper.INSTANCE.toDto(personOpt.orElseThrow(()-> new ResourceNotFoundException("ID não encontrado !")));
    }

    @Transactional
    public PersonDTO update(PersonDTO personDTO, Long id){
        if (personRepository.existsById(id)){
            personDTO.setId(id);
            personRepository.save(PersonMapper.INSTANCE.toEntity(personDTO));
        }
        Optional<Person> personOpt = personRepository.findById(id);
        return PersonMapper.INSTANCE.toDto(personOpt.orElseThrow(()-> new ResourceNotFoundException("ID não encontrado !")));
    }

}
