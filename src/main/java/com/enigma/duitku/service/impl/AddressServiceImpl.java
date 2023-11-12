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
import org.springframework.stereotype.Service;

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

            if (user.getAddress() == null) {
                user.setAddress(new Address());
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
}
