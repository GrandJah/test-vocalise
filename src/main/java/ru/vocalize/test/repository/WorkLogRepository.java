package ru.vocalize.test.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import ru.vocalize.test.model.Log;
import ru.vocalize.test.model.User;

public interface WorkLogRepository extends CrudRepository<Log, Long> {
  List<Log> findByUserOrderByTimePoint(User user);

  @Transactional
  void deleteAllByUser(User user);

  Log findFirstByUserOrderByTimePointDesc(User user);
}
