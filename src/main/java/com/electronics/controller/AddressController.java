package com.electronics.controller;

import com.electronics.dto.AddressRequest;
import com.electronics.dto.AddressResponse;
import com.electronics.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public void add(@RequestBody AddressRequest request) {
        addressService.addAddress(request);
    }

    @GetMapping
    public List<AddressResponse> getAll() {
        return addressService.getAddresses();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        addressService.deleteAddress(id);
    }
}
