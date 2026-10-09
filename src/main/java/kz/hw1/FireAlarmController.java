package kz.hw1;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
public class FireAlarmController {

  private final FireAlarmRepository repository;

  @Value("${ALARM_FIRE_TEMP:60}")
  private double fireTemp;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public FireAlarm create(@RequestBody CreateAlarmRequest request) {
    var alarm = new FireAlarm();
    alarm.setAddress(request.address());
    return repository.save(alarm);
  }

  @GetMapping
  public List<FireAlarm> list(@RequestParam(required = false) AlarmStatus status) {
    return status == null ? repository.findAll(Sort.by("id")) : repository.findByStatusOrderById(status);
  }

  @GetMapping("/{id}")
  public FireAlarm get(@PathVariable long id) {
    return repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "alarm " + id + " not found"));
  }

  @PutMapping("/{id}")
  public FireAlarm update(@PathVariable long id, @RequestBody UpdateAlarmRequest request) {
    if (request.temperature() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "temperature is required");
    }
    var alarm = get(id);
    alarm.setTemperature(request.temperature());
    alarm.setStatus(detect(request.temperature(), fireTemp));
    alarm.setUpdatedAt(Instant.now());
    return repository.save(alarm);
  }

  static AlarmStatus detect(double temp, double fireTemp) {
    if (temp >= fireTemp) {
      return AlarmStatus.FIRE;
    }
    if (temp >= fireTemp * 0.8) {
      return AlarmStatus.WARNING;
    }
    return AlarmStatus.OK;
  }

}
