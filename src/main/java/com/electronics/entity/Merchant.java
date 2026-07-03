package com.electronics.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "merchant")
public class Merchant extends User {

    @Column(name = "about", length = Integer.MAX_VALUE)
    private String about;

    @Column(name = "business_name")
    private String businessName;
}