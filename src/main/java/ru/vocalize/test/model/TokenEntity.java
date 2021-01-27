package ru.vocalize.test.model;

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
@Table(name = "token")
@Data
public class TokenEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(unique = true, nullable = false)
  private Long id;

  @Column(unique = true, nullable = false, length = 36)
  private String uuid;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private User user;

  @Column(nullable = false)
  private Boolean authenticated;

  public static TokenEntity of(String uuid, User user, boolean authenticated) {
    TokenEntity tokenEntity = new TokenEntity();
    tokenEntity.uuid = uuid;
    tokenEntity.user = user;
    tokenEntity.authenticated = authenticated;
    return tokenEntity;
  }
}
