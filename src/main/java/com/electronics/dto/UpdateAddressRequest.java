package com.electronics.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateAddressRequest {

    Integer id;

    @Size(max = 200)
    String addressName;

    @Size(max = 100)
    String government;

    @Size(max = 100)
    String city;

    @Size(max = 150)
    String street;

    @Size(max = 20)
    String buildingNo;

    @Size(max = 1000)
    String description;
}