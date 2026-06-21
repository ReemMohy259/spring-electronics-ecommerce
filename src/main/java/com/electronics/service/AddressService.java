package com.electronics.service;

import com.electronics.dto.AddressRequest;
import com.electronics.dto.AddressResponse;
import com.electronics.entity.Address;
import com.electronics.entity.User;
import com.electronics.exception.AddressNotFoundException;
import com.electronics.exception.InvalidRequestException;
import com.electronics.exception.UserNotFoundException;
import com.electronics.repository.AddressRepository;
import com.electronics.repository.UserRepository;
import com.electronics.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public void addAddress(AddressRequest request) {
        User user = getCurrentUser();

        Address address = new Address();
        address.setUser(user);
        address.setGovernment(request.government());
        address.setCity(request.city());
        address.setStreet(request.street());
        address.setBuildingNo(request.buildingNo());
        address.setDescription(request.description());

        addressRepository.save(address);
    }

    public List<AddressResponse> getAddresses() {
        String email = SecurityUtil.getCurrentUserEmail();

        return addressRepository.findByUser_Email(email).stream()
                .map(a -> new AddressResponse(a.getId(), a.getGovernment(), a.getCity(),
                        a.getStreet(), a.getBuildingNo(), a.getDescription()))
                .toList();
    }

    public void deleteAddress(Integer id) {
        String email = SecurityUtil.getCurrentUserEmail();

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException(id));

        if (!address.getUser().getEmail().equals(email)) {
            throw new InvalidRequestException("Not allowed, ownership required");
        }

        addressRepository.delete(address);
    }
}
