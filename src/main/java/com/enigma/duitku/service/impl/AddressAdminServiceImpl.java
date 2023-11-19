package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.Address;
import com.enigma.duitku.entity.Admin;
import com.enigma.duitku.exception.ConflictException;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.AddressRequest;
import com.enigma.duitku.model.response.AddressResponse;
import com.enigma.duitku.repository.AddressRepository;
import com.enigma.duitku.repository.AdminRepository;
import com.enigma.duitku.security.JwtUtils;
import com.enigma.duitku.service.AddressAdminService;
import com.enigma.duitku.service.AdminService;
import com.enigma.duitku.service.UserService;
import com.enigma.duitku.util.AccountUtil;
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
public class AddressAdminServiceImpl implements AddressAdminService {

    private final JwtUtils jwtUtils;
    private final AdminService adminService;
    private final AdminRepository adminRepository;
    private final AddressRepository addressRepository;
    private final AccountUtil accountUtil;

    @Override
    public AddressResponse addAddressAdmin(AddressRequest addressRequest, String token) throws UserException {

        String loggedInUserId = jwtUtils.extractUserId(token);

        Admin admin = adminService.getByIdAdmin(loggedInUserId);

        if(admin != null) {
            if (admin.getAddress() != null) {
                throw new ConflictException("Admin already has an address");
            }

            Address address = new Address();
            address.setState(addressRequest.getState());
            address.setCity(addressRequest.getCity());
            address.setStreetName(addressRequest.getStreetName());
            address.setPostalCode(addressRequest.getPostalCode());

            admin.setAddress(address);

            addressRepository.save(address);

            adminRepository.save(admin);

            return AddressResponse.builder()
                    .state(admin.getAddress().getState())
                    .city(admin.getAddress().getCity())
                    .streetName(admin.getAddress().getStreetName())
                    .postalCode(admin.getAddress().getPostalCode())
                    .build();
        } else {
            throw new UserException("Please Login in!");
        }
    }

    @Override
    public Address updateAddress(Address address, String token) throws UserException {
        String loggedInUserId = jwtUtils.extractUserId(token);
        Admin admin= adminService.getByIdAdmin(loggedInUserId);

        if(admin!= null) {
            Address currentAddress = getAddressId(address.getId());
            return addressRepository.save(address);
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
        // TODO 1 : Extract the user ID from the JWT token using jwtUtils.
        String loggedInUserId = jwtUtils.extractUserId(token);

        // TODO 2 : Retrieve the user object from the userService based on the extracted user ID.
        Admin admin= adminService.getByIdAdmin(loggedInUserId);

        if(admin != null) {
            // TODO 3 : Retrieving and Deleting Addresses
            Address address = addressRepository.findById(id).orElse(null);

            if(address != null) {
                // TODO 4 : Removes the address from the user object
                admin.setAddress(null);

                // TODO : Removes the address entity from the repository
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

}
