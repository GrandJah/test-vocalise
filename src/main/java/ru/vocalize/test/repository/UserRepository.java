package ru.vocalize.test.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import ru.vocalize.test.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
  Optional<User> findByLogin(String login);
}
