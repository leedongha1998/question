package com.bulbas23r.client.user.application.dto;

import com.bulbas23r.client.user.domain.model.User;
import common.model.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDataDto {
  private String username;
  private String name;
  private String slackEmail;
  private UserRoleEnum role;

  public UserDataDto(User user) {
    this.username = user.getUsername();
    this.name = user.getName();
    this.slackEmail = user.getSlackId();
    this.role = user.getUserRoleEnum();
  }
}
