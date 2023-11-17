package com.enigma.duitku.util;

import com.enigma.duitku.entity.UserDetailImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AccountUtil {

    public UserDetailImpl blockAccount(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailImpl) authentication.getPrincipal();
    }

}
