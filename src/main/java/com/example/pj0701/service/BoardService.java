package com.example.pj0701.service;

import com.example.pj0701.proc.BoardMapper;
import com.example.pj0701.vo.ArticleVO;
import com.example.pj0701.vo.CommentVO;
import com.example.pj0701.vo.ShopInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Board service.
 */
@Slf4j
@Service
public class BoardService {

    BoardMapper boardMapper;
    BoardService(BoardMapper boardMapper){
        this.boardMapper=boardMapper;
    }

    public Object procTest(int articleNo, int userNo){
        return boardMapper.testproc1(articleNo, userNo);
    }

    public List<Object> shopListSel(){
        log.info("board service shoplist");
        return boardMapper.shopInfoListSel_1(1,50,'n','a');
//        (1,50,'n','a');
    }

    public List<ArticleVO> articleListSel(int pageNo, int cntPerPage){
        List<Object> list=boardMapper.articleListSel(pageNo, cntPerPage, 'd','d');
        log.info("[0]:board size // {}", list.get(0));
        log.info("[1]:board vo list // {}", list.get(1));
//        if(Integer.parseInt(list.get(0).toString())==0){}
        List<ArticleVO> voList = new ArrayList<>((List<ArticleVO>) list.get(1));
        return voList;
    }

    public void articleHitUp(int articleNo){
        boardMapper.articleHitUpd(articleNo);
    }

    public List<Object> articleDetailSel(int articleNo, int userNo){
        return boardMapper.articleDetailSel2(articleNo, userNo);
    }
    public List<Object> commentListSel(int articleNo, int pageNo, int cntPerPage){
        return boardMapper.commentListSel(articleNo, pageNo, cntPerPage, 'd');
    }

    public int articleLikeUp(int articleNo, int userNo){
        int result = boardMapper.articleLikeUserIns(articleNo, userNo);
        if(result>0) return boardMapper.articleLikeUp(articleNo);
        else return result;
    }
    public int articleLikeDown(int articleNo, int userNo){
        int result = boardMapper.articleLikeUserDel(articleNo, userNo);
        if(result>0) return boardMapper.articleLikeDown(articleNo);
        else return result;
    }

    public void commentIns(CommentVO commentVO){
        boardMapper.commentIns(commentVO);
    }
    public void commentUpd(int articleNo, int commentNo, String contents){
        boardMapper.commentUpd(articleNo, commentNo, contents);
    }
    public void commentDel(int articleNo, int commentNo){
        boardMapper.commentDel(articleNo, commentNo);
    };

    public int articleIns(ArticleVO articleVO){
        return boardMapper.articleIns(articleVO);
    }

    /**
     * method : articlePhotoIns
     * author : linohoch
     * description :
     * Article photo ins int.
     * 1.S3 imgList -> articleIns articleNo -> photoIns
     * @param articleVO the article vo
     * @param imgList   the img list
     * @return the int
     */
    public int articlePhotoIns(ArticleVO articleVO, List<Map<String,Object>> imgList){
        int articleNo = boardMapper.articleIns(articleVO);
        if(articleNo>0){
            return photoIns(articleNo, imgList);
        }else{
            return 0;
        }
    }
    public int photoIns(int articleNo, List<Map<String,Object>> imgList) {
        imgList.forEach(map->{ map.put("articleNo",articleNo);});
        return boardMapper.photoListIns(imgList);
    }
    //TODO proc 합쳐
    public void photoInsByShopNo(int shopNo, List<Map<String,Object>> imgList) {
        imgList.forEach(map->{ map.put("shopNo",shopNo);});
        boardMapper.photoListIns(imgList);
    }

    public int shopInfoIns(ShopInfoVO shopInfoVO){
        return boardMapper.shopInfoIns(shopInfoVO);
    }
}
