package io.jmix.petclinic.view.pet.pet.contact;

import io.jmix.core.DataManager;
import io.jmix.core.Id;
import io.jmix.core.Messages;
import io.jmix.petclinic.entity.owner.Owner;
import io.jmix.petclinic.entity.pet.Pet;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.springframework.util.StringUtils.*;


// tag::fetch-contact-logic[]
@Component
public class PetContactFetcher {

    private final DataManager dataManager;
    private final Messages messages;

    public PetContactFetcher(DataManager dataManager, Messages messages) {
        this.dataManager = dataManager;
        this.messages = messages;
    }

    public Optional<Contact> findContact(Pet pet) {

        Optional<Owner> petOwner = loadOwnerFor(pet);

        if (petOwner.isPresent()) {

            Owner owner = petOwner.get();
            String telephone = owner.getTelephone();
            String email = owner.getEmail();

            if (hasText(telephone)) {
                return createContact(telephone, ContactType.TELEPHONE);
            } else if (hasText(email)) {
                return createContact(email, ContactType.EMAIL);
            } else {
                String address = formatOwnerAddress(owner);
                if (hasText(address)) {
                    return createContact(address, ContactType.ADDRESS);
                } else {
                    return Optional.empty();
                }
            }
        } else {
            return Optional.empty();
        }
    }

    private Optional<Contact> createContact(String contactValue, ContactType contactType) {
        Contact contact = new Contact();
        contact.setValue(contactValue);
        contact.setType(contactType);
        return Optional.of(contact);
    }

    private String formatOwnerAddress(Owner owner) {
        return messages.formatMessage(this.getClass(), "ownerAddressFormat", owner.getFirstName(), owner.getLastName(), owner.getAddress(), owner.getCity());
    }

    private Optional<Owner> loadOwnerFor(Pet pet) {
        if (pet.getOwner() == null) {
            return Optional.empty();
        }
        return dataManager.load(Id.of(pet.getOwner())).optional();
    }
}
// end::fetch-contact-logic[]