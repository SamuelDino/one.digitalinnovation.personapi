package one.digitalinnovation.personapi.mapper;

import one.digitalinnovation.personapi.dto.PersonDTO;
import one.digitalinnovation.personapi.entity.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper( PersonMapper.class );

    @Mapping(source = "birthDate", target = "birthDate", dateFormat = "dd-MM-yyyy")
    PersonDTO toDto(Person person);
    Person toEntity(PersonDTO personDTO);
}
