package com.bulbas23r.client.user.presentation.controller;

import com.bulbas23r.client.user.application.service.UserResponseDto;
import common.dto.user.UserDetailsDto;
import com.bulbas23r.client.user.application.dto.UserSignUpRequestDto;
import com.bulbas23r.client.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  //회원가입
  @PostMapping("/sign-up")
  public ResponseEntity<?> signUp(@RequestBody UserSignUpRequestDto userRequestDto) throws Exception {
    userService.signUp(userRequestDto);
    return ResponseEntity.ok().build();
  }

  // todo 얘는 로그인이 아니라 auth 에서 회원 정보 가져오는거라 이름 바꾸고 위치도 바꿔주기
  @GetMapping("/login")
  public UserDetailsDto getUserDetails(@RequestParam String username) {
    return userService.getUserDetails(username);
  }

  //유저 : 개인 상세 정보 조회
  @GetMapping("/me")
  public ResponseEntity<?> getUserDetail() {
    // SecurityContextHolder에서 인증 정보 가져오기
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName(); // 필터에서 설정한 username
    UserResponseDto userResponseDto = userService.getUserDetail(username);
    return ResponseEntity.ok(userResponseDto);
  }


  //관리자 : 유저 상세 정보 조회
  @GetMapping("/{userId}")
  @PreAuthorize("hasAnyAuthority('MASTER')")
  public ResponseEntity<?> getUserDetailForRegister(@PathVariable("userId") Long userId) {
    UserResponseDto userResponseDto = userService.getUserDetailForMaster(userId);
    return ResponseEntity.ok(userResponseDto);
  }

//  //관리자 : 유저 리스트 조회
//  @GetMapping("/reg/list")
//  public ResponseEntity getUsers(@RequestParam("page") int page,
//      @RequestParam("size") int size,
//      @RequestParam("sort") String sort) {
//    CustomPageResponse<UserDataForRegisterDto> userList = userService.getUsers(page-1, size, sort);
//    return ResponseEntity.ok(userList);
//  }
//
//  //관리자 : 유저 검색
//  @GetMapping("/reg/search")
//  public ResponseEntity searchUser(@RequestParam(name="text") String searchText,
//      @RequestParam(name = "page", defaultValue = "1") int page,
//      @RequestParam(name = "size", defaultValue = "10") int size,
//      @RequestParam(value = "sort", defaultValue = "createdAt") String sort) {
//    CustomPageResponse<UserDataForRegisterDto> userList = userService.searchUsers(searchText, page-1, size, sort);
//    return ResponseEntity.ok(userList);
//  }
//
//  //관리자 : 유저 정보 수정
//  @PatchMapping("/reg/{userId}")
//  public ResponseEntity patchUser(@PathVariable("userId") Long userId,
//      @RequestBody UserPatchRequestForRegisterDto patchDto) {
//    UserResponseForRegisterDto<UserDataForRegisterDto> patchUser =
//        userService.patchUser(userId, patchDto);
//    return ResponseEntity.ok(patchUser);
//  }
//  //관리자 : 유저 삭제
//  @DeleteMapping("/reg/{userId}")
//  public ResponseEntity deleteUser(@PathVariable("userId") Long userId) {
//    userService.deleteUser(userId);
//    return ResponseEntity.noContent().build();
//  }
}
