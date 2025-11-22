package api.techhatch.com.controller;


import api.techhatch.com.model.UserPrinciple;
import api.techhatch.com.model.Users;
import api.techhatch.com.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class SimpleController {

    @Autowired
    AuthService service;


    @GetMapping("/user")
    public Map<String,Object> user(@AuthenticationPrincipal UserPrinciple principal){
        return Collections.singletonMap("name",principal.getAuthorities());
    }

    @GetMapping("/auth-page")
    public String page(){
        return "This is a authenticated-page";
    }

    @GetMapping("/getAllUsers")
    public List<Users> getAllUsers(){
        return service.getAllUsers();
    }

}
