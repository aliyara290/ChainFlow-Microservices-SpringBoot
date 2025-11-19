package com.aliyara.customerservice.integration;

import com.aliyara.customerservice.dto.request.AdresseRequestDTO;
import com.aliyara.customerservice.dto.request.CustomerRequestDTO;
import com.aliyara.customerservice.model.Adresse;
import com.aliyara.customerservice.model.Customer;
import com.aliyara.customerservice.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;
    private Adresse testAdresse;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();

        testAdresse = new Adresse();
        testAdresse.setStreet("123 Main St");
        testAdresse.setCity("New York");
        testAdresse.setState("NY");
        testAdresse.setCountry("USA");
        testAdresse.setZip("10001");

        testCustomer = new Customer();
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setPhone("+1234567890");
        testCustomer.setAdresse(testAdresse);
    }

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("Test create customer endpoint")
    void testCreateCustomer() throws Exception {
        AdresseRequestDTO adresseDTO = AdresseRequestDTO.builder()
                .street("123 Main St")
                .city("New York")
                .state("NY")
                .country("USA")
                .zip("10001")
                .build();

        CustomerRequestDTO requestDTO = CustomerRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .adresse(adresseDTO)
                .build();

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Customer created successfully"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"));
    }

    @Test
    @DisplayName("Test get all customers endpoint")
    void testGetAllCustomers() throws Exception {
        customerRepository.save(testCustomer);

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].firstName").value("John"));
    }

    @Test
    @DisplayName("Test get customer by id endpoint")
    void testGetCustomerById() throws Exception {
        Customer savedCustomer = customerRepository.save(testCustomer);

        mockMvc.perform(get("/api/v1/customers/" + savedCustomer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.id").value(savedCustomer.getId()))
                .andExpect(jsonPath("$.data.firstName").value("John"));
    }

    @Test
    @DisplayName("Test update customer endpoint")
    void testUpdateCustomer() throws Exception {
        Customer savedCustomer = customerRepository.save(testCustomer);

        AdresseRequestDTO adresseDTO = AdresseRequestDTO.builder()
                .street("456 Oak Ave")
                .city("Los Angeles")
                .state("CA")
                .country("USA")
                .zip("90001")
                .build();

        CustomerRequestDTO updateDTO = CustomerRequestDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phone("+1987654321")
                .adresse(adresseDTO)
                .build();

        mockMvc.perform(put("/api/v1/customers/" + savedCustomer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.firstName").value("Jane"))
                .andExpect(jsonPath("$.data.email").value("jane.smith@example.com"));
    }

    @Test
    @DisplayName("Test delete customer endpoint")
    void testDeleteCustomer() throws Exception {
        Customer savedCustomer = customerRepository.save(testCustomer);

        mockMvc.perform(delete("/api/v1/customers/" + savedCustomer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Customer deleted successfully"));
    }

    @Test
    @DisplayName("Test create customer with duplicate email")
    void testCreateCustomerDuplicateEmail() throws Exception {
        customerRepository.save(testCustomer);

        AdresseRequestDTO adresseDTO = AdresseRequestDTO.builder()
                .street("789 Elm St")
                .city("Chicago")
                .state("IL")
                .country("USA")
                .zip("60601")
                .build();

        CustomerRequestDTO requestDTO = CustomerRequestDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("john.doe@example.com")  // Duplicate email
                .phone("+1987654321")
                .adresse(adresseDTO)
                .build();

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    @DisplayName("Test get customer by id not found")
    void testGetCustomerByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/customers/non-existent-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    @DisplayName("Test create customer with validation error")
    void testCreateCustomerValidationError() throws Exception {
        CustomerRequestDTO requestDTO = CustomerRequestDTO.builder()
                .firstName("")  // Invalid: blank
                .lastName("")   // Invalid: blank
                .email("invalid-email")  // Invalid: not a valid email
                .phone("")  // Invalid: blank
                .build();

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }
}