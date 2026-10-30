package kz.hw1.dto;

import kz.hw1.entity.CallStatus;

public record UpdateCallRequest(
    String address, String callerPhone, String description, CallStatus status, String brigade) {}
