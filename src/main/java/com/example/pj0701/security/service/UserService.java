package com.example.pj0701.security.service;

import com.example.pj0701.proc.UserMapper;
import com.example.pj0701.vo.Pj07UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService{//기존의것
    @Autowired
    UserMapper userMapper;

    public Pj07UserInfoVO createUser(Pj07UserInfoVO pj07UserInfoVO) {

        String userId = pj07UserInfoVO.getUserId();

        if(userMapper.selectUser(userId)!=null){
            log.warn("already exist id");
        }else{
            int userNo=userMapper.createUser(pj07UserInfoVO);
            return pj07UserInfoVO;
        }
        return null;
    };

    public boolean findById(String id){
        return userMapper.selectUser(id) != null;

    }

    public Pj07UserInfoVO checkPassword(String id,
                                        String password){

        String db_pw= userMapper.selectUser(id).getPw();
        if(password.equals(db_pw)){
            return userMapper.selectUser(id);
        }else{
            return null;
        }
    }

    public Pj07UserInfoVO getByCredentials(String input_id,
                                           String input_password,
                                           final PasswordEncoder passwordEncoder){
        final Pj07UserInfoVO pj07UserInfoVO = userMapper.selectUserById(input_id);

        if(pj07UserInfoVO.getPwEncryptYn().equals("N")) {
            String msg= userMapper.userLogin(input_id, input_password);
                if(msg.equals("로그인성공")){
                    userMapper.updateUserPwEnc(pj07UserInfoVO.getUserNo(), passwordEncoder.encode(input_password));
                }
            pj07UserInfoVO.setMsg(msg);
        }else{
            pj07UserInfoVO.setMsg(userMapper.userLogin(input_id, passwordEncoder.encode(input_password)));
        }
        return pj07UserInfoVO;

    }


}
