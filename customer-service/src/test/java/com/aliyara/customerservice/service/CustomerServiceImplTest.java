package com.aliyara.customerservice.service;

import com.aliyara.customerservice.dto.request.AdresseRequestDTO;
import com.aliyara.customerservice.dto.request.CustomerRequestDTO;
import com.aliyara.customerservice.dto.response.AdresseResponseDTO;
import com.aliyara.customerservice.dto.response.CustomerResponseDTO;
import com.aliyara.customerservice.exception.FailedToInsertDataException;
import com.aliyara.customerservice.exception.NoRecordFoundException;
import com.aliyara.customerservice.exception.RecordNotFoundException;
import com.aliyara.customerservice.mapper.CustomerMapper;
import com.aliyara.customerservice.model.Adresse;
import com.aliyara.customerservice.model.Customer;
import com.aliyara.customerservice.payload.ApiResponse;
import com.aliyara.customerservice.repository.CustomerRepository;
import com.aliyara.customerservice.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerRequestDTO requestDTO;
    private CustomerResponseDTO responseDTO;
    private Adresse adresse;
    private AdresseRequestDTO adresseRequestDTO;
    private AdresseResponseDTO adresseResponseDTO;

    @BeforeEach
    void setUp() {
        adresse = new Adresse();
        adresse.setId(UUID.randomUUID().toString());
        adresse.setStreet("123 Main St");
        adresse.setCity("New York");
        adresse.setState("NY");
        adresse.setCountry("USA");
        adresse.setZip("10001");

        customer = new Customer();
        customer.setId(UUID.randomUUID().toString());
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhone("+1234567890");
        customer.setAdresse(adresse);

        adresseRequestDTO = AdresseRequestDTO.builder()
                .street("123 Main St")
                .city("New York")
                .state("NY")
                .country("USA")
                .zip("10001")
                .build();

        requestDTO = CustomerRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .adresse(adresseRequestDTO)
                .build();

        adresseResponseDTO = AdresseResponseDTO.builder()
                .id(adresse.getId())
                .street("123 Main St")
                .city("New York")
                .state("NY")
                .country("USA")
                .zip("10001")
                .build();

        responseDTO = CustomerResponseDTO.builder()
                .id(customer.getId())
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .adresse(adresseResponseDTO)
                .build();
    }

    @Test
    @DisplayName("Test create customer successfully")
    void testCreateCustomer() {
        when(customerRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(customerRepository.existsByPhone(requestDTO.getPhone())).thenReturn(false);
        when(customerMapper.toEntity(requestDTO)).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(responseDTO);

        CustomerResponseDTO result = customerService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");

        verify(customerRepository, times(1)).existsByEmail(requestDTO.getEmail());
        verify(customerRepository, times(1)).existsByPhone(requestDTO.getPhone());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Test create customer throws exception when email exists")
    void testCreateCustomerEmailExists() {
        when(customerRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        assertThrows(FailedToInsertDataException.class, () -> {
            customerService.create(requestDTO);
        });

        verify(customerRepository, times(1)).existsByEmail(requestDTO.getEmail());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Test create customer throws exception when phone exists")
    void testCreateCustomerPhoneExists() {
        when(customerRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(customerRepository.existsByPhone(requestDTO.getPhone())).thenReturn(true);

        assertThrows(FailedToInsertDataException.class, () -> {
            customerService.create(requestDTO);
        });

        verify(customerRepository, times(1)).existsByEmail(requestDTO.getEmail());
        verify(customerRepository, times(1)).existsByPhone(requestDTO.getPhone());
        verify(customerRepository, never()).save(any(Customer.class));
    }

//    @Test
//    @DisplayName("Test update customer successfully")
//    void testUpdateCustomer() {
//        String customerId = customer.getId();
//
//        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
//        when(customerRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
//        when(customerRepository.existsByPhone(requestDTO.getPhone())).thenReturn(false);
//        doNothing().when(customerMapper).updateEntityFromDTO(requestDTO, customer);
//        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
//        when(customerMapper.toResponse(customer)).thenReturn(responseDTO);
//
//        CustomerResponseDTO result = customerService.update(customerId, requestDTO);
//
//        assertThat(result).isNotNull();
//        assertThat(result.getId()).isEqualTo(customerId);
//
//        verify(customerRepository, times(1)).findById(customerId);
//        verify(customerRepository, times(1)).save(any(Customer.class));
//    }

    @Test
    @DisplayName("Test update customer throws exception when not found")
    void testUpdateCustomerNotFound() {
        String customerId = UUID.randomUUID().toString();

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            customerService.update(customerId, requestDTO);
        });

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Test delete customer successfully")
    void testDeleteCustomer() {
        String customerId = customer.getId();

        when(customerRepository.existsById(customerId)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(customerId);

        ApiResponse<Void> result = customerService.delete(customerId);

        assertThat(result).isNotNull();
        assertThat(result.status()).isTrue();
        assertThat(result.message()).isEqualTo("Customer deleted successfully");

        verify(customerRepository, times(1)).existsById(customerId);
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    @DisplayName("Test delete customer throws exception when not found")
    void testDeleteCustomerNotFound() {
        String customerId = UUID.randomUUID().toString();

        when(customerRepository.existsById(customerId)).thenReturn(false);

        assertThrows(RecordNotFoundException.class, () -> {
            customerService.delete(customerId);
        });

        verify(customerRepository, times(1)).existsById(customerId);
        verify(customerRepository, never()).deleteById(customerId);
    }

    @Test
    @DisplayName("Test find customer by id successfully")
    void testFindById() {
        String customerId = customer.getId();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(responseDTO);

        CustomerResponseDTO result = customerService.findById(customerId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(customerId);
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerMapper, times(1)).toResponse(customer);
    }

    @Test
    @DisplayName("Test find customer by id throws exception when not found")
    void testFindByIdNotFound() {
        String customerId = UUID.randomUUID().toString();

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            customerService.findById(customerId);
        });

        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    @DisplayName("Test find all customers successfully")
    void testFindAll() {
        List<Customer> customers = List.of(customer);

        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.toResponse(customer)).thenReturn(responseDTO);

        List<CustomerResponseDTO> result = customerService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("john.doe@example.com");

        verify(customerRepository, times(1)).findAll();
        verify(customerMapper, times(1)).toResponse(customer);
    }

    @Test
    @DisplayName("Test find all customers throws exception when empty")
    void testFindAllEmpty() {
        when(customerRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(NoRecordFoundException.class, () -> {
            customerService.findAll();
        });

        verify(customerRepository, times(1)).findAll();
    }
}