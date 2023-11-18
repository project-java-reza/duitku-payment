package com.enigma.duitku.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AuthRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private LocalDate dateOfBirth;
}
