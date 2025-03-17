package common.dto.user;

import common.model.UserRoleEnum;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDto implements Serializable {
  private Long userId;
  private String username;
  private String password;
  private String slackId;
  private UserRoleEnum role;
  private Long tokenExpiration;
}
