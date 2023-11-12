package com.enigma.duitku.service;

import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.AddressRequest;
import com.enigma.duitku.model.response.AddressResponse;

public interface AddressService {

    AddressResponse addAddress(AddressRequest addressRequest, String token) throws UserException;

}
