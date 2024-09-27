package io.jmix.petclinic.view.pet.pet.contact;

import io.jmix.core.Messages;
import io.jmix.petclinic.entity.owner.Owner;
import io.jmix.petclinic.entity.pet.Pet;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;


// tag::fetch-contact-logic[]
@Component
public class PetContactFetcher {

    private final Messages messages;

    public PetContactFetcher(Messages messages) {
        this.messages = messages;
    }

    public Optional<Contact> findContact(Pet pet) {
        return Optional.ofNullable(pet.getOwner())
                .flatMap(owner ->
                        Stream.of(
                                        contactInfo(owner.getTelephone(), ContactType.TELEPHONE),
                                        contactInfo(owner.getEmail(), ContactType.EMAIL),
                                        contactInfo(ownerAddress(owner), ContactType.ADDRESS)
                                )
                                .filter(Optional::isPresent)
                                .findFirst()
                                .orElse(Optional.empty())
                );
    }

    private Optional<Contact> contactInfo(String contactInfo, ContactType contactType) {
        return Optional.ofNullable(contactInfo)
                .filter(StringUtils::hasText)
                .map(info -> createContact(info, contactType));
    }

    private Contact createContact(String contactValue, ContactType contactType) {
        Contact contact = new Contact();
        contact.setValue(contactValue);
        contact.setType(contactType);
        return contact;
    }

    private String ownerAddress(Owner owner) {
        return messages.formatMessage(
                this.getClass(), "ownerAddressFormat",
                owner.getFirstName(), owner.getLastName(), owner.getAddress(), owner.getCity()
        );
    }
}
// end::fetch-contact-logic[]