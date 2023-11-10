package com.enigma.duitku.controller;

import com.enigma.duitku.model.request.BeneficiaryRequest;
import com.enigma.duitku.model.response.BeneficiaryResponse;
import com.enigma.duitku.model.response.CommonResponse;
import com.enigma.duitku.security.AuthTokenFilter;
import com.enigma.duitku.service.BeneficiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/beneficiary")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    private final AuthTokenFilter authTokenFilter;

    @PostMapping("/add")
    public ResponseEntity<?> addBeneficiary(@RequestBody BeneficiaryRequest request, HttpServletRequest httpServletRequest) {

        String jwtToken = authTokenFilter.parseJwt(httpServletRequest);

        if(jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CommonResponse.builder()
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .build());
        }

        BeneficiaryResponse beneficiaryResponse = beneficiaryService.addBeneficiary(request, jwtToken);

        if(beneficiaryResponse.getErrors() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse.builder()
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .data(beneficiaryResponse)
                            .message("Cannot add a new account because it is already registered")
                            .build());
        } else {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CommonResponse.builder()
                            .statusCode(HttpStatus.CREATED.value())
                            .data(beneficiaryResponse)
                            .message("Successfully create benefiary")
                            .build());
        }
    }

    @GetMapping("/viewall")
    public ResponseEntity<List<BeneficiaryResponse>> getAllBeneficiaryResponse() {
        List<BeneficiaryResponse> allBeneficiaryResponse = beneficiaryService.viewAllBeneficiaries();
        return new ResponseEntity<>(allBeneficiaryResponse, HttpStatus.OK);
    }

}
