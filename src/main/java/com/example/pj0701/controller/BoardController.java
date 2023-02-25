package com.example.pj0701.controller;

import com.example.pj0701.security.userInfo.AuthUserInfo;
import com.example.pj0701.service.BoardService;
import com.example.pj0701.service.S3Service;
import com.example.pj0701.security.service.UserService;
import com.example.pj0701.util.CookieUtil;
import com.example.pj0701.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.events.Comment;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.*;

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
    public Object test (@RequestParam int articleNo, @RequestParam int userNo){
        return boardService.procTest(articleNo, userNo);}

//    private int loginUser(HttpServletRequest request, Model model){
//        AuthUserInfo userInfo = new AuthUserInfo();
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if(!(auth instanceof AnonymousAuthenticationToken)){
//            userInfo = (AuthUserInfo) auth.getPrincipal();
//        }
//        int userNo = userInfo.getUserNo();
//        model.addAttribute("userInfo", userInfo);
//        model.addAttribute("isLogin",userNo>0);
//        return userNo;
//    }
    private AuthUserInfo user(Authentication authentication){
        log.info("컨트롤러에서 뽑은 auth//{}",authentication);
        return authentication!=null? (AuthUserInfo) authentication.getPrincipal():new AuthUserInfo();
    }

//pageAct
    //메인리스트
    @RequestMapping("/list")
    public String boardList(@RequestParam(value = "pageNo", required = false)
                                        Integer pageNo,
                                        Model model,
                                        HttpServletRequest request,
                                        Authentication auth
                                        ){
        AuthUserInfo user = user(auth);
        int userNo=user.getUserNo();
        model.addAttribute("userInfo", user);
        model.addAttribute("isLogin",userNo>0);

        log.info("컨트롤러에서 뽑은 principal//{}",auth);

        if(pageNo==null){pageNo=1;}
        Map<String, Object> boardMap = boardService.articleListSel(1, 10);
        PaginationVO pvo = new PaginationVO();
        pvo.setCurrentPageNo(pageNo);
        pvo.setTotalArticleCount((int)boardMap.get("total"));
        model.addAttribute("pagination", pvo);
        model.addAttribute("shopList", boardMap.get("shopList"));
        model.addAttribute("list", boardMap.get("articleList"));
        return "board/boardList";
    }
    //쓰기페이지
    @RequestMapping("/writeForm")
    public String writeForm(HttpServletRequest request, Model model){
        //user_no
        List<Object> list = boardService.shopListSel();
        model.addAttribute("shopList", list);
        return "board/write";
    }
    //게시글내용
    @GetMapping("/article/{articleNo}")
    public String getArticleDetail (@PathVariable int articleNo,
                                                 HttpServletRequest request,
                                                 Model model,
                                                 HttpServletResponse response,
                                                 Authentication auth) {
        if(!CookieUtil.isInListAs(request, "read", String.valueOf(articleNo))){
            CookieUtil.appendValue(request, response, "read", String.valueOf(articleNo));
            boardService.articleHitUp(articleNo);
        }
        AuthUserInfo user = user(auth);
        int userNo=user.getUserNo();
        model.addAttribute("userInfo", user);
        model.addAttribute("isLogin",userNo>0);

        List<Object> article=boardService.articleDetailSel(articleNo, userNo);
        model.addAttribute("contents", article.get(0));
        model.addAttribute("photoList", article.get(1));
        log.info("test->{}",article.get(0));
        List<Object> list=boardService.commentListSel(articleNo, 1, 50);
        model.addAttribute("cnt", list.get(0));
        model.addAttribute("comment",list.get(1));
        return "board/detail";
    }



//data

    //댓글목록
//    @ResponseBody
//    @RequestMapping("/data/commentList")
//    public List<CommentVO> getCommentList (@RequestBody HashMap<String,Integer> params){
//        int articleNo = params.get("articleNo");
//        int pageNo = params.get("pageNo");
//        int cntPerPage = params.get("cntPerPage");
//        return boardService.commentListSel(articleNo, pageNo, cntPerPage);
//    }

    @RequestMapping("/shop/list")
    @ResponseBody
    public ResponseEntity<?> shopListData(@RequestParam(value = "pageNo", required = false) Integer pageNo){
        if(pageNo==null){pageNo=1;}
        return ResponseEntity.ok().body(boardService.shopListSel());
    }

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

            int shopNo=boardService.shopInfoIns(shopInfoVO);
            boardService.photoInsByShopNo(shopNo, fileList);

        }
        return "redirect:/";
    }
    //게시글 등록
    @PostMapping("/article")
    public String articleFileInsert(@RequestPart(value="files", required=false) List<MultipartFile> multipartFileList,
                                    @RequestParam HashMap<String,String> param,
                                                  HttpServletRequest request)throws IOException {
        log.info("params//{}", param);
        //TODO userNo from cookie
        int userNo=Integer.parseInt(CookieUtil.getCookieValue(request,"userNo"));
        //
        ArticleVO articleVO = new ArticleVO();
                articleVO.setShopNo(Integer.parseInt(param.get("shopNo")));
                articleVO.setTitle(param.get("title"));
                articleVO.setContents(param.get("contents"));
                articleVO.setUserNo(userNo);

        if(!multipartFileList.isEmpty()){
            List<Map<String,Object>> fileList = s3Service.upload(multipartFileList);
            int result = boardService.articlePhotoIns(articleVO, fileList);
            log.info("photoins//{}", result);
        }else{
            boardService.articleIns(articleVO);
        }

        return "redirect:/";
    }
    //댓글 등록
    @ResponseBody
    @RequestMapping("/comment/ins")
    public List<CommentVO> commentInsert(@RequestBody CommentVO commentVO,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws IOException {
        int userNo = Integer.parseInt(CookieUtil.getCookieValue(request, "userNo"));
        commentVO.setUserNo(userNo);
        boardService.commentIns(commentVO);
        return null;
//        return boardService.commentListSel(articleNo, 1, 50);
    }



}
