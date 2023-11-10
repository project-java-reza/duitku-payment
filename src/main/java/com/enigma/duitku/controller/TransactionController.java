package com.enigma.duitku.controller;

import com.enigma.duitku.entity.Transaction;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.TransactionRequest;
import com.enigma.duitku.model.response.CommonResponse;
import com.enigma.duitku.model.response.TransactionResponse;
import com.enigma.duitku.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/view")
    public ResponseEntity<?> viewTransaction(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.builder()
                        .data(transactionService.viewTransactionId(id))
                        .build());
    }

    @GetMapping("/viewall")
    public ResponseEntity<?> viewAllTransaction() {

        List<TransactionResponse> transactions = transactionService.viewAllTransaction();

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully get all transaction")
                        .data(transactions)
                        .build());
    }
}
