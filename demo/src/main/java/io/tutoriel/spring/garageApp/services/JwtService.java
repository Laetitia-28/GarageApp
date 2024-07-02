package io.tutoriel.spring.garageApp.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
//This JwtService will do anything related to the Jwt Token that will be received from or within the request
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    // method for extracting username out of the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // the subject is or should be the username/email
    }

    //method for extracting any claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final  Claims claims = extractAllClaims(token);
        return  claimsResolver.apply(claims);
    }

    //method that will help us generate a token without of extra claims. From the user details only
    public String generateToken(UserDetails userDetails){
        return  generateToken(new HashMap<>(), userDetails);
    }

    //method that will help us generate a token out of extra claims and the user details
    public String generateToken(
            Map<String, Object> extraClaims, // this map will contain the claims or extra-claims that we want to add
            UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public String generateRefreshToken(
            UserDetails userDetails
    ) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    public String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) // date at which the claim was created. Useful to check if the token is still valid or not.
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // to set how long the token should be valid. In this case, it will be valid for 24h + 1000ms (60 is in mins and 1000 in ms)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact(); // it's the one that will generate and return the token
    }

    // Method for token validation
    public boolean isTokenValid(String token, UserDetails userDetails) {
        //We use the User Details b/c we want to validate is the token parsed belongs to the user parsed
        final  String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    //Method to check if the token is expired or not
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); // before today's date
    }

    //Method to extract the token's expiration date
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Method for extracting all the claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey()) // used to create the signature part of a jwt in order to ensure that the sender is the real one and the message has not be changed
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
