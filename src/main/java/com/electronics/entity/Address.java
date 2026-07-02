package com.electronics.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_id_gen")
    @SequenceGenerator(name = "address_id_gen", sequenceName = "address_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Size(max = 200)
    @Column(name = "address_name", length = 200, nullable = false)
    private String addressName;

    @Size(max = 100)
    @Column(name = "government", length = 100)
    private String government;

    @Size(max = 100)
    @Column(name = "city", length = 100)
    private String city;

    @Size(max = 150)
    @Column(name = "street", length = 150)
    private String street;

    @Size(max = 20)
    @Column(name = "building_no", length = 20)
    private String buildingNo;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;
}