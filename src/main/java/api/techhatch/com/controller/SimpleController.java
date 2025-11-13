package api.techhatch.com.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class SimpleController {

    @GetMapping("/user")
    public Map<String,Object> user(@AuthenticationPrincipal OAuth2User principle){
        return Collections.singletonMap("name",principle.getAttribute("name"));
    }

}
