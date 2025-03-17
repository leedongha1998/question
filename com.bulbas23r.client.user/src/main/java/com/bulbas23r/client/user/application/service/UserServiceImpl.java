package com.bulbas23r.client.user.application.service;

import com.bulbas23r.client.user.application.dto.UserDataDto;
import com.bulbas23r.client.user.application.dto.UserSignUpRequestDto;
import common.dto.user.UserDetailsDto;
import com.bulbas23r.client.user.domain.model.User;
import com.bulbas23r.client.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDetailsDto getUserDetails(String username) {
    //repository 조회
    try {
      User user = userRepository.findByUsernameForAuth(username);

      //to dto
      UserDetailsDto detailsDto = new UserDetailsDto();
      detailsDto.setUserId(user.getUserId());
      detailsDto.setUsername(user.getUsername());
      detailsDto.setPassword(user.getPassword());
      detailsDto.setRole(user.getUserRoleEnum());
      return detailsDto;
    } catch (Exception e ) {
      log.error(e.getMessage());
      return null;
    }
  }

  //회원가입
  public void signUp(UserSignUpRequestDto signUpRequestDto) {
    // username 중복 확인
    boolean isExist = userRepository.existsByUsername(signUpRequestDto.getUsername());

    if(isExist) {
      throw new IllegalArgumentException("Username is already in use");
    }

    String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());

    User user = new User(signUpRequestDto, encodedPassword);
    // db 저장
    userRepository.save(user);
  }

  // 유저 : 개인의 상세 정보 조회
  public UserResponseDto getUserDetail(String username) {
    // repository 찾기
    User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
    // dto 변환
    return UserResponseDto.fromEntity(user);
  }

  @Override
  public UserResponseDto getUserDetailForMaster(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    return UserResponseDto.fromEntity(user);
  }

//
//  // 관리자 : 유저의 상세 정보 조회
//  public UserResponseForRegisterDto getUserDetailForRegister(Long userId) {
//    //repository 찾기
//    User user = userRepository.findById(userId);
//    //dto 변환
//    UserDataForRegisterDto userDataForRegisterDto = new UserDataForRegisterDto(user);
//    return new UserResponseForRegisterDto<UserDataForRegisterDto>(200, "유저 상세 정보 조회 성공", userDataForRegisterDto);
//  }
//
//  // 관리자 : 유저 리스트 조회
//  public CustomPageResponse<UserDataForRegisterDto> getUsers(int page, int size, String sort) {
//    //sort
//    Sort sortBy = Sort.by(Sort.Direction.DESC, sort);
//    Pageable pageable = PageRequest.of(page, size, sortBy);
//    // repository 검색
//    Page<User> userList = userRepository.findAll(pageable);
//    // Dto 변환
//    Page<UserDataForRegisterDto> dtoPage = userList.map(UserDataForRegisterDto:: new);
//    return new CustomPageResponse<>(dtoPage);
//  }
//
//  // 관리자 : 유저 검색
//  public CustomPageResponse<UserDataForRegisterDto> searchUsers(
//      String searchText, int page, int size, String sort) {
//    //sort
//    Sort sortBy = Sort.by(Sort.Direction.DESC, sort);
//    Pageable pageable = PageRequest.of(page, size, sortBy);
//
//    Page<User> searchUsers = userRepository.search(searchText, pageable);
//    // to Dto
//    Page<UserDataForRegisterDto> userList = searchUsers.map(UserDataForRegisterDto::new);
//    return new CustomPageResponse<>(userList);
//
//  }
//
//  //관리자 : 유저 수정
//  public UserResponseForRegisterDto patchUser(Long userId, UserPatchRequestForRegisterDto patchDto) {
//    //repository 검색
//    User user = userRepository.findById(userId);
//    //수정 및 유효성 검사(unique 값)
//    if(patchDto.getName() == null || patchDto.getUsername() == null ||
//        patchDto.getSlackEmail() == null || patchDto.getUserRoleEnum() == null) {
//      throw new BusinessLogicException(ExceptionCode.NOT_PROVIDE_LANGUAGE);
//    }
//    user.setName(patchDto.getName());
//    user.setUsername(patchDto.getUsername());
//    user.setSlackEmail(patchDto.getSlackEmail());
//    user.setUserRoleEnum(patchDto.getUserRoleEnum());
//    user.setConfirmed(patchDto.isConfirmed());
//    //db 저장
//    userRepository.save(user);
//    //to dto
//    UserDataForRegisterDto patchedUserDto = new UserDataForRegisterDto(user);
//    //return
//    return new UserResponseForRegisterDto<> (200, "유저 정보 수정 완료", patchedUserDto);
//  }
//
//  //관리자 : 유저 삭제
//  public void deleteUser(Long userId) {
//    //repository 찾기
//    User user = userRepository.findById(userId);
//    //논리적 삭제 -> isDeleted = true
//    user.setIsDeleted(true);
//    //db 저장
//    userRepository.save(user);
//  }
}
