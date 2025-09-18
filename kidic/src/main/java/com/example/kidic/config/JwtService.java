package com.example.kidic.config;

import com.example.kidic.entity.Parent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "4f714a748e4ba9ead2c9af8447bcb33f7ed0cb1043ca08ac0254a4fa313d51bb";
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build().parseClaimsJws(token)
                .getBody();
    }

    public UUID extractFamilyId(String token) {
        Claims claims = extractClaims(token);
        Object familyIdObj = claims.get("family_id");
        if (familyIdObj == null) {
            return null; // parent didn't join a family yet
        }
        return UUID.fromString(familyIdObj.toString());
    }


    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String generateToken(UserDetails userDetails) {
        Parent parent = (Parent) userDetails;  // since your Parent implements UserDetails

        Map<String, Object> extraClaims = new HashMap<>();

        if (parent.getFamily() != null) {
            extraClaims.put("family_id", parent.getFamily().getId());
        }

        return generateToken(extraClaims, userDetails);
    }

    public String generateToken(
            Map<String,Object> extraClaims,
            UserDetails userDetails) {
        return Jwts
                .builder()
                .addClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = this.extractUsername(token);
        return !isTokenExpired(token) && username.equals(userDetails.getUsername());
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }

    private Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
