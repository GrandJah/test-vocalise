package ru.vocalize.test.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.vocalize.test.model.Status;
import ru.vocalize.test.model.TokenEntity;
import ru.vocalize.test.service.TimeManagerService;

@RestController
public class TimeManagerController {

  private final TimeManagerService timeManager;

  public TimeManagerController(TimeManagerService timeManager) {
    this.timeManager = timeManager;
  }

  @PostMapping("/status")
  public ResponseEntity<Status> echo(@AuthenticationPrincipal TokenEntity tokenEntity) {
    return ResponseEntity.ok(
      Status.message(timeManager.isWork(tokenEntity.getUser()) ? "in" : "out"));
  }

  @PostMapping("/in")
  public ResponseEntity<Status> in(@AuthenticationPrincipal TokenEntity tokenEntity) {
    timeManager.userIn(tokenEntity.getUser());
    return ResponseEntity.ok(Status.message("in"));
  }

  @PostMapping("/out")
  public ResponseEntity<Status> out(@AuthenticationPrincipal TokenEntity tokenEntity) {
    timeManager.userOut(tokenEntity.getUser());
    return ResponseEntity.ok(Status.message("out"));
  }

  @PostMapping("/report")
  public ResponseEntity<Status> get(@AuthenticationPrincipal TokenEntity tokenEntity) {
    return ResponseEntity.ok(Status.message(timeManager.createReport(tokenEntity.getUser())));
  }
}
