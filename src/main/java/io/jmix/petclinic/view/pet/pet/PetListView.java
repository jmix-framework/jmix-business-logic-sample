package io.jmix.petclinic.view.pet.pet;

import io.jmix.core.Metadata;
import io.jmix.core.MetadataTools;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.propertyfilter.PropertyFilter;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.petclinic.entity.pet.Pet;

import io.jmix.petclinic.view.contact.Contact;
import io.jmix.petclinic.view.contact.PetContactFetcher;
import io.jmix.petclinic.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;
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

    @Subscribe("petsDataGrid.calculateDiscount")
    public void onPetsDataGridCalculateDiscount(final ActionPerformedEvent event) {
        Pet pet = petsDataGrid.getSingleSelectedItem();

        int discount = calculateDiscount(pet);

        showDiscountCalculatedNotification(pet, discount);
    }

    private int calculateDiscount(Pet pet) {
        int discount = 0;

        int visitAmount = pet.getVisits().size();
        if (visitAmount > 300) {
            discount = 10;
        } else if (visitAmount > 150) {
            discount = 5;
        }
        return discount;
    }


    private void showDiscountCalculatedNotification(Pet pet, int discount) {

        String petName = metadataTools.getInstanceName(pet);

        String discountMessage = "Discount for %s: %s%%".formatted(petName, discount);

        notifications.create(discountMessage)
                .withType(Notifications.Type.DEFAULT)
                .show();
    }


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
