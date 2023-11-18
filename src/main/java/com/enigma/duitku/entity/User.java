package com.enigma.duitku.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "m_user")
@Builder(toBuilder = true)
public class User {

    @Id
    @NotBlank(message= "Mobile number is required")
    @NotEmpty(message= "Mobile number is required")
    @Size(min = 10, max= 12, message = "Mobile number must be between 10 and 12 characters")
    @Column(name = "mobile_number")
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
    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @OneToOne
    @JoinColumn(name = "user_credential_id")
    private UserCredential userCredential;

    @OneToOne(cascade = CascadeType.ALL)
    private Wallet wallet;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;
}
