package com.electronics.controller;

import com.electronics.dto.AddressRequest;
import com.electronics.dto.AddressResponse;
import com.electronics.dto.UpdateAddressRequest;
import com.electronics.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/profile/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public void add(@Valid @RequestBody AddressRequest request) {
        addressService.addAddress(request);
    }

    @GetMapping
    public List<AddressResponse> getAll() {
        return addressService.getAddresses();
    }

    @GetMapping("{id}")
    public AddressResponse getAddress(@PathVariable Integer id) {
        return addressService.getAddress(id);
    }

    @PatchMapping("{id}")
    public void add(@Valid @RequestBody UpdateAddressRequest request, @PathVariable Integer id) {
        request.setId(id);
        addressService.updateAddress(request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        addressService.deleteAddress(id);
    }
}
