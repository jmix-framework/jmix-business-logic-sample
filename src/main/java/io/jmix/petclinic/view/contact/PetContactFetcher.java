package io.jmix.petclinic.view.contact;

import io.jmix.core.DataManager;
import io.jmix.core.Id;
import io.jmix.core.Messages;
import io.jmix.petclinic.entity.owner.Owner;
import io.jmix.petclinic.entity.pet.Pet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
            String address = formatOwnerAddress(owner);

            if (isAvailable(telephone)) {
                return createContact(telephone, ContactType.TELEPHONE);
            } else if (isAvailable(email)) {
                return createContact(email, ContactType.EMAIL);
            } else if (isAvailable(address)) {
                return createContact(address, ContactType.ADDRESS);
            } else {
                return Optional.empty();
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

    private boolean isAvailable(String contactOption) {
        return StringUtils.isNotBlank(contactOption);
    }
}
