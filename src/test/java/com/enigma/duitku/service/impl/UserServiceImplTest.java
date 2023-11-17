package com.enigma.duitku.service.impl;

import com.enigma.duitku.entity.Role;
import com.enigma.duitku.entity.User;
import com.enigma.duitku.entity.UserCredential;
import com.enigma.duitku.entity.Wallet;
import com.enigma.duitku.entity.constant.ERole;
import com.enigma.duitku.exception.UserException;
import com.enigma.duitku.model.request.AuthRequest;
import com.enigma.duitku.model.response.RegisterResponse;
import com.enigma.duitku.repository.UserCredentialRepository;
import com.enigma.duitku.repository.UserRepository;
import com.enigma.duitku.security.BCryptUtils;
import com.enigma.duitku.service.RoleService;
import com.enigma.duitku.service.UserService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class UserServiceImplTest {

    @Value("${DB_USER}")
    private String DBUSER;

    @Value("${DB_PASSWORD}")
    private String DBPASSWORD;

    @Value("${DB_LOCAL_HOST}")
    private String DBLOCALHOST;

    @Value("${DB_PORT}")
    private Integer DBPORT;

    @Value("${DB_NAME}")
    private String DBNAME;

    @Value("${APP_PORT}")
    private Integer APPPORT;

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserCredentialRepository userCredentialRepository=mock(UserCredentialRepository.class);
    private final UserService userService = mock(UserService.class);
    private final RoleService roleService = mock(RoleService.class);
    private final AuthServiceImpl authServiceImpl = mock(AuthServiceImpl.class);
    private final BCryptUtils bCryptUtils = mock(BCryptUtils.class);

    @Test
    void itShouldReturnUserWhenCreateNewUser() throws UserException {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://" + DBLOCALHOST + ":" + DBPORT + "/" + DBNAME);
        config.setUsername(DBUSER);
        config.setPassword(DBPASSWORD);
        DataSource dataSource = new HikariDataSource(config);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("reza@gmail.com");
        authRequest.setFirstName("rizqi");
        authRequest.setLastName("reza ardiansyah");
        authRequest.setPassword("Password@123");
        authRequest.setMobileNumber("0851568119779");

        Role role = Role.builder()
                .role(ERole.ROLE_USER)
                .build();

        UserCredential credential = UserCredential.builder()
                .mobileNumber(authRequest.getMobileNumber())
                .password(bCryptUtils.hashPassword(authRequest.getPassword()))
                .roles(List.of(role))
                .build();

        User user = User.builder()
                .firstName(authRequest.getFirstName())
                .lastName(authRequest.getLastName())
                .mobileNumber(authRequest.getMobileNumber())
                .email(authRequest.getEmail())
                .userCredential(credential)
                .build();


        Wallet wallet = new Wallet();
        wallet.setBalance(0.0);

        when(roleService.getOrSave(ERole.ROLE_USER)).thenReturn(role);
        when(userCredentialRepository.saveAndFlush(credential)).thenReturn(credential);
        when(userService.create(user)).thenReturn(user);

        RegisterResponse registerResponse = authServiceImpl.registerUsers(authRequest);

        assertEquals(authRequest.getEmail(), registerResponse.getEmail());
        assertEquals(authRequest.getFirstName(), registerResponse.getFirstName());
        assertEquals(authRequest.getLastName(), registerResponse.getLastName());
        assertEquals(authRequest.getMobileNumber(), registerResponse.getMobileNumber());
        assertEquals(wallet.getBalance(), registerResponse.getBalance());

        verify(roleService, times(1)).getOrSave(ERole.ROLE_USER);
        verify(bCryptUtils, times(1)).hashPassword("password");
        verify(userCredentialRepository, times(1)).saveAndFlush(any(UserCredential.class));
        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    void getById() {
    }

    @Test
    void update() {
    }

    @Test
    void deleteById() {
    }
}