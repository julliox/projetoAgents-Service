package br.com.octopus.projectA.security;

import br.com.octopus.projectA.entity.UserEntity;
import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.UUID;

@Log4j2
@Component
public class JwtProvider {

    @Value("${sga.auth.jwtSecret}")
    private String jwtSecret;
    @Value("${sga.auth.jwtExpirationMs}")
    private Long jwtExpirationMs;

    public String generateJwt(Authentication authentication, UserEntity userEntity) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setId(userPrincipal.getId().toString())
                .setSubject(userPrincipal.getUsername()) // Email de usuário
                .setAudience(userPrincipal.getEmail()) // Email como audiência
                .setIssuedAt(new Date()) // Data de emissão
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Data de expiração
                .signWith(SignatureAlgorithm.HS512, jwtSecret) // Algoritmo de assinatura e chave secreta
                .claim("profile", userEntity.getProfile())
                .claim("name", userEntity.getName())// Adicionando o perfil do usuário
                .compact();
    }

    public String generateJwtFile(UUID sistemaId,String filename, String identificador) {
        return Jwts.builder()
                .setAudience(sistemaId.toString())
                .setSubject((filename))
                .setId(identificador)
                .setIssuedAt(new Date())
            //    .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameJwt(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public String getAudienceJwt(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getAudience();
    }

    public String getIdJwt(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getId();
    }

    public boolean validateJwt(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }



}
