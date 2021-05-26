package de.helmholtz.marketplace.cerebrum.service;

import org.springframework.stereotype.Service;

import de.helmholtz.marketplace.cerebrum.entity.Person;
import de.helmholtz.marketplace.cerebrum.repository.PersonRepository;
import de.helmholtz.marketplace.cerebrum.service.common.CerebrumServiceBase;

@Service
public class PersonService extends CerebrumServiceBase<Person, PersonRepository>
{
    private final PersonRepository personRepository;

    protected PersonService(PersonRepository personRepository)
    {
        super(Person.class, PersonRepository.class);
        this.personRepository = personRepository;
    }

    public Person getPerson(String uuid)
    {
        return getEntity(uuid, personRepository);
    }

    public Person getPerson(String firstName, String lastName)
    {
        return personRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    public Person createPerson(Person entity)
    {
        return createEntity(entity, personRepository);
    }
}
