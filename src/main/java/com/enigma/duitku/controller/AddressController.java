package com.enigma.duitku.controller;

import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.AddressRequest;
import com.enigma.duitku.model.response.AddressResponse;
import com.enigma.duitku.model.response.BankAccountResponse;
import com.enigma.duitku.model.response.CommonResponse;
import com.enigma.duitku.security.AuthTokenFilter;
import com.enigma.duitku.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/address")
public class AddressController {

    private final AddressService addressService;

    private final AuthTokenFilter authTokenFilter;

    @PostMapping("/add")
    public ResponseEntity<?> addAddress(@RequestBody AddressRequest addressRequest, HttpServletRequest httpServletRequest) throws UserException {
        try {
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);
            AddressResponse addressResponse = addressService.addAddress(addressRequest, jwtToken);

            if(addressResponse.getErrors()!= null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .data(addressResponse)
                                .message("Failed add address")
                                .build());
            } else {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(CommonResponse.builder()
                                .statusCode(HttpStatus.CREATED.value())
                                .data(addressResponse)
                                .message("Successfully created address")
                                .build());
            }

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Failed add address" + e.getMessage())
                            .build());
        }
    }

}
