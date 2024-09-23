package com.example_login_2.mapper;

import com.example_login_2.controller.AuthResponse.MUserResponse;
import com.example_login_2.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    MUserResponse toUserResponse(User user);
}
