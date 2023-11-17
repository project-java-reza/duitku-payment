package com.enigma.duitku.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "m_user")
@Builder(toBuilder = true)
public class User {

    @Id
    @NotBlank(message= "{User.mobileNumber.notBlankAndNotEmpty}")
    @NotEmpty(message= "{User.mobileNumber.notBlankAndNotEmpty}")
    @Size(min = 10, max= 12, message = "{User.mobileNumber.invalid}")
    @Column(name = "mobile_number")
    private String mobileNumber;

    @NotBlank(message = "{User.name.invalid}")
    @NotEmpty(message = "{User.name.invalid}")
    @NotNull(message = "{User.name.invalid]")
    @Column(length = 50)
    private String firstName;

    @NotBlank(message = "{User.name.invalid}")
    @NotEmpty(message = "{User.name.invalid}")
    @NotNull(message = "{User.name.invalid]")
    @Column(length = 50)
    private String lastName;

    @NotBlank(message = "{User.email.notBlankAndNotEmpty}")
    @NotEmpty(message = "{User.email.notBlankAndNotEmpty}")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", flags = Pattern.Flag.CASE_INSENSITIVE, message = "{User.email.notBlankAndNotEmpty}")
    @Column(length = 100)
    private String email;

    @NotBlank(message = "{User.dateOfBirth.invalid}")
    @NotEmpty(message = "{User.dateOfBirth.invalid}")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @OneToOne
    @JoinColumn(name = "user_credential_id")
    private UserCredential userCredential;

    @OneToOne(cascade = CascadeType.ALL)
    private Wallet wallet;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;
}
