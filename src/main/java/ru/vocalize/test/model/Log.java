package ru.vocalize.test.model;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "log")
@Data
public class Log {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Boolean working;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private User user;

  @Column(nullable = false)
  private Instant timePoint = Instant.now();

  static public Log in(User user) {
    Log log = new Log();
    log.user = user;
    log.working = true;
    return log;
  }

  static public Log out(User user) {
    Log log = new Log();
    log.user = user;
    log.working = false;
    return log;
  }
}

