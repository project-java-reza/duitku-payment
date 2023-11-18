package com.enigma.duitku.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "m_admin")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class Admin {

    @Id
    @GenericGenerator(strategy = "uuid2", name= "system-uuid")
    @GeneratedValue(generator = "system-uuid")
    private String id;

    @NotBlank(message = "Mobile number is required")
    @Size(min = 10, max = 12, message = "Invalid mobile number")
    @Column(name = "mobile_number", unique = true)
    private String mobileNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Column(name = "last_name")
    private String lastName;

    @NotBlank(message = "Date of birth is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String dateOfBirth;

    @OneToOne
    @JoinColumn(name = "user_credential_id")
    private UserCredential userCredential;

    @OneToOne(cascade = CascadeType.ALL)
    private Wallet wallet;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

}
