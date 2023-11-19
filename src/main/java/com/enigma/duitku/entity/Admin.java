package com.enigma.duitku.entity;

import com.enigma.duitku.entity.constant.EWalletType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "m_admin")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class Admin {

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
    private String dateOfBirth;

    @CreatedDate
    @Column(name = "created_at")
    protected LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private EWalletType walletType;

    @OneToOne
    @JoinColumn(name = "user_credential_id")
    private UserCredential userCredential;

    @OneToOne(cascade = CascadeType.ALL)
    private Wallet wallet;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;
}
