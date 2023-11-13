package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.Address;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.AddressRequest;
import com.enigma.duitku.model.response.AddressResponse;
import com.enigma.duitku.repository.AddressRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.security.JwtUtils;
import com.enigma.duitku.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class AddressServiceImplTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserService userService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void addAddress_shouldSucceed() throws UserException {

        String userId = "08964363436";
        String token = "validToken";
        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setState("tunjungan");
        addressRequest.setCity("Semarang");
        addressRequest.setStreetName("Jalan kita bersama");
        addressRequest.setPostalCode("59483");

        User user = new User();
        user.setMobileNumber(userId);

        Address address = new Address();
        address.setState(addressRequest.getState());
        address.setCity(addressRequest.getCity());
        address.setStreetName(addressRequest.getStreetName());
        address.setPostalCode(addressRequest.getPostalCode());


        when(jwtUtils.extractUserId(token)).thenReturn(userId);
        when(userService.getById(userId)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        AddressResponse addressResponse = addressService.addAddress(addressRequest, token);

        assertNotNull(addressResponse);
        assertEquals(addressRequest.getState(), addressResponse.getState());
        assertEquals(addressRequest.getCity(), addressResponse.getCity());
        assertEquals(addressRequest.getStreetName(), addressResponse.getStreetName());
        assertEquals(addressRequest.getPostalCode(), addressResponse.getPostalCode());


        verify(jwtUtils, times(1)).extractUserId(token);
        verify(userService, times(1)).getById(userId);
        verify(userRepository, times(1)).save(user);
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void updateAddress() {
    }

    @Test
    void getAddressId() {
    }

    @Test
    void removeAddress() {
    }

    @Test
    void viewAllAddress() {
    }

    @Test
    void viewAddress() {
    }
}