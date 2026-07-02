package com.electronics.service;

import com.electronics.entity.Customer;
import com.electronics.entity.Merchant;
import com.electronics.entity.Order;
import com.electronics.exception.CustomerNotFoundException;
import com.electronics.exception.InvalidRequestException;
import com.electronics.exception.MerchantNotFoundException;
import com.electronics.exception.OrderNotFoundException;
import com.electronics.repository.CustomerRepository;
import com.electronics.repository.MerchantRepository;
import com.electronics.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void softDeleteCustomer(Integer id) {
        Customer customer = customerRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new CustomerNotFoundException(id));
        customer.setDeleted(true);
        customerRepository.save(customer);
    }

    @Transactional
    public void softDeleteMerchant(Integer id) {
        Merchant merchant = merchantRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new MerchantNotFoundException(id));
        merchant.setDeleted(true);
        merchantRepository.save(merchant);
    }

    @Transactional
    public void cancelOrder(Integer id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            throw new InvalidRequestException("Order is already cancelled", Map.of("id", id));
        }
        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }
}
