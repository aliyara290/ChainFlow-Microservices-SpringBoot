package com.aliyara.customerservice.service.impl;


import com.aliyara.customerservice.dto.request.AdresseRequestDTO;
import com.aliyara.customerservice.dto.request.DeliveryRequestDTO;
import com.aliyara.customerservice.dto.response.DeliveryResponseDTO;
import com.aliyara.customerservice.exception.FailedToInsertDataException;
import com.aliyara.customerservice.exception.NoRecordFoundException;
import com.aliyara.customerservice.exception.RecordNotFoundException;
import com.aliyara.customerservice.mapper.AdresseMapper;
import com.aliyara.customerservice.mapper.DeliveryMapper;
import com.aliyara.customerservice.model.*;
import com.aliyara.customerservice.repository.*;
import com.aliyara.customerservice.service.interfaces.DeliveryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final AdresseRepository adresseRepository;
    private final AdresseMapper adresseMapper;
    private final DriverRepository driverRepository;
    private final DeliveryMapper deliveryMapper;

    @Override
    public DeliveryResponseDTO createDelivery(DeliveryRequestDTO deliveryRequestDTO) {
        try {
            Vehicle vehicle = vehicleRepository.findById(deliveryRequestDTO.getVehicleId())
                    .orElseThrow(() -> new RecordNotFoundException("Vehicle not found with id: " + deliveryRequestDTO.getVehicleId()));

            Driver driver = driverRepository.findById(deliveryRequestDTO.getDriverId())
                    .orElseThrow(() -> new RecordNotFoundException("Driver not found with id: " + deliveryRequestDTO.getDriverId()));

            Order order = orderRepository.findById(deliveryRequestDTO.getOrderId())
                    .orElseThrow(() -> new RecordNotFoundException("Order not found with id: " + deliveryRequestDTO.getOrderId()));

            Adresse deliveryAdresse;
            if (deliveryRequestDTO.getAdresse() == null) {
                if (order.getCustomer() == null) {
                    throw new RecordNotFoundException("Customer not found for order: " + deliveryRequestDTO.getOrderId());
                }

                Customer customer = customerRepository.findById(order.getCustomer().getId())
                        .orElseThrow(() -> new RecordNotFoundException("Customer not found with id: " + order.getCustomer().getId()));

                if (customer.getAdresse() == null) {
                    throw new RecordNotFoundException("Customer address not found");
                }

                deliveryAdresse = customer.getAdresse();
            } else {
                deliveryAdresse = adresseMapper.toEntity(deliveryRequestDTO.getAdresse());
                deliveryAdresse = adresseRepository.save(deliveryAdresse);
            }

            Delivery delivery = deliveryMapper.toEntity(deliveryRequestDTO);
            delivery.setVehicle(vehicle);
            delivery.setDriver(driver);
            delivery.setOrder(order);
            delivery.setAdresse(deliveryAdresse);

            Delivery savedDelivery = deliveryRepository.save(delivery);
            return deliveryMapper.toResponse(savedDelivery);

        } catch (RecordNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FailedToInsertDataException("Failed to create delivery: " + e.getMessage());
        }
    }

    @Override
    public DeliveryResponseDTO updateDelivery(DeliveryRequestDTO deliveryRequestDTO, String id) {
        try {
            Delivery existingDelivery = deliveryRepository.findById(id)
                    .orElseThrow(() -> new RecordNotFoundException("Delivery"));

            if (deliveryRequestDTO.getStatus() != null) {
                existingDelivery.setStatus(deliveryRequestDTO.getStatus());
            }
            if (deliveryRequestDTO.getDate() != null) {
                existingDelivery.setDate(deliveryRequestDTO.getDate());
            }
            if (deliveryRequestDTO.getCost() != null) {
                existingDelivery.setCost(deliveryRequestDTO.getCost());
            }

            if (deliveryRequestDTO.getVehicleId() != null) {
                Vehicle vehicle = vehicleRepository.findById(deliveryRequestDTO.getVehicleId())
                        .orElseThrow(() -> new RecordNotFoundException("Vehicle"));
                existingDelivery.setVehicle(vehicle);
            }

            if (deliveryRequestDTO.getDriverId() != null) {
                Driver driver = driverRepository.findById(deliveryRequestDTO.getDriverId())
                        .orElseThrow(() -> new RecordNotFoundException("Driver"));
                existingDelivery.setDriver(driver);
            }

            if (deliveryRequestDTO.getOrderId() != null) {
                Order order = orderRepository.findById(deliveryRequestDTO.getOrderId())
                        .orElseThrow(() -> new RecordNotFoundException("Order"));
                existingDelivery.setOrder(order);
            }

            if (deliveryRequestDTO.getAdresse() != null) {
                Adresse newAddress = adresseMapper.toEntity(deliveryRequestDTO.getAdresse());
                Adresse savedAdresse = adresseRepository.save(newAddress);
                existingDelivery.setAdresse(savedAdresse);
            }

            Delivery updatedDelivery = deliveryRepository.save(existingDelivery);
            return deliveryMapper.toResponse(updatedDelivery);

        } catch (RecordNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FailedToInsertDataException("Failed to update delivery: " + e.getMessage());
        }
    }

    @Override
    public DeliveryResponseDTO getDelivery(String id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Delivery"));
        return deliveryMapper.toResponse(delivery);
    }

    @Override
    public List<DeliveryResponseDTO> getDeliveries() {
        List<Delivery> deliveries = deliveryRepository.findAll();
        if (deliveries.isEmpty()) {
            throw new NoRecordFoundException("No deliveries found");
        }
        return deliveries.stream()
                .map(deliveryMapper::toResponse)
                .collect(Collectors.toList());
    }
}
