package com.bulbas23r.client.user.application.service;

import com.bulbas23r.client.user.domain.model.User;
import common.model.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
  private String username;
  private String name;
  private String slackId;
  private UserRoleEnum role;

  public static UserResponseDto fromEntity(User user) {
    return new UserResponseDto(user.getUsername(),user.getName(),user.getSlackId(),user.getUserRoleEnum());
  }
}
