package com.enigma.duitku.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

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

    @NotBlank(message = "{Admin.name.invalid}")
    @NotEmpty(message = "{Admin.name.invalid}")
    @NotNull(message = "{Admin.name.invalid]")
    @Column(length = 50)
    private String firstname;

    @NotBlank(message = "{Admin.name.invalid}")
    @NotEmpty(message = "{Admin.name.invalid}")
    @NotNull(message = "{Admin.name.invalid]")
    @Column(length = 50)
    private String lastName;

    @NotBlank(message = "{Admin.email.notBlankAndNotEmpty}")
    @NotEmpty(message = "{Admin.email.notBlankAndNotEmpty}")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", flags = Pattern.Flag.CASE_INSENSITIVE, message = "{User.email.notBlankAndNotEmpty}")
    @Column(length = 100)
    private String email;

    @NotBlank(message= "{Admin.mobileNumber.notBlankAndNotEmpty}")
    @NotEmpty(message= "{Admin.mobileNumber.notBlankAndNotEmpty}")
    @Size(min = 10, max= 12, message = "{Admin.mobileNumber.invalid}")
    @Column(name = "mobile_number")
    private String mobileNumber;

    @NotBlank(message = "{Admin.dateOfBirth.invalid}")
    @NotEmpty(message = "{Admin.dateOfBirth.invalid}")
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
