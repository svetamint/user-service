package com.godeltech.users.service;

import com.godeltech.grpc.user.*;
import com.godeltech.grpc.user.UserServiceGrpc.UserServiceImplBase;
import com.godeltech.users.advice.UserNotFoundException;
import com.godeltech.users.mapper.UserMapper;
import com.godeltech.users.repository.UserRepository;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;
import validation.ProtoValidationService;

@GRpcService
@RequiredArgsConstructor
public class UserService extends UserServiceImplBase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProtoValidationService validationService;

    @Override
    public void createUser(CreateUserDto request, StreamObserver<UserDto> responseObserver) {
        validationService.validate(request);
        var newUser = userRepository.save(userMapper.createUserRequestToUser(request));
        responseObserver.onNext(userMapper.userToUserDto(newUser));
        responseObserver.onCompleted();
    }

    @Override
    public void findUserById(UserIdDto request, StreamObserver<UserDto> responseObserver) {
        validationService.validate(request);
        var userId = request.getId();
        var user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        responseObserver.onNext(userMapper.userToUserDto(user));
        responseObserver.onCompleted();
    }

    @Override
    public void findUserByEmail(final UserEmailDto request, StreamObserver<UserDto> responseObserver) {
        validationService.validate(request);
        var email = request.getEmail();
        var user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        responseObserver.onNext(userMapper.userToUserDto(user));
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(UserDto request, StreamObserver<UserDto> responseObserver) {
        validationService.validate(request);
        var userId = request.getId();
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        var updatedUser = userRepository.save(userMapper.userDtoToUser(request));
        responseObserver.onNext(userMapper.userToUserDto(updatedUser));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUserById(
            final UserIdDto request, final StreamObserver<Empty> responseObserver) {
        validationService.validate(request);
        userRepository.deleteById(request.getId());
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void findAllUser(final Empty request, final StreamObserver<UserListDto> responseObserver) {
        var userList = userRepository.findAll();
        responseObserver.onNext(userMapper.userListToUserListDto(userList.size(), userList));
        responseObserver.onCompleted();
    }
}
