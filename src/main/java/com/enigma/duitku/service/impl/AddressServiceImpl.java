package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.Address;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.AddressRequest;
import com.enigma.duitku.model.response.AddressResponse;
import com.enigma.duitku.repository.AddressRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.security.JwtUtils;
import com.enigma.duitku.service.AddressService;
import com.enigma.duitku.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final JwtUtils jwtUtils;

    private final UserService userService;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public AddressResponse addAddress(AddressRequest addressRequest, String token) throws UserException {
        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        if(user != null) {

            if (user.getAddress() != null) {
                throw new UserException("User already has an address");
            }

            Address address = new Address();
            address.setState(addressRequest.getState());
            address.setCity(addressRequest.getCity());
            address.setStreetName(addressRequest.getStreetName());
            address.setPostalCode(addressRequest.getPostalCode());
            user.setAddress(address);

            addressRepository.save(address);
            userRepository.save(user);

            return AddressResponse.builder()
                    .state(user.getAddress().getState())
                    .city(user.getAddress().getCity())
                    .streetName(user.getAddress().getStreetName())
                    .postalCode(user.getAddress().getPostalCode())
                    .build();
        } else {
            throw new UserException("Please Login in!");
        }
    }

    @Override
    public AddressResponse updateAddress(AddressRequest addressRequest, String token) throws UserException {
        String loggedInUserId = jwtUtils.extractUserId(token);
        User user = userService.getById(loggedInUserId);

        if(user != null) {

           Address currentAddress = addressRepository.findById(addressRequest.getId()).orElse(null);

            currentAddress.setState(addressRequest.getState());
            currentAddress.setCity(addressRequest.getCity());
            currentAddress.setStreetName(addressRequest.getStreetName());
            currentAddress.setPostalCode(addressRequest.getPostalCode());

            addressRepository.saveAndFlush(currentAddress);

            return AddressResponse.builder()
                    .postalCode(addressRequest.getPostalCode())
                    .streetName(addressRequest.getStreetName())
                    .state(addressRequest.getState())
                    .build();
        } else {
            throw new UserException("Please Login in!");
        }
    }

    @Override
    public Address getAddressId(String id) {
            return addressRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "address id not found"));
    }

    @Override
    public void removeAddress(String id, String token) throws UserException{
        String loggedInUserId = jwtUtils.extractUserId(token);
        User userValidate = userService.getById(loggedInUserId);

        if(userValidate != null) {
            Address address = addressRepository.findById(id).orElse(null);

            if(address != null) {
                userValidate.setAddress(null);

                addressRepository.delete(address);
            }
        } else {
            throw new UserException("Please Login in!");
        }
    }

    // Admin
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<AddressResponse> viewAllAddress(Integer page, Integer size) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Address> addresses = addressRepository.findAll(pageable);
            List<AddressResponse> addressResponses = new ArrayList<>();
            for (Address address : addresses.getContent()) {
                AddressResponse addressResponse = AddressResponse.builder()
                        .city(address.getCity())
                        .postalCode(address.getPostalCode())
                        .state(address.getState())
                        .streetName(address.getStreetName())
                        .build();
                addressResponses.add(addressResponse);
            }
            return new PageImpl<>(addressResponses, pageable, addresses.getTotalElements());
    }

    @Override
    public Address viewAddress() {
        return null;
    }
}
