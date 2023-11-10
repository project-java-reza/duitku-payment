package com.enigma.duitku.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserResponse {
    private String email;
    private String mobileNumber;
    private String address;
    private String name;
}
