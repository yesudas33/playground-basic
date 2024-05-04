import helper.FhirHelper;
import service.FhirClient;

public class SampleClient {
    private static final int LOOP_COUNT=3;
    private static final int NAMES_COUNT=20;

    public static void main(String[] theArgs) {

        // Create a FHIR client
       /* FhirContext fhirContext = FhirContext.forR4();
        IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
        client.registerInterceptor(new LoggingInterceptor(false));

        // Search for Patient resources
        Bundle response = client
                .search()
                .forResource("Patient")
                .where(Patient.FAMILY.matches().value("SMITH"))
                .returnBundle(Bundle.class)
                .execute();*/
        FhirClient fhirClient = new FhirClient(LOOP_COUNT,NAMES_COUNT);
        FhirHelper fhirHelper = new FhirHelper(fhirClient);
        //fhirHelper.searchAndPrintPatients("SMITH");   //Task 1
        fhirHelper.timePatientSearchResponse(LOOP_COUNT,NAMES_COUNT);         //Task 2
    }

}
