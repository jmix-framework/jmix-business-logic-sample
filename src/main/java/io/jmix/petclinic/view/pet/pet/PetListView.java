package io.jmix.petclinic.view.pet.pet;

import com.vaadin.flow.router.Route;
import io.jmix.core.MetadataTools;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.propertyfilter.PropertyFilter;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.view.*;
import io.jmix.petclinic.entity.pet.Pet;
import io.jmix.petclinic.view.pet.pet.contact.Contact;
import io.jmix.petclinic.view.pet.pet.contact.PetContactDisplay;
import io.jmix.petclinic.view.pet.pet.contact.PetContactFetcher;
import io.jmix.petclinic.view.main.MainView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route(value = "pets", layout = MainView.class)
@ViewController("petclinic_Pet.list")
@ViewDescriptor("pet-list-view.xml")
@LookupComponent("petsDataGrid")
@DialogMode(width = "50em")
public class PetListView extends StandardListView<Pet> {

    @ViewComponent
    private PropertyFilter identificationNumberFilter;
    @ViewComponent
    private PropertyFilter typeFilter;
    @ViewComponent
    private PropertyFilter ownerFilter;
    @ViewComponent
    private DataGrid<Pet> petsDataGrid;
    @Autowired
    private PetContactFetcher petContactFetcher;
    @Autowired
    private Notifications notifications;
    @Autowired
    private PetContactDisplay petContactDisplay;
    @Autowired
    private MetadataTools metadataTools;
    @Autowired
    private DialogWindows dialogWindows;

    @Subscribe("clearFilterAction")
    public void onClearFilterAction(final ActionPerformedEvent event) {
        identificationNumberFilter.clear();
        typeFilter.clear();
        ownerFilter.clear();
    }

    // tag::calculate-discount[]
    @Subscribe("petsDataGrid.calculateDiscount")
    public void onPetsDataGridCalculateDiscount(final ActionPerformedEvent event) {
        Pet pet = petsDataGrid.getSingleSelectedItem();

        int discount = calculateDiscount(pet);

        notifications.create("Discount for %s: %s%%".formatted(metadataTools.getInstanceName(pet), discount))
                .withType(Notifications.Type.DEFAULT)
                .show();
    }

    private int calculateDiscount(Pet pet) {
        int visitAmount = pet.getVisits().size();

        if (visitAmount > 300) {
            return 10;
        } else if (visitAmount > 150) {
            return 5;
        }

        return 0;
    }
    // end::calculate-discount[]


    @Subscribe("petsDataGrid.fetchContact")
    public void onPetsDataGridFetchContact(final ActionPerformedEvent event) {

        Pet pet = petsDataGrid.getSingleSelectedItem();

        Optional<Contact> contactInformation = petContactFetcher.findContact(pet);

        petContactDisplay.displayContact(contactInformation);
    }

    @Subscribe("petsDataGrid.createDiseaseWarningMailing")
    public void onPetsDataGridCreateDiseaseWarningMailing(final ActionPerformedEvent event) {
        dialogWindows.view(this, CreateDiseaseWarningMailing.class)
                .open();
    }
}
