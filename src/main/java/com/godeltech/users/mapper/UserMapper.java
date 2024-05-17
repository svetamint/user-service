package com.godeltech.users.mapper;

import com.godeltech.grpc.user.CreateUserDto;
import com.godeltech.grpc.user.UserDto;
import com.godeltech.grpc.user.UserListDto;
import com.godeltech.users.model.User;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

  User createUserRequestToUser(CreateUserDto createUserDto);

  UserDto userToUserDto(User user);

  UserListDto userListToUserListDto(int count, List<User> userList);

  User userDtoToUser(UserDto request);
}
