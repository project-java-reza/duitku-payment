package com.enigma.duitku.entity.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum EWalletType {

    PREMIUM("Premium"),
    BASIC("Basic");

    private String name;
}
