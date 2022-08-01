package com.example.pj0701.proc;

import com.example.pj0701.vo.Pj07UserInfoVO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    /*
    test
     */
    List<Pj07UserInfoVO> selectAll();
    @ResultMap({"ResultMap.Pj07UserInfoVO"})
    @Select("select * from user_sign where user_id=#{userId}")
    Pj07UserInfoVO selectUser(String userId);
    /*
    아이디 중복체크용 조회
     */
    @ResultMap("ResultMap.integer")
    @Select("select count(*) from user_sign where user_id=#{userId}")
    int countUserById(String userId);
    /**
     * method : selectUserById
     * author : linohoch
     * description :
     * Select user by id pj 07 user info vo.
     * 회원의 전체정보 조인조회
     * @param userId the user id
 *              userId          varchar(50)     이메일
     * @return the pj 07 user info vo
     *          userNo          int             회원번호
     *          userId          varchar(50)     이메일
     *          pwLastChange    timestamp       마지막비밀번호변경일시
     *          pwEncryptYn     char            비밀번호암호화저장여부
     *          bannedYn        char            차단여부
     *          firstName       varchar(50)     이름
     *          lastName        varchar(50)     성
     *          userBirthDate   date            생년월일
     *          userSex         varchar(50)     성별
     *          joinDate        timestamp       가입일시
     *          ipAddress       varchar(20)     아이피주소
     *          lastLogin       timestamp       마지막로그인
     *          bannedDate      timestamp       차단일시
     *          bannedCode      varchar(50)     차단사유코드
     */
    @ResultMap({"ResultMap.Pj07UserInfoVO"})
    @Select("CALL p_user_info_sel(#{userId})")
    Pj07UserInfoVO selectUserById(String userId);


    /**
     * method : 회원등록
     * author : linohoch
     * description :
     * Create user int.
     *
     * @param pj07UserInfoVO the pj 07 user info vo
     * @return the int
     */
    @ResultMap("ResultMap.integer")
    @Select("CALL user_signup_ins(#{userId},#{firstName},#{lastName},#{userBirthDate},#{userSex},#{ipAddress},#{pw})")
    int createUser(Pj07UserInfoVO pj07UserInfoVO);

    /**
     * method : 소셜 회원등록
     * author : linohoch
     * description :
     * Create social user int.
     *
     * @param pj07UserInfoVO the pj 07 user info vo
     *                       userId         varchar(50)     이메일
     *                       firstName      varchar(50)     이름
     *                       lastName       varchar(50)     성
     *                       userBirthDate  date            생년월일
     *                       userSex        varchar(50)     성별
     *                       ipAddress      varchar(20)     아이피주소
     *                       userPw         varchar(50)     비밀번호
     *                       providerType   varchar(50)     소셜로그인(구글,네이버)
     *
     * @return the int
 *                          userNo          int
     */
    @Select("CALL user_social_ins_v1(#{userId},#{firstName},#{lastName},#{userBirthDate},#{userSex},#{ipAddress},#{userPw},#{providerType})")
    int createSocialUser(Pj07UserInfoVO pj07UserInfoVO);

    /*
    비밀번호 암호화 안된 더미 암호화
     */
    @Select("CALL p_user_pw_enc_upd(#{userNo},#{pw})")
    int updateUserPwEnc(int userNo, String userPw);
    /**
     * method : userLogin
     * author : linohoch
     * description :
     * User login string.
     * 비밀번호 비교하고 로그인 성공여부 반환
     * @param userId    the userId      varchar(50)     이메일
     * @param inputPw   the inputPw     varchar(50)     비밀번호
     * @return          the string                      로그인성공/비밀번호불일치/차단회원/회원없음
     */
    @Select("CALL pj1.p_user_login_v1(#{userId},#{inputPw})")
    String userLogin(String userId, String inputPw);

    /**
     * method : updateLoginTimestamp
     * author : linohoch
     * description :
     * Update login timestamp int.
     *
     * @param userNo the user no
     * @return the int
     */
    @Select("update pj1.user_basic set last_login=current_timestamp where user_no=#{userNo};")
    int updateLoginTimestamp(int userNo);

//    @Select("CALL pj1.p_user_refresh_chk_sel(#{token})")
//    boolean refreshTokenChk(String token);


    /**
     * method : 리프레시 토큰 가져오기
     * author : linohoch
     * description :
     * Select refresh token map.
     *
     * @param socialId the social id
     * @return the map
     * user_no
     * social_id
     * provider
     * refresh_token
     * ins_date
     * exp_date
     * up_date
     */
    @ResultMap("ResultMap.map")
    @Select("select * from pj1.socail_token where soial_id=#{socialId} order by ins_date DESC limit 1")
    Map<String, Object> selectRefreshToken(String socialId);
    /**
     * method : 리프레시 토큰 저장
     * author : linohoch
     * description :
     * Insert refresh token.
     *
     * @param userId       the user id
     * @param provider     the provider
     * @param refreshToken the refresh token
     * @param expDate      the exp date
     */
//    @Insert("insert into pj1.social_token(social_id, provider, refresh_token, exp_date) values(#{userId},#{provider},#{refreshToken},#{expDate})")
//    void insertRefreshToken(String userId, String provider,String refreshToken, String expDate);
    @Insert("CALL pj1.p_refresh_token_ins(#{userId},#{provider},#{refreshToken},#{expDate})")
    void insertRefreshToken(String userId, String provider,String refreshToken, String expDate);

    @Delete("CALL pj1.p_invalidate_refresh_token_del(#{userNo})")
    void deleteRefreshToken(int userNo);
}
