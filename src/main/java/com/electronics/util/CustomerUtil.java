package com.electronics.util;

import com.electronics.entity.Customer;
import com.electronics.exception.CustomerNotFoundException;
import com.electronics.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerUtil {

    private final CustomerRepository customerRepository;

    public Customer findCustomerOrThrow(Integer customerId) {
        return customerRepository.findByIdAndDeletedFalse(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }
}
