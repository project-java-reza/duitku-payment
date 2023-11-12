package com.enigma.duitku.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "m_user")
@Builder(toBuilder = true)
public class User {

    @Id
    @Column(name = "mobile_number", length = 12, nullable = false)
    private String mobileNumber;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @OneToOne
    @JoinColumn(name = "user_credential_id")
    private UserCredential userCredential;

    @OneToOne(cascade = CascadeType.ALL)
    private Wallet wallet;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;
}
