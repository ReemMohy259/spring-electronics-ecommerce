package com.electronics;

import com.electronics.entity.Customer;
import com.electronics.entity.Role;
import com.electronics.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
public class ElectronicsEcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElectronicsEcommerceApplication.class, args);
    }

    CommandLineRunner commandLineRunner(
        UserRepository userRepository
    ) {
        return args -> {
            Customer user = new Customer();
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setUsername("johndoe552");
            user.setEmail("johndoe@gmail.com");
            user.setPassword("12345678");
            user.setRole(Role.CUSTOMER);
            user.setBirthDate(LocalDate.of(2002, 6, 17));
            userRepository.save(user);
        };
    }
}
