package api.techhatch.com.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private Long EXPIRATION_TIME;

    private String secret="";

    public JwtUtil(){
        try{
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey key = keyGen.generateKey();
            secret = Base64.getEncoder().encodeToString(key.getEncoded());
        }catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }

    public String generateToken(String email, String role, Long userId){
        System.out.println("Generating token");
        return Jwts.builder()
                .subject(email)
                .claim("role",role)
                .claim("userId",userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }

    SecretKey getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token, UserDetails email){
        try{
            Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (JwtException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getEmailFromToken(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public String getRoleFromToken(String token){
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }
}
