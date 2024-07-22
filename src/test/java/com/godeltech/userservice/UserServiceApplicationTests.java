package com.godeltech.userservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.godeltech.grpc.user.*;
import com.godeltech.users.advice.UserNotFoundException;
import com.godeltech.users.mapper.UserMapper;
import com.godeltech.users.model.User;
import com.godeltech.users.repository.UserRepository;
import com.godeltech.users.service.UserService;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import validation.ProtoValidationService;

@ExtendWith(MockitoExtension.class)
class UserServiceApplicationTests {

  @Mock private UserRepository userRepository;

  @Mock private UserMapper userMapper;

  @Mock private ProtoValidationService validationService;

  private UserService userService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    userService = new UserService(userRepository, userMapper, validationService);
  }

  @Test
  public void shouldCreateUser() {
    CreateUserDto request = CreateUserDto.newBuilder().setEmail("test@example.com").build();
    UserDto response = UserDto.newBuilder().setEmail("test@example.com").build();

    when(userMapper.createUserRequestToUser(any())).thenReturn(new User());
    when(userRepository.save(any())).thenReturn(new User());
    when(userMapper.userToUserDto(any())).thenReturn(response);

    StreamObserver<UserDto> responseObserver = mock(StreamObserver.class);
    userService.createUser(request, responseObserver);

    verify(validationService).validate(request);
    verify(userRepository).save(any());
    verify(responseObserver).onNext(response);
    verify(responseObserver).onCompleted();
  }

  @Test
  public void shouldReturnExistingUserById() {

    UserIdDto request = UserIdDto.newBuilder().setId(1L).build();
    UserDto response = UserDto.newBuilder().setId(1L).build();

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().id(1L).build()));
    when(userMapper.userToUserDto(any())).thenReturn(response);

    StreamObserver<UserDto> responseObserver = mock(StreamObserver.class);
    userService.findUserById(request, responseObserver);

    verify(validationService).validate(request);
    verify(userRepository).findById(1L);
    verify(responseObserver).onNext(response);
    verify(responseObserver).onCompleted();
  }

  @Test
  public void shouldFailedWithUserNotFoundException_causeUserWithIdDoesNotExist() {
    UserIdDto request = UserIdDto.newBuilder().setId(1L).build();

    StreamObserver<UserDto> responseObserver = mock(StreamObserver.class);

    UserNotFoundException exception =
        assertThrows(
            UserNotFoundException.class, () -> userService.findUserById(request, responseObserver));

    String expectedMessage = "User with id=1 not found";
    String actualMessage = exception.getMessage();

    assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void shouldReturnExistingUserByEmail() {
    UserEmailDto request = UserEmailDto.newBuilder().setEmail("test@example.com").build();
    UserDto response = UserDto.newBuilder().setEmail("test@example.com").build();

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
    when(userMapper.userToUserDto(any())).thenReturn(response);

    StreamObserver<UserDto> responseObserver = mock(StreamObserver.class);
    userService.findUserByEmail(request, responseObserver);

    verify(validationService).validate(request);
    verify(userRepository).findByEmail("test@example.com");
    verify(responseObserver).onNext(response);
    verify(responseObserver).onCompleted();
  }

  @Test
  public void shouldFailedWithUserNotFoundException_causeUserWithEmailDoesNotExist() {
    UserEmailDto request = UserEmailDto.newBuilder().setEmail("test@example.com").build();

    StreamObserver<UserDto> responseObserver = mock(StreamObserver.class);

    UserNotFoundException exception =
        assertThrows(
            UserNotFoundException.class,
            () -> userService.findUserByEmail(request, responseObserver));

    String expectedMessage = "User with email: test@example.com not found";
    String actualMessage = exception.getMessage();

    assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void shouldUpdateExistingUser() {

    UserDto request = UserDto.newBuilder().setId(1L).setEmail("updated@example.com").build();
    UserDto response = UserDto.newBuilder().setId(1L).setEmail("updated@example.com").build();

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
    when(userRepository.save(any())).thenReturn(new User());
    when(userMapper.userDtoToUser(any())).thenReturn(new User());
    when(userMapper.userToUserDto(any())).thenReturn(response);

    StreamObserver<UserDto> responseObserver = mock(StreamObserver.class);
    userService.updateUser(request, responseObserver);

    verify(validationService).validate(request);
    verify(userRepository).findById(1L);
    verify(userRepository).save(any());
    verify(responseObserver).onNext(response);
    verify(responseObserver).onCompleted();
  }

  @Test
  public void shouldFailedUpdateUser_causeUserWithIdDoesNotExist() {
    UserDto request = UserDto.newBuilder().setId(1L).build();

    StreamObserver<UserDto> responseObserver = mock(StreamObserver.class);

    UserNotFoundException exception =
        assertThrows(
            UserNotFoundException.class, () -> userService.updateUser(request, responseObserver));

    String expectedMessage = "User with id=1 not found";
    String actualMessage = exception.getMessage();

    assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void shouldDeleteUserById() {
    UserIdDto request = UserIdDto.newBuilder().setId(1L).build();

    StreamObserver<Empty> responseObserver = mock(StreamObserver.class);
    userService.deleteUserById(request, responseObserver);

    verify(validationService).validate(request);
    verify(userRepository).deleteById(1L);
    verify(responseObserver).onNext(Empty.getDefaultInstance());
    verify(responseObserver).onCompleted();
  }

  @Test
  public void shouldReturnAllUsers() {
    UserListDto response = UserListDto.newBuilder().addUser(UserDto.newBuilder().build()).build();

    when(userRepository.findAll()).thenReturn(Collections.singletonList(new User()));
    when(userMapper.userListToUserListDto(anyInt(), anyList())).thenReturn(response);

    StreamObserver<UserListDto> responseObserver = mock(StreamObserver.class);
    userService.findAllUser(Empty.getDefaultInstance(), responseObserver);

    verify(userRepository).findAll();
    verify(responseObserver).onNext(response);
    verify(responseObserver).onCompleted();
  }
}
