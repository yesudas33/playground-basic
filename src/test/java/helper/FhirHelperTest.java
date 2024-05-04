package helper;

import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import service.FhirClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class FhirHelperTest {

    @Mock
    private FhirClient fhirClient;

    private FhirHelper fhirHelper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fhirHelper = new FhirHelper(fhirClient);
    }

    @Test
    public void testSearchAndPrintPatients_Success() throws FHIRException {
        // Mock data
        List<Patient> patients = new ArrayList<>();
        Patient patient1 = new Patient();
        patient1.addName().setFamily("Smith").addGiven("John");
        patients.add(patient1);
        Patient patient2 = new Patient();
        patient2.addName().setFamily("Smith").addGiven("Adam");
        patients.add(patient2);
        Patient patient3 = new Patient();
        patient3.addName().setFamily("Warden").addGiven("Carol");
        patient3.addName().setFamily("Smith").addGiven("Becky");
        patients.add(patient3);

        when(fhirClient.searchPatientsByLastName("Smith", false)).thenReturn(patients);

        // Call the method under test
        patients=fhirHelper.searchAndPrintPatients("Smith");

        // Test sorting- various conditions
        Assertions.assertTrue(patients.get(0).getName().get(0).getGiven().get(0).getValue().equalsIgnoreCase("Adam"));

        //This covers the scenario where multiple names are present for the same patient
        Assertions.assertTrue(patients.get(1).getName().get(1).getGiven().get(0).getValue().equalsIgnoreCase("Becky"));
        Assertions.assertTrue(patients.get(2).getName().get(0).getGiven().get(0).getValue().equalsIgnoreCase("John"));


    }

    @Test
    public void testSearchAndPrintPatients_FHIRException() throws FHIRException {
        // Mocking FhirClient behavior
        when(fhirClient.searchPatientsByLastName(anyString(), anyBoolean())).thenThrow(FHIRException.class);

        // Call the method under test
        List<Patient> patients =fhirHelper.searchAndPrintPatients("Smith");

        Assertions.assertTrue(patients==null); //for graceful handling of no results or error screen
    }

    @Test
    public void testTimePatientSearchResponse_Success() throws FHIRException, InterruptedException, IOException {
        // Mock data
        int LOOP_COUNT = 3;
        int NAMES_COUNT = 2;

        // Mocking behavior of FhirClient
        when(fhirClient.searchPatientsByLastName("SMITH", false)).thenReturn(null);
        when(fhirClient.searchPatientsByLastName("DAVIDZO", false)).thenReturn(null);

        // Call the method under test
        fhirHelper.timePatientSearchResponse(LOOP_COUNT, NAMES_COUNT);

        // Verifying behavior
        verify(fhirClient, times(LOOP_COUNT * NAMES_COUNT)).searchPatientsByLastName(anyString(), anyBoolean());
        verifyNoMoreInteractions(fhirClient);

    }

    @Test
    public void testTimePatientSearchResponse_FHIRException() throws FHIRException, InterruptedException, IOException {
        // Mock data
        int LOOP_COUNT = 3;
        int NAMES_COUNT = 1;

        // Mocking behavior of FhirClient
        when(fhirClient.searchPatientsByLastName(anyString(), anyBoolean())).thenThrow(FHIRException.class);


        // Call the method under test
        fhirHelper.timePatientSearchResponse(LOOP_COUNT, NAMES_COUNT);

        // Verifying behavior
        verify(fhirClient, times(NAMES_COUNT)).searchPatientsByLastName(anyString(), anyBoolean());
        verifyNoMoreInteractions(fhirClient);

    }


}
