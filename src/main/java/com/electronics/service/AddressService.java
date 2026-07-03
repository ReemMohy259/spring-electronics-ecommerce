package com.electronics.service;

import com.electronics.dto.AddressRequest;
import com.electronics.dto.AddressResponse;
import com.electronics.dto.UpdateAddressRequest;
import com.electronics.entity.Address;
import com.electronics.entity.User;
import com.electronics.exception.AddressNotFoundException;
import com.electronics.exception.InvalidRequestException;
import com.electronics.exception.UserNotFoundException;
import com.electronics.repository.AddressRepository;
import com.electronics.repository.UserRepository;
import com.electronics.util.CurrentUserDataUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CurrentUserDataUtil currentUserDataUtil;

    private User getCurrentUser() {
        String email = currentUserDataUtil.getCurrentUserEmail();

        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(email));
    }

    public void addAddress(AddressRequest request) {
        User user = getCurrentUser();

        Address address = new Address();
        address.setUser(user);
        address.setAddressName(request.addressName());
        address.setGovernment(request.government());
        address.setCity(request.city());
        address.setStreet(request.street());
        address.setBuildingNo(request.buildingNo());
        address.setDescription(request.description());

        addressRepository.save(address);
    }

    public List<AddressResponse> getAddresses() {
        String email = currentUserDataUtil.getCurrentUserEmail();

        return addressRepository.findByUser_Email(email)
            .stream()
            .map(
                a -> new AddressResponse(
                    a.getId(),
                    a.getAddressName(),
                    a.getGovernment(),
                    a.getCity(),
                    a.getStreet(),
                    a.getBuildingNo(),
                    a.getDescription()))
            .toList();
    }

    public AddressResponse getAddress(Integer addressId) {

        return addressRepository.findById(addressId)
            .map(
                a -> new AddressResponse(
                    a.getId(),
                    a.getAddressName(),
                    a.getGovernment(),
                    a.getCity(),
                    a.getStreet(),
                    a.getBuildingNo(),
                    a.getDescription()))
            .orElseThrow(() -> new AddressNotFoundException(addressId));
    }

    public void deleteAddress(Integer id) {
        String email = currentUserDataUtil.getCurrentUserEmail();

        Address address = addressRepository.findById(id)
            .orElseThrow(() -> new AddressNotFoundException(id));

        if (!address.getUser().getEmail().equals(email)) {
            throw new InvalidRequestException("Not allowed, ownership required");
        }

        addressRepository.delete(address);
    }

    public void updateAddress(UpdateAddressRequest request) {

        String email = currentUserDataUtil.getCurrentUserEmail();

        Address address = addressRepository.findById(request.getId())
            .orElseThrow(() -> new AddressNotFoundException(request.getId()));

        if (!address.getUser().getEmail().equals(email)) {
            throw new InvalidRequestException("Not allowed, ownership required");
        }

        if (request.getAddressName() != null) {
            address.setAddressName(request.getAddressName());
        }
        if (request.getCity() != null) {
            address.setCity(request.getCity());
        }
        if (request.getBuildingNo() != null) {
            address.setBuildingNo(request.getBuildingNo());
        }
        if (request.getDescription() != null) {
            address.setDescription(request.getDescription());
        }
        if (request.getStreet() != null) {
            address.setStreet(request.getStreet());
        }
        if (request.getGovernment() != null) {
            address.setGovernment(request.getGovernment());
        }

        addressRepository.save(address);
    }
}
