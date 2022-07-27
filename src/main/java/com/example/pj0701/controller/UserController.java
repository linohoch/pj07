package com.example.pj0701.controller;

import com.example.pj0701.security.jwt.JwtTokenProvider;
import com.example.pj0701.security.service.UserService;
import com.example.pj0701.vo.ResponseDTO;
import com.example.pj0701.vo.Pj07UserInfoVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/auth")
@AllArgsConstructor
public class UserController {

    private UserService userService;
    private JwtTokenProvider jwtTokenProvider;
    final private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/test")
    public ResponseEntity<?> test(){
        log.info("/auth/test");
        return ResponseEntity.ok().body("auth_path_success");
    }
    @RequestMapping("loginForm")
    public String toLoginForm(){
        log.info("/auth/loginForm");
        return "auth/login";
    }
    @RequestMapping("/signupForm")
    public String toSignupForm(){
        log.info("/auth/signupForm");
        return "auth/signup";
    }

    //----------------------------

    @RequestMapping("/checkId")
    public ResponseEntity<?> checkId(@RequestBody Pj07UserInfoVO pj07UserInfoVO){
        log.info("checkId: {}", pj07UserInfoVO);
        //response.status==400
        if(userService.findById(pj07UserInfoVO.getUserId())){
            return ResponseEntity.status(201).build();
        }
        return ResponseEntity.status(204).build();
    }
    //회원등록
    @PostMapping("/signup")//폼테스트
    public ResponseEntity<?> createUser(Pj07UserInfoVO pj07UserInfoVO){
        String encoded_pw=passwordEncoder.encode(pj07UserInfoVO.getPw());
        pj07UserInfoVO.setPw(encoded_pw);
        pj07UserInfoVO.setPwEncryptYn("y");
        if(userService.createUser(pj07UserInfoVO)!=null){
            return ResponseEntity.status(200).build();
        };
        return ResponseEntity.status(400).build();
    }
    //로그인 토큰 응답 확인 완료
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@Valid @RequestBody Pj07UserInfoVO pj07UserInfoVO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            //상세에러메세지를 쓰려면 bindingResult.getAllErrors()
            return ResponseEntity.badRequest().body("message: invalid email input");

        }else {
            Pj07UserInfoVO user = userService.getByCredentials( pj07UserInfoVO.getUserId(),
                                                                pj07UserInfoVO.getPw(),
                                                                passwordEncoder);
            if ("로그인성공".equals(user.getMsg())) {
                final Map<String,String> tokens = jwtTokenProvider.createTokens("",user.getUserId());
                final Pj07UserInfoVO responseDTO = Pj07UserInfoVO.builder()
                        .userId(user.getUserId())
                        .accessToken(tokens.get("accessToken"))
                        .refreshToken(tokens.get("refreshToken"))
                        .build();
                return ResponseEntity.ok().body(responseDTO);

            } else {
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .error(user.getMsg())
                        .build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        }
    }

    @RequestMapping("/logout")
    public void logout(){
    }
}
