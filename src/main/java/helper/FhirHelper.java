package helper;

import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.LoggerFactory;
import service.FhirClient;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class FhirHelper {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);
    private FhirClient fhirClient;

    public FhirHelper(FhirClient fhirClient) {
        this.fhirClient = fhirClient;
    }

    /**
     * Searches for patients based on the specified family name and prints their information, sorted alphabetically.
     * @param familyName The family name of the patients to search for
     */
    public void searchAndPrintPatients(String familyName) {
        try {
            List<Patient> patients = fhirClient.searchPatientsByLastName(familyName);
            // Sorting patients by the first name containing search string <familyName> (case insensitive)
            patients.sort(Comparator.comparing(patient -> getFirstNameForMatchingFamilyName(patient, familyName)));

            // Printing patient information
            for (Patient patient : patients) {
                Optional<HumanName> filteredNameList = patient.getName().stream()
                        .filter(name -> name.getFamily().equalsIgnoreCase(familyName))
                        .findFirst();

                filteredNameList.ifPresent(name -> {
                    System.out.println("First Name: " + name.getGiven().get(0).getValue());
                    System.out.println("Last Name: " + name.getFamily());
                    System.out.println("Birth Date: " + (patient.getBirthDate()!=null?patient.getBirthDate():"Not available in records"));
                    System.out.println(/*patient.getId()+*/"-------------------------");
                });
            }
        } catch (FHIRException e) {
            log.error("Error interacting with FHIR server::", e.getMessage(), e);
        } catch (NullPointerException npe){
            log.error("NPE parsing response: Format not as expected::", npe.getMessage(),npe);
        } catch (Exception e) {
            log.error("Error in searchAndPrintPatients::",e.getMessage(),e);
        }
    }

    /**
     * Returns the first Name of patients based on their specified family name.
     * For patients who have multiple names, will search for the object with the matching family name
     * @param patient The entire patient object
     * @param familyName The family name of the patients to search for
     * @return the firstName of the patient which matches the family name.
     */
    private String getFirstNameForMatchingFamilyName(Patient patient, String familyName) {
        return patient.getName().stream()
                .filter(name -> name.getFamily().equalsIgnoreCase(familyName))
                .flatMap(name -> name.getGiven().stream())
                .map(given -> given.getValue().toUpperCase())
                .findFirst().orElse("");
    }
}
