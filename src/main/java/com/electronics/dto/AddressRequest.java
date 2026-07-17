package com.electronics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(

    @NotBlank(message = "Address name is required") @Size(max = 200) String addressName,

    @NotBlank(message = "Government is required") @Size(max = 100) String government,

    @NotBlank(message = "City is required") @Size(max = 100) String city,

    @NotBlank(message = "Street is required") @Size(max = 150) String street,

    @NotBlank(message = "Building number is required") @Size(max = 20) String buildingNo,

    @Size(max = 1000) String description

) {
}