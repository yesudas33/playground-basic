package helper;

import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.LoggerFactory;
import service.ConfigUtility;
import service.FhirClient;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class FhirHelper {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(FhirHelper.class);
    private static final String NAMES_FILE_PATH = "names.txt";
    private FhirClient fhirClient;

    public FhirHelper(FhirClient fhirClient) {
        this.fhirClient = fhirClient;
    }

    /**
     * Searches for patients based on the specified family name and prints their information, sorted alphabetically.
     * @param familyName The family name of the patients to search for
     */
    public List<Patient> searchAndPrintPatients(String familyName) {
        List<Patient> patients =null;
        try {
            patients= fhirClient.searchPatientsByLastName(familyName,false);
            // Sorting patients by the first name containing search string <familyName> (case insensitive)
            patients.sort(Comparator.comparing(patient -> getFirstNameForMatchingFamilyName(patient, familyName)));

            // Printing patient information
            for (Patient patient : patients) {
                Optional<HumanName> filteredNameList = patient.getName().stream()
                        .filter(name -> name.getFamily().equalsIgnoreCase(familyName))
                        .findFirst();

                filteredNameList.ifPresent(name -> {
                    log.info("First Name: " + name.getGiven().get(0).getValue());
                    log.info("Last Name: " + name.getFamily());
                    log.info("Birth Date: " + (patient.getBirthDate()!=null?patient.getBirthDate():"Not available in records"));
                    log.info(/*patient.getId()+*/"-------------------------");
                });
            }
        } catch (FHIRException e) {
            log.error("Error interacting with FHIR server::", e.getMessage(), e);
        } catch (NullPointerException npe){
            log.error("NPE parsing response: Format not as expected::", npe.getMessage(),npe);
        } catch (Exception e) {
            log.error("Error in searchAndPrintPatients::",e.getMessage(),e);
        }
        return patients;
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

    /**
     * Performs searches for patients with last names from the provided list and prints the average response time.
     *@param LOOP_COUNT The number of times the patient list should be repetitively fetched.
     * @param NAMES_COUNT The number of last names in the file
     */
    public void timePatientSearchResponse(int LOOP_COUNT, int NAMES_COUNT) {
        try {
            List<String> lastNames = ConfigUtility.readLastNamesFromFile(NAMES_FILE_PATH);

            for (int i = 0; i < LOOP_COUNT; i++) {
                boolean disableCache = (i == LOOP_COUNT - 1); // Disable caching for the last loop
                for (int j = 0; j < NAMES_COUNT; j++) {
                    String lastName = lastNames.get(j);
                    fhirClient.searchPatientsByLastName(lastName, disableCache);
                }
                if(i<LOOP_COUNT-1) TimeUnit.SECONDS.sleep(10);  //Added to test cache impact. Introducing delay after each Set
            }
        }catch (FHIRException e) {
            log.error("Error interacting with FHIR server::", e.getMessage(), e);
        } catch (NullPointerException npe){
            log.error("NPE parsing response: Format not as expected::", npe.getMessage(),npe);
        } catch (Exception e) {
            log.error("Error in searchAndPrintPatients::",e.getMessage(),e);
        }
    }
}
