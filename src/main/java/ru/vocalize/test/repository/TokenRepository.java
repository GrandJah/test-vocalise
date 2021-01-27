package ru.vocalize.test.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import ru.vocalize.test.model.TokenEntity;
import ru.vocalize.test.model.User;

public interface TokenRepository extends CrudRepository<TokenEntity, Long> {

  TokenEntity findByUuid(String uuid);

  @Transactional
  void deleteByUser(User user);
}
