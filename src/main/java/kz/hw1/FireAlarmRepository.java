package kz.hw1;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FireAlarmRepository extends JpaRepository<FireAlarm, Long> {

  List<FireAlarm> findByStatusOrderById(AlarmStatus status);

}
