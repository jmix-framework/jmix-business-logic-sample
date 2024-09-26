package io.jmix.petclinic.view.pet.pet;

import java.util.Optional;

import io.jmix.flowui.Notifications;
import io.jmix.petclinic.view.contact.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.text.View;

@Component
public class PetContactDisplay {

    @Autowired
    private Notifications notifications;

    public void displayContact(Optional<Contact> contactInformation) {

        contactInformation
                .ifPresentOrElse(it -> {
                    notifications.create(it.toString())
                            .withType(Notifications.Type.SUCCESS)
                            .show();
                },
                        () -> {

                    notifications.create("No contact information found")
                            .withType(Notifications.Type.ERROR)
                            .show();
                        });
    }
}
