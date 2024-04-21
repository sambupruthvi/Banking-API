package com.codebank.accounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customer")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Customer extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private long customerId;

    @Column(name = "customer_name")
    private String customerName;

    private String email;

    private String phoneNumber;

}
