package ru.vocalize.test.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import ru.vocalize.test.model.Status;
import ru.vocalize.test.model.TokenEntity;
import ru.vocalize.test.model.User;
import ru.vocalize.test.service.AuthService;

@Controller
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/log-in")
  public ResponseEntity<Status> login(@RequestBody User user, HttpServletResponse response) {
    return ResponseEntity.ok(authService.checkUser(response, user));
  }

  @RequestMapping("/log-out")
  public ResponseEntity<Status> logout(@AuthenticationPrincipal TokenEntity tokenEntity) {
    authService.logout(tokenEntity);
    return ResponseEntity.ok(Status.ok());
  }

  @PostMapping("/registration")
  public ResponseEntity<Status> registration(@RequestBody User user) {
    return ResponseEntity.ok(authService.registration(user));
  }
}
