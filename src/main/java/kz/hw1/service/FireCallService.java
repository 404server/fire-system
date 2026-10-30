package kz.hw1.service;

import java.util.List;
import kz.hw1.dto.CreateCallRequest;
import kz.hw1.dto.UpdateCallRequest;
import kz.hw1.entity.CallStatus;
import kz.hw1.entity.FireCall;
import kz.hw1.repository.FireCallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FireCallService {

  private final FireCallRepository repository;

  public FireCall create(CreateCallRequest request) {
    var call = new FireCall();
    call.setAddress(require(request.address(), "address"));
    call.setCallerPhone(request.callerPhone());
    call.setDescription(request.description());
    return repository.save(call);
  }

  public List<FireCall> list(CallStatus status) {
    return status == null ? repository.findAll(Sort.by("id")) : repository.findByStatusOrderById(status);
  }

  public FireCall get(long id) {
    return repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "call " + id + " not found"));
  }

  public FireCall update(long id, UpdateCallRequest request) {
    var call = get(id);
    call.setAddress(require(request.address(), "address"));
    call.setCallerPhone(request.callerPhone());
    call.setDescription(request.description());
    call.setStatus(request.status() == null ? call.getStatus() : request.status());
    call.setBrigade(request.brigade());
    return repository.save(call);
  }

  public void delete(long id) {
    repository.delete(get(id));
  }

  private static String require(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " is required");
    }
    return value;
  }

}
