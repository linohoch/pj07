package com.example.pj0701.controller;

import com.example.pj0701.service.BoardService;
import com.example.pj0701.service.S3Service;
import com.example.pj0701.security.service.UserService;
import com.example.pj0701.util.CookieUtil;
import com.example.pj0701.vo.ArticleVO;
import com.example.pj0701.vo.CommentVO;
import com.example.pj0701.vo.Pj07UserInfoVO;
import com.example.pj0701.vo.ShopInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.events.Comment;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final S3Service s3Service;
    private final BoardService boardService;
    private final UserService userService;

    @ResponseBody
    @RequestMapping("/test")
    public List<Object> test (){
        return boardService.shopListSel();
    }

//pageAct
    //메인리스트
    @RequestMapping("/list")
    public String boardList(@RequestParam(value = "pageNo", required = false) Integer pageNo, Model model){
        if(pageNo==null){pageNo=1;}
        List<ArticleVO> list = boardService.articleListSel(1, 10);
        model.addAttribute("list", list);
        return "board/boardList";
    }
    //쓰기페이지
    @RequestMapping("/writeForm")
    public String writeForm(Model model){
        //user_no
        List<Object> list = boardService.shopListSel();
        model.addAttribute("shopList", list);
        return "board/write";
    }
    //게시글내용
    @RequestMapping("/article/{articleNo}")
    public String getArticleDetail (@PathVariable int articleNo,
                                    HttpServletRequest request,
                                    Model model) {
        int userNo = Integer.parseInt(CookieUtil.getCookie(request, "userNo"));
        model.addAttribute("contents",
            boardService.articleDetailSel(articleNo, userNo));
        model.addAttribute("comment",
            boardService.commentListSel(articleNo, 1, 50));
        return "board/detail";
    }

//data

    //댓글목록
    @ResponseBody
    @RequestMapping("/data/commentList")
    public List<CommentVO> getCommentList (@RequestBody HashMap<String,Integer> params){
        int articleNo = params.get("articleNo");
        int pageNo = params.get("pageNo");
        int cntPerPage = params.get("cntPerPage");
        return boardService.commentListSel(articleNo, pageNo, cntPerPage);
    }

//    @RequestMapping("/shop/list")
//    @ResponseBody
//    public List<ShopInfoVO> shopListData(@RequestParam(value = "pageNo", required = false) Integer pageNo){
//        if(pageNo==null){pageNo=1;}
//        return boardService.shopListSel();
//    }

    //업체리스트 등록
    @RequestMapping("/shop/add")
    public String addShopInfo(@RequestPart(value="files", required=false) List<MultipartFile> multipartFileList,
                                    ShopInfoVO shopInfoVO){
//        if(shopInfoVO.getShopName().trim().isEmpty() || shopInfoVO.getShopLat().trim().isEmpty() ||
//                shopInfoVO.getShopLong().trim().isEmpty()){
//            System.out.println("test");
//        }
        if(!multipartFileList.isEmpty()){
            //샵등록은 대표사진 한개만
            List<Map<String,Object>> fileList = s3Service.upload(multipartFileList);
            String rpstPhotoUrl = fileList.get(0).get("urlPath").toString();
        System.out.println(rpstPhotoUrl);
            shopInfoVO.setRpstPhoto(rpstPhotoUrl);
            //1.shopInfo ins -> get shop_no
            //2.photos ins
        }
        return "redirect:/";
    }
    //게시글 등록
    @RequestMapping("/ins")
    public String articleFileInsert(@RequestPart(value="files", required=false) List<MultipartFile> multipartFileList,
                                            @RequestParam HashMap<String,String> param,
                                            HttpServletRequest request)throws IOException {
        log.info("params//{}", param);
        //TODO userNo from cookie
        request.getCookies();
        //
        ArticleVO articleVO = new ArticleVO();
                articleVO.setShopNo(Integer.parseInt(param.get("shopNo")));
                articleVO.setTitle(param.get("title"));
                articleVO.setContents(param.get("contents"));

        if(!multipartFileList.isEmpty()){
            List<Map<String,Object>> fileList = s3Service.upload(multipartFileList);
            int result = boardService.articlePhotoIns(articleVO, fileList);
            log.info("photoins//{}", result);
        }else{
            boardService.articleIns(articleVO);
        }

        return "redirect:/";
    }



}
