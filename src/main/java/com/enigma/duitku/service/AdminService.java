package com.enigma.duitku.service;

import com.enigma.duitku.entity.Admin;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.response.UserResponse;
import org.springframework.data.domain.Page;

public interface AdminService {

    Admin create (Admin admin) throws UserException;
    Page<UserResponse> getAll(Integer page, Integer size);
}
