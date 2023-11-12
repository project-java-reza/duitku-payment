package com.enigma.duitku.controller;

import com.enigma.duitku.entity.Address;
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
import org.springframework.web.bind.annotation.*;

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

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CommonResponse.builder()
                            .statusCode(HttpStatus.CREATED.value())
                            .data(addressResponse)
                            .message("Successfully created address")
                            .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Failed add address" + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateAddress(@RequestBody AddressRequest addressRequest, HttpServletRequest httpServletRequest) throws UserException {
        try {
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);
            AddressResponse addressResponse = addressService.updateAddress(addressRequest, jwtToken);

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(CommonResponse.builder()
                            .statusCode(HttpStatus.ACCEPTED.value())
                            .data(addressResponse)
                            .message("Successfully update address")
                            .build());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.<BankAccountResponse>builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Failed update address" + e.getMessage())
                            .build());
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getAddressId(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<Address>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully get customer by id")
                        .data(addressService.getAddressId(id))
                        .build());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAddress(@RequestParam String id, HttpServletRequest httpServletRequest ) {
        try{
            String jwtToken = authTokenFilter.parseJwt(httpServletRequest);
            addressService.removeAddress(id, jwtToken);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(CommonResponse.builder()
                            .statusCode(HttpStatus.OK.value())
                            .message(String.valueOf("Sucessfully delete address"))
                            .build());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponse.builder()
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .message("Delete Failed " + e.getMessage())
                            .build());
        }
    }

}
