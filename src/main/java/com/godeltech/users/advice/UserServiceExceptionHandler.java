package com.godeltech.users.advice;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;

import io.grpc.Status;
import org.lognet.springboot.grpc.recovery.GRpcExceptionHandler;
import org.lognet.springboot.grpc.recovery.GRpcExceptionScope;
import org.lognet.springboot.grpc.recovery.GRpcServiceAdvice;
import validation.ProtoValidationException;

@GRpcServiceAdvice
public class UserServiceExceptionHandler {
  @GRpcExceptionHandler
  public Status handleProtoValidationException(
      final ProtoValidationException exception, final GRpcExceptionScope gRpcExceptionScope) {
    return INVALID_ARGUMENT.withDescription(exception.getMessage());
  }

  @GRpcExceptionHandler
  public Status handleUserNotFoundException(
      final UserNotFoundException exception, final GRpcExceptionScope gRpcExceptionScope) {
    return NOT_FOUND.withDescription(exception.getMessage());
  }
}
