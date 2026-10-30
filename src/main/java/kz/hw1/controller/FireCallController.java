package kz.hw1.controller;

import java.util.List;
import kz.hw1.dto.CreateCallRequest;
import kz.hw1.dto.UpdateCallRequest;
import kz.hw1.entity.CallStatus;
import kz.hw1.entity.FireCall;
import kz.hw1.service.FireCallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calls")
@RequiredArgsConstructor
public class FireCallController {

  private final FireCallService service;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public FireCall create(@RequestBody CreateCallRequest request) {
    return service.create(request);
  }

  @GetMapping
  public List<FireCall> list(@RequestParam(required = false) CallStatus status) {
    return service.list(status);
  }

  @GetMapping("/{id}")
  public FireCall get(@PathVariable long id) {
    return service.get(id);
  }

  @PutMapping("/{id}")
  public FireCall update(@PathVariable long id, @RequestBody UpdateCallRequest request) {
    return service.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    service.delete(id);
  }

}
