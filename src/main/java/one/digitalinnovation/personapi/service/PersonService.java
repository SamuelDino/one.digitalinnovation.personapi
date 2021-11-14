package one.digitalinnovation.personapi.service;

import one.digitalinnovation.personapi.dto.PersonDTO;
import one.digitalinnovation.personapi.entity.Person;
import one.digitalinnovation.personapi.exception.ConstraintViolationException;
import one.digitalinnovation.personapi.exception.ResourceNotFoundException;
import one.digitalinnovation.personapi.mapper.PersonMapper;
import one.digitalinnovation.personapi.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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
        try{
            Optional<Person> personOpt = personRepository.findById(id);
            personRepository.deleteById(id);
            return PersonMapper.INSTANCE.toDto(personOpt.get());
        }
        catch(EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("ID não encontrado !");
        }
    }

    @Transactional
    public PersonDTO update(PersonDTO personDTO, Long id) {
        try {
            personDTO.setId(id);
            Person personSaved = personRepository.save(PersonMapper.INSTANCE.toEntity(personDTO));
            return PersonMapper.INSTANCE.toDto(personSaved);
        }
        catch(DataIntegrityViolationException e) {
            throw new ResourceNotFoundException("ID não encontrado !");
        }
    }

}
