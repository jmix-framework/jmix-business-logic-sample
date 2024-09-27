package io.jmix.petclinic.view.pet.pet;


import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.view.*;
import io.jmix.petclinic.service.DiseaseWarningMailingService;
import io.jmix.petclinic.entity.pet.PetType;
import io.jmix.petclinic.view.main.MainView;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "create-disease-warning-mailing", layout = MainView.class)
@ViewController("petclinic_CreateDiseaseWarningMailing")
@ViewDescriptor("create-disease-warning-mailing.xml")
public class CreateDiseaseWarningMailing extends StandardView {

    @ViewComponent
    private Select<PetType> petType;
    @ViewComponent
    private TypedTextField<String> city;
    @ViewComponent
    private TypedTextField<String> disease;
    @Autowired
    private Notifications notifications;
    @ViewComponent
    private MessageBundle messageBundle;


    // tag::disease-warning-service-usage[]
    @Autowired
    private DiseaseWarningMailingService diseaseWarningMailingService;

    @Subscribe("createDiseaseWarningMailing")
    public void onCreateDiseaseWarningMailing(final ActionPerformedEvent event) {

        int notifiedPets = diseaseWarningMailingService.warnAboutDisease(
                petType.getValue(),
                disease.getValue(),
                city.getValue()
        );

        close(StandardOutcome.SAVE)
                .then(() ->
                        notifications.create(messageBundle.formatMessage("ownersNotified", String.valueOf(notifiedPets)))
                        .withType(Notifications.Type.SUCCESS)
                        .show()
                );
    }
    // end::disease-warning-service-usage[]
}