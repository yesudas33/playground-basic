package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;


public class ConfigUtilityTest {

    private Properties properties;

    @BeforeEach
    public void setUp() {
        properties = new Properties();
        properties.setProperty("FHIR_BASE_URL", "http://hapi.fhir.org/baseR4");
    }

    @Test
    public void testLoadConfigValue() {
        // Test when key exists
        Assertions.assertEquals("http://hapi.fhir.org/baseR4", ConfigUtility.loadConfigValue("FHIR_BASE_URL"));

        // Test when key does not exist
        Assertions.assertThrows(RuntimeException.class, () -> ConfigUtility.loadConfigValue("key1"));
    }

    @Test
    public void testLoadConfigValueWithBlank() {
        // Test when properties is null
        Assertions.assertEquals("", ConfigUtility.loadConfigValue("FHIR_BASE_URL_V2"));
    }

    @Test
    public void testLoadConfigValueWithNull() {
        // Test when properties is null
        Assertions.assertThrows(RuntimeException.class, () -> ConfigUtility.loadConfigValue("FHIR_BASE_URL_V3"));
    }
    @Test
    public void testReadLastNamesFromFile() throws IOException {

        // Call the method to read last names from the file
        List<String> actualLastNames = ConfigUtility.readLastNamesFromFile("names.txt");

        // Compare the expected and actual lists of last names
        assertNotNull(actualLastNames);
        // Test when file does not exist
        Assertions.assertThrows(NullPointerException.class, () -> ConfigUtility.readLastNamesFromFile("nonexistent.txt"));

    }
}
