package ru.vocalize.test.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.vocalize.test.model.TokenEntity;
import ru.vocalize.test.model.User;
import ru.vocalize.test.repository.TokenRepository;

@Service
public class SecurityService {
  private final TokenRepository tokenStorage;

  private final String authHeader;

  private final String key;

  private final long expiration;

  public SecurityService(TokenRepository tokenStorage,
    @Value("${security.cookie.name}") String authHeader, @Value("${security.key}") String key,
    @Value("${security.expiration}") String expiration) {
    this.tokenStorage = tokenStorage;
    this.authHeader = authHeader;
    this.key = key;
    this.expiration = Long.parseLong(expiration);
  }

  private Optional<Authentication> getAuthentication(HttpServletRequest request) {
    Optional<Authentication> authentication = Optional.empty();
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : request.getCookies()) {
        if (cookie.getName()
                  .equals(authHeader)) {
          authentication = Optional.ofNullable(cookie.getValue())
                                   .flatMap(this::extractUUID)
                                   .map(tokenStorage::findByUuid)
                                   .map(this::fromToken);
        }
      }
    }
    return authentication;
  }

  private Authentication fromToken(TokenEntity tokenEntity) {
    return new Auth(tokenEntity.getUser(), tokenEntity);
  }

  public void createToken(User user, HttpServletResponse response) {
    String uuid;
    do {
      uuid = UUID.randomUUID()
                 .toString();
    } while (tokenStorage.findByUuid(uuid) != null);
    tokenStorage.deleteByUser(user);
    tokenStorage.save(TokenEntity.of(uuid, user, true));
    String token = Jwts.builder()
                       .signWith(SignatureAlgorithm.HS256, key)
                       .setId(uuid)
                       .setExpiration(Date.from(Instant.now()
                                                       .plus(Duration.ofSeconds(expiration))))
                       .compact();
    response.addCookie(new Cookie(authHeader, token) {
      {
        setMaxAge((int) expiration);
      }
    });
  }

  public void deleteToken(TokenEntity tokenEntity) {
    tokenStorage.delete(tokenEntity);
  }

  private Optional<String> extractUUID(String token) {
    try {
      return Optional.of(Jwts.parser()
                             .setSigningKey(key)
                             .parseClaimsJws(token)
                             .getBody()
                             .getId());
    } catch (RuntimeException e) {
      return Optional.empty();
    }
  }

  @Bean
  public Filter filter() {
    return (request, response, chain) -> {
      SecurityContextHolder.getContext()
                           .setAuthentication(
                             SecurityService.this.getAuthentication((HttpServletRequest) request)
                                                 .orElse(null));
      chain.doFilter(request, response);
    };
  }

  private static class Auth implements Authentication {

    private final User user;

    private final TokenEntity tokenEntity;

    Auth(User user, TokenEntity tokenEntity) {
      this.user = user;
      this.tokenEntity = tokenEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return Collections.singleton((GrantedAuthority) () -> "USER");
    }

    @Override
    public User getCredentials() {
      return user;
    }

    @Override
    public User getDetails() {
      return user;
    }

    @Override
    public TokenEntity getPrincipal() {
      return tokenEntity;
    }

    @Override
    public boolean isAuthenticated() {
      return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    }

    @Override
    public String getName() {
      return user.getLogin();
    }
  }
}

