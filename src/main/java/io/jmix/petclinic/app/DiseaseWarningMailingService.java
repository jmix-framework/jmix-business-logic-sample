package io.jmix.petclinic.app;

import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.email.EmailInfo;
import io.jmix.email.EmailInfoBuilder;
import io.jmix.email.Emailer;
import io.jmix.petclinic.entity.pet.Pet;
import io.jmix.petclinic.entity.pet.PetType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class DiseaseWarningMailingService {


    private final Emailer emailer;
    private final DataManager dataManager;

    public DiseaseWarningMailingService(Emailer emailer, DataManager dataManager) {
        this.emailer = emailer;
        this.dataManager = dataManager;
    }

    public int warnAboutDisease(PetType petType, String disease, String city) {

        List<Pet> petsInDiseaseCity = findPetsInDiseaseCity(petType, city);

        List<Pet> petsWithEmail = filterPetsWithValidOwnersEmail(petsInDiseaseCity);

        petsWithEmail.forEach(pet -> sendEmailToPetsOwner(pet, disease, city));

        return petsWithEmail.size();
    }

    private List<Pet> filterPetsWithValidOwnersEmail(List<Pet> petsInDiseaseCity) {
        return petsInDiseaseCity
                .stream()
                .filter(pet -> StringUtils.hasText(pet.getOwner().getEmail()))
                .toList();
    }

    private void sendEmailToPetsOwner(Pet pet, String disease, String city) {
        String emailBody = """
                Hello %s,
                
                In the area of %s the following disease have been reported: %s.
                In case your Pet %s shows any unusual behavior, please approach Jmix Petclinic.
                
                Yours sincerely,
                
                Jmix Petclinic Inc.
                """
                .formatted(
                        pet.getOwner().getFullName(),
                        city,
                        disease,
                        pet.getName()
                );
        EmailInfo email = EmailInfoBuilder.create()
                .setAddresses(pet.getOwner().getEmail())
                .setSubject("Warning about %s in the Area of %s".formatted(disease, city))
                .setBody(emailBody)
                .build();

        emailer.sendEmailAsync(email);


    }

    private List<Pet> findPetsInDiseaseCity(PetType petType, String city) {
        return dataManager.load(Pet.class)
                .query(
                        "select e from petclinic_Pet e where e.owner.city = :ownerCity and e.type = :petType")
                .parameter("ownerCity", city)
                .parameter("petType", petType)
                .fetchPlan(pet -> {
                    pet.addFetchPlan(FetchPlan.BASE);
                    pet.add("owner", FetchPlan.BASE);
                    pet.add("type", FetchPlan.BASE);
                })
                .list();
    }
}