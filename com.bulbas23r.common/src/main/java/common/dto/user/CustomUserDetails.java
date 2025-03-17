package common.dto.user;

import common.model.UserRoleEnum;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserDetails implements UserDetails {

  private Long id;
  private String username;
  private String password;
  // 단일 권한 반환
  private GrantedAuthority authority;

  public CustomUserDetails(Long id, String username, String password, UserRoleEnum role) {
    this.id = id;
    this.username = username;
    this.password = password;
    // UserRoleEnum을 GrantedAuthority로 변환
    this.authority = new SimpleGrantedAuthority(String.valueOf(role));
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(authority);  // 단일 권한을 컬렉션으로 반환
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

}
