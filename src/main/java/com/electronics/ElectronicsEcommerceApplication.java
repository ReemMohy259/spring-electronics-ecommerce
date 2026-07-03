package com.electronics;

import com.electronics.entity.Category;
import com.electronics.entity.Customer;
import com.electronics.entity.Merchant;
import com.electronics.entity.Role;
import com.electronics.repository.CategoryRepository;
import com.electronics.repository.MerchantRepository;
import com.electronics.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class ElectronicsEcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElectronicsEcommerceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(
        UserRepository userRepository,
        MerchantRepository merchantRepository,
        CategoryRepository categoryRepository) {
        return args -> {
            // Customer user = new Customer();
            // user.setFirstName("John");
            // user.setLastName("Doe");
            // user.setUsername("johndoe552");
            // user.setEmail("johndoe@gmail.com");
            // user.setPassword("12345678");
            // user.setRole(Role.CUSTOMER);
            // user.setBirthDate(LocalDate.of(2002, 6, 17));
            // userRepository.save(user);
            //
            // Merchant merchant = new Merchant();
            // merchant.setEmail("omarali552@gmail.com");
            // merchant.setBusinessName("Laptops");
            // merchant.setDeleted(false);
            // merchant.setAbout("This is a merchant");
            // merchantRepository.save(merchant);

            // Category category1 = new Category();
            // category1.setName("Mobile");
            //
            // Category category2 = new Category();
            // category2.setName("Tablet");
            //
            // Category category3 = new Category();
            // category3.setName("PC");
            //
            // categoryRepository.saveAll(List.of(category2, category1, category3));
        };
    }
}
