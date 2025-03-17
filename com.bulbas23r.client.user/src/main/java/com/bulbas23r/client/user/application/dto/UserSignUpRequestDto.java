package com.bulbas23r.client.user.application.dto;

import common.model.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpRequestDto {
  private String username;
  private String password;
  private String slackId;
  private String name;
  private UserRoleEnum userRoleEnum;
}
