package kz.hw1.repository;

import java.util.List;
import kz.hw1.entity.CallStatus;
import kz.hw1.entity.FireCall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FireCallRepository extends JpaRepository<FireCall, Long> {

  List<FireCall> findByStatusOrderById(CallStatus status);

}
