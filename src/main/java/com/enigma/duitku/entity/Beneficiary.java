package com.enigma.duitku.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "m_beneficiary")
@Builder(toBuilder = true)
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer beneficiaryId;

    @Column(name= "mobile_number", length = 12, nullable = false)
    private String mobileNumber;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(name="account_no", length = 15)
    private String accountNo;

    @Column(name= "bank_name", length = 15, nullable = false)
    private String bankName;

}
