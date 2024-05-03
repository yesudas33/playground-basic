package service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * service.FhirClient class handles interactions with the FHIR server.
 * It provides methods for searching patients and printing their information.
 */
public class FhirClient {

    // Constants
    private static final String FHIR_BASE_URL = "FHIR_BASE_URL";

    // Dependencies
    private final FhirContext fhirContext;
    private final IGenericClient client;
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    /**
     * Constructs a new instance of service.FhirClient with default configuration.
     * It initializes the FHIR context and client with the base URL retrieved from configuration.
     * @throws RuntimeException if the FHIR base URL is not configured
     */
    public FhirClient() {
        fhirContext = FhirContext.forR4();
        client = fhirContext.newRestfulGenericClient(ConfigUtility.loadConfigValue(FHIR_BASE_URL));
        client.registerInterceptor(new LoggingInterceptor(false));
    }


    /**
     * Searches for patients based on the specified last name and returns a list of matching patients.
     * @param lastName The last name of the patients to search for
     * @return A list of patients matching the specified last name
     */
    public List<Patient> searchPatientsByLastName(String lastName) {
        Bundle response = client
                .search()
                .forResource("Patient")
                .where(Patient.FAMILY.matches().value(lastName))
                .returnBundle(Bundle.class)
                .execute();

        return response.getEntry().stream()
                .map(entry -> (Patient) entry.getResource())
                .collect(Collectors.toList());
    }


}

