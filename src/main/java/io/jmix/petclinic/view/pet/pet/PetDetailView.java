package io.jmix.petclinic.view.pet.pet;

import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.petclinic.entity.pet.Pet;

import io.jmix.petclinic.view.pet.pet.contact.Contact;
import io.jmix.petclinic.view.pet.pet.contact.PetContactDisplay;
import io.jmix.petclinic.view.pet.pet.contact.PetContactFetcher;
import io.jmix.petclinic.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route(value = "pets/:id", layout = MainView.class)
@ViewController("petclinic_Pet.detail")
@ViewDescriptor("pet-detail-view.xml")
@EditedEntityContainer("petDc")
@DialogMode
public class PetDetailView extends StandardDetailView<Pet> {

    // tag::fetch-contact[]
    @Autowired
    private PetContactFetcher petContactFetcher;
    @Autowired
    private PetContactDisplay petContactDisplay;

    @Subscribe("fetchContact")
    public void onFetchContact(final ActionPerformedEvent event) {

        Optional<Contact> contactInformation = petContactFetcher.findContact(getEditedEntity());

        petContactDisplay.displayContact(contactInformation);
    }
    // end::fetch-contact[]
}