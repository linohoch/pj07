package com.example.pj0701.proc;

import com.example.pj0701.vo.*;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {
    //procTest
    @ResultMap({"ResultMap.ArticleVO"})
    @Select("CALL pj1.p_test_grant()")
    List<ArticleVO> testproc();
    @ResultMap({"ResultMap.ArticleVO","ResultMap.PhotoVO"})
    @Select("CALL pj1.p_article_detail_sel_v1( #{articleNo}, #{userNo} )")
    List<Object> testproc1(int articleNo, int userNo);

    //업체등록,수정,삭제
    @Select("CAll pj1.p_shopInfo_ins(#{shopName},#{shopDisc},#{shopLat},#{shopLong},#{rpstPhoto},#{userNo},#{chrgrNo})")
    int shopInfoIns(ShopInfoVO shopInfoVO);
    //업체리스트 가저오기
    @ResultMap("ResultMap.ShopInfoVO")
    @Select("CALl pj1.p_shopInfo_list_sel(#{pageNo},#{cntPerPage},#{orderCol},#{orderBy})")
    List<Object> shopInfoListSel_1(int pageNo, int cntPerPage, char orderCol, char orderBy);

    //업체디테일
    @ResultMap("ResultMap.ShopInfoVO")
    @Select("Select * from shopInfo where shop_name=#{shopName}")
    ShopInfoVO shopInfoSel(char op, String shopName);
//    int shopInfoUpd(ShopInfoVO shopInfoVO);
//    int shopInfoDel(ShopInfoVO shopInfoVO);

    //게시글리스트 가져오기
    @ResultMap({"ResultMap.integer","ResultMap.integer","ResultMap.ArticleVO"})
    @Select("CALL pj1.p_article_list_sel_v4(#{pageNo},#{cntPerPage},#{orderCol},#{orderBy})")
    List<Object> articleListSel(int pageNo, int cntPerPage, char orderCol, char orderBy);

    /**
     * method : 게시글디테일 가져오기
     * author : linohoch
     * description :
     * Article detail sel list.
     *
     * @param articleNo the article no
     * @param userNo    the user no
     * @return the listarticle
     * shop_no
     * article_no
     * user_no
     * title
     * contents
     * hit_cnt
     * like_cnt
     * ins_date
     * up_date
     * shop_name
     * shop_disc
     * shop_lat
     * shop_long
     * star
     * like_yn      열람자 좋아요 여부
     * photo_no
     * url
     */
    @Select("CALL pj1.article_detail_sel(#{articleNo},#{userNo})")
    ArticleVO articleDetailSel(int articleNo, int userNo);

    @ResultMap({"ResultMap.ArticleVO","ResultMap.PhotoVO"})
    @Select("CALL pj1.p_article_detail_sel_v2( #{articleNo}, #{userNo} )")
    List<Object> articleDetailSel2(int articleNo, int userNo);

    //조회수
    @Update("UPDATE pj1.article SET hit_cnt=hit_cnt+1 WHERE article_no=#{articleNo}")
    void articleHitUpd(int articleNo);
    //좋아요
    //TODO proc으로 합쳐
    @Update("UPDATE pj1.article SET like_cnt=like_cnt+1 WHERE article_no=#{articleNo}")
    int articleLikeUp(int articleNo);
    @Update("UPDATE pj1.article SET like_cnt=like_cnt-1 WHERE article_no=#{articleNo}")
    int articleLikeDown(int articleNo);
    @Insert("Insert into pj1.user_article_likeYn(article_no, user_no, like_yn) VALUES(#{articleNo},#{userNo},'y')")
    int articleLikeUserIns(int articleNo, int userNo);
    @Delete("Delete FROM pj1.user_article_likeYn WHERE user_no=#{userNo} and article_no=#{articleNo}")
    int articleLikeUserDel(int articleNo, int userNo);
    //댓글 좋아요 수정
    @ResultMap({"ResultMap.integer"})
    @Select("CALL pj1.p_comment_like_up_v1(#{articleNo},#{commentNo},#{like},#{userNo})")
    int commentLikeUpsert(int articleNo, int commentNo, boolean like, int userNo);

    //-------------------------------------------------------------

    //게시글등록
    @ResultMap("ResultMap.integer")
    @Select("CALL pj1.p_article_ins(#{shopNo},#{userNo},#{title},#{contents})")
    int articleIns(ArticleVO articleVO);
    //첨부파일업로드
    int photoListIns(List<Map<String, Object>> imageInfoList);

    /**
     * method : 댓글등록
     * author : linohoch
     * description :
     * Comment ins int.
     *
     * @param commentVO the comment vo
     *                  commentNo   --부모댓글의 번호 -> 나의 parent_no
     *                  grp         --부모댓글의 그룹 루트(lv1) 댓글 번호
     *                  lv          --부모댓글의 레벨
     *                  seq         --부모댓글의 그룹내 댓글순서
     *                  articleNo   --게시글 번호
     *                  userNo      --댓글 작성자
     *                  contents    --댓글 내용
     * @return the int
     */
    @ResultMap("ResultMap.integer")
    @Select("CALL pj1.p_comment_ins_v1(#{commentNo},#{grp},#{lv},#{seq},#{articleNo},#{userNo},#{contents})")
    Integer commentIns(CommentVO commentVO);
    @Update("Update FROM pj1.comments(contents) VALUES(#{contents}) WHERE article_no=#{articleNo} and comment_no=#{commentNo}")
    int commentUpd(int articleNo, int commentNo, String contents);
    @Delete("Delete FROM pj1.comments WHERE article_no=#{articleNo} and comment_no=#{commentNo}")
    int commentDel(int articleNo, int commentNo);

    //댓글리스트 가져오기
    @ResultMap({"ResultMap.integer","ResultMap.CommentVO"})
    @Select("CALL pj1.p_comment_list_sel_v4(#{articleNo},#{userNo},#{pageNo},#{cntPerPage},#{orderSlct})")
    List<Object> commentListSel(int articleNo, int userNo, int pageNo, int cntPerPage, char orderSlct);
//    @ResultMap("ResultMap.CommentVO")
//    @Select("select * from comments where article_no=#{articleNo} order by grp desc, seq asc limit #{pageNo},#{cntPerPage}")
//    int commentListSel(int articleNo, int pageNo, int cntPerPage);

    //내 게시물 가져오기
    List<ArticleVO> myArticleListSel(int userNo, int pageNo, int cntPerPage);
}
