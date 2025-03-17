package com.bulbas23r.client.user.application.service;

import common.dto.user.UserDetailsDto;
import com.bulbas23r.client.user.application.dto.UserSignUpRequestDto;

public interface UserService {

  UserDetailsDto getUserDetails(String username);

  void signUp(UserSignUpRequestDto userRequestDto);

  UserResponseDto getUserDetail(String username);

  UserResponseDto getUserDetailForMaster(Long userId);
}
