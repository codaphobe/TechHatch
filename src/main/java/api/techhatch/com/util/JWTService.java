package api.techhatch.com.util;

import org.springframework.stereotype.Component;

@Component
public class JWTService {

    public String generateToken(){
        return "Hey_this_is_your_token";
    }
}
