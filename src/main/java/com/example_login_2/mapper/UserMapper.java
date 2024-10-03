package com.example_login_2.mapper;

import com.example_login_2.controller.AuthResponse.MUserResponse;
import com.example_login_2.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    MUserResponse toUserResponse(User user);

    List<MUserResponse> toUserResponseList(List<User> users);
}
