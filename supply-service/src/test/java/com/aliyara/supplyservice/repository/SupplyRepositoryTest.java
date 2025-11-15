package com.aliyara.supplyservice.repository;

import com.aliyara.supplyservice.model.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class SupplyRepositoryTest {

    @Autowired
    private SupplierRepository supplierRepository;

    @Test
    public void testSaveSupplier() {
        Supplier supplier = Supplier.builder()
                .firstName("Ali")
                .lastName("Yara")
                .email("ali@gmail.com")
                .phone("0643569435")
                .rating(5.6)
                .leadTime(10)
                .build();

        Supplier savedSupplier = supplierRepository.save(supplier);

        assertThat(savedSupplier).isNotNull();
        assertThat(savedSupplier.getId()).isNotNull();
        assertThat(savedSupplier.getFirstName()).isEqualTo("Ali");
        assertThat(savedSupplier.getEmail()).isEqualTo("ali@gmail.com");
    }

    @Test
    public void testFindById() {
        Supplier supplier = Supplier.builder()
                .firstName("Ali")
                .lastName("Yara")
                .email("ali@gmail.com")
                .phone("0643569435")
                .build();
        Supplier savedSupplier = supplierRepository.save(supplier);

        Optional<Supplier> foundSupplier = supplierRepository.findById(savedSupplier.getId());

        assertThat(foundSupplier).isPresent();
        assertThat(foundSupplier.get().getFirstName()).isEqualTo("Ali");
        assertThat(foundSupplier.get().getEmail()).isEqualTo("ali@gmail.com");
    }

    @Test
    public void testDeleteSupplier() {
        Supplier supplier = Supplier.builder()
                .firstName("Ali")
                .lastName("Yara")
                .email("ali@gmail.com")
                .phone("0643569435")
                .build();
        Supplier savedSupplier = supplierRepository.save(supplier);
        String supplierId = savedSupplier.getId();

        supplierRepository.deleteById(supplierId);

        assertThat(supplierRepository.existsById(supplierId)).isFalse();
    }

    @Test
    public void testUpdateSupplier() {
        Supplier supplier = Supplier.builder()
                .firstName("Ali")
                .lastName("Yara")
                .email("ali@gmail.com")
                .phone("0643569435")
                .rating(5.6)
                .build();
        Supplier savedSupplier = supplierRepository.save(supplier);

        savedSupplier.setFirstName("Ahmed");
        savedSupplier.setRating(4.8);
        Supplier updatedSupplier = supplierRepository.save(savedSupplier);

        assertThat(updatedSupplier.getFirstName()).isEqualTo("Ahmed");
        assertThat(updatedSupplier.getRating()).isEqualTo(4.8);
        assertThat(updatedSupplier.getLastName()).isEqualTo("Yara");
    }

    @Test
    public void testFindAll() {
        Supplier supplier1 = Supplier.builder()
                .firstName("Ali")
                .lastName("Yara")
                .email("ali@gmail.com")
                .phone("0643569435")
                .build();

        Supplier supplier2 = Supplier.builder()
                .firstName("Sara")
                .lastName("Smith")
                .email("sara@gmail.com")
                .phone("0643569436")
                .build();

        supplierRepository.save(supplier1);
        supplierRepository.save(supplier2);

        long count = supplierRepository.count();

        assertThat(count).isGreaterThanOrEqualTo(2);
    }
}