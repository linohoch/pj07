package com.example.pj0701.controller;

import com.example.pj0701.security.userInfo.AuthUserInfo;
import com.example.pj0701.service.BoardService;
import com.example.pj0701.service.S3Service;
import com.example.pj0701.util.CookieUtil;
import com.example.pj0701.vo.ArticleVO;
import com.example.pj0701.vo.CommentVO;
import com.example.pj0701.vo.Pj07UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class BoardRestApi {

    final BoardService boardService;
    final S3Service s3Service;

    private AuthUserInfo user(Authentication authentication){
        return authentication!=null? (AuthUserInfo) authentication.getPrincipal():new AuthUserInfo();
    }
    @Operation(method = "GET", summary = "게시글 쓰기")
    @PostMapping
    public ResponseEntity<?> postArticle(@RequestPart(value="files", required=false) List<MultipartFile> multipartFileList,
                                         @RequestBody ArticleVO articleVO,
                                         HttpServletRequest request){
//        int userNo=Integer.parseInt(CookieUtil.getCookieValue(request,"userNo"));
//        articleVO.setUserNo(userNo);
        try{
            if(!multipartFileList.isEmpty()){
                List<Map<String,Object>> fileList = s3Service.upload(multipartFileList);
                int result = boardService.articlePhotoIns(articleVO, fileList);
                log.info("photoins//{}", result);
            }else{
                boardService.articleIns(articleVO);
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(201).build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                        description = "게시글 리스트 반환",
                        content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ArticleVO.class)))),
            @ApiResponse(responseCode = "201", description = "게시글 반환 실패")
    })
    @Operation(method = "GET", summary = "게시글 리스트 가져오기")
    @GetMapping()
    public ResponseEntity<?> getArticleList(@Parameter(name="page", in=ParameterIn.QUERY, required = true)
                                            @RequestParam("page") int pageNo,
                                            @Parameter(name="per-page", in=ParameterIn.QUERY, required = true)
                                            @RequestParam("per-page") int cntPerPage){
        try {
            Map<String, Object> boardMap = boardService.articleListSel(pageNo, cntPerPage);
            return ResponseEntity.ok().body(boardMap.get("articleList"));
        }catch (Exception e){
            return ResponseEntity.status(201).build();
        }

    }
    @Operation(method = "GET", summary = "n번 게시물 내용 가져오기")
    @GetMapping("/{articleNo}")
    public ResponseEntity<?> getArticleDetail(@PathVariable("articleNo") int articleNo,
                                              @RequestBody Pj07UserInfoVO userInfoVO,
                                              HttpServletRequest request){
        int userNo = 0; //TODO
        List<Object> article=boardService.articleDetailSel(articleNo, userInfoVO.getUserNo());
        List<Object> comment=boardService.commentListSel(articleNo, userNo,1, 50);
        Map<String, Object> result = new HashMap<>();
            result.put("article",article);
            result.put("comment",comment);
        return ResponseEntity.ok().body(result);
    }
    @Operation(method = "PUT", summary = "n번 게시물 수정")
    @PutMapping("/{articleNo}")
    public ResponseEntity<?> putArticleCont(@PathVariable("articleNo") int articleNo,
                                            @RequestBody ArticleVO articleVO){
        return null;
    }
    @Operation(method = "DELETE", summary = "n번 게시물 삭제")
    @DeleteMapping("/{articleNo}")
    public ResponseEntity<?> deleteArticle(@PathVariable("articleNo") int articleNo){
        return null;
    }
    @Operation(method = "PUT", summary = "n번 게시물 좋아요 추가")
    @PutMapping("/{articleNo}/like")
    public ResponseEntity<?> putLike(@PathVariable("articleNo") int articleNo,
//                                     @PathVariable("userNo") int userNo
                                     HttpServletRequest request,
                                     Authentication auth
    ){
        int userNo=user(auth).getUserNo();
//        int userNo=Integer.parseInt(CookieUtil.getCookieValue(request,"userNo"));
        int result = boardService.articleLikeUp(articleNo, userNo);
        if(result>0) return ResponseEntity.status(201).build();
        return ResponseEntity.status(202).build();
    }
    @Operation(method = "DELETE", summary = "n번 게시물 좋아요 삭제")
    @DeleteMapping("/{articleNo}/like")
    public ResponseEntity<?> deleteLike(@PathVariable("articleNo") int articleNo,
//                                        @PathVariable("userNo") int userNo
                                        HttpServletRequest request,
                                        Authentication auth
    ){
        int userNo=user(auth).getUserNo();
//        int userNo=Integer.parseInt(CookieUtil.getCookieValue(request,"userNo"));
        int result = boardService.articleLikeDown(articleNo, userNo);
        if(result>0) return ResponseEntity.status(201).build();
        return ResponseEntity.status(202).build();
    }

    //--
    @Operation(method = "POST", summary = "n번 게시물 코멘트 입력")
    @PostMapping("/{articleNo}/comment")
    public ResponseEntity<?> postComment(@PathVariable("articleNo") int articleNo,
                                         @RequestBody CommentVO commentVO,
                                         Authentication auth){
        commentVO.setUserNo(user(auth).getUserNo());
        boardService.commentIns(commentVO);
        return null;
    }

    @Operation(method = "GET", summary = "n번 게시물 코멘트 조회")
    @GetMapping("/{articleNo}/comment")
    public ResponseEntity<?> getCommentList(@PathVariable("articleNo") int articleNo,
                                            @RequestParam("page") int pageNo,
                                            @RequestParam("per-page") int cntPerPage){
        return null;
    }
    @Operation(method = "PUT", summary = "n번 게시물 m번 코멘트 수정")
    @PutMapping("/{articleNo}/comment/{commentNo}")
    public ResponseEntity<?> putComment(@PathVariable("articleNo") int articleNo,
                                        @PathVariable("commentNo") int commentNo,
                                        @RequestBody CommentVO commentVO){
        boardService.commentUpd(articleNo, commentNo, commentVO.getContents());
        return null;
    }
    @Operation(method = "DELETE", summary = "n번 게시물 m번 코멘트 삭제")
    @DeleteMapping("/{articleNo}/comment/{commentNo}")
    public ResponseEntity<?> deleteComment(@PathVariable("articleNo") int articleNo,
                                           @PathVariable("commentNo") int commentNo){
        boardService.commentDel(articleNo, commentNo);
        return null;
    }
    @Operation(method = "PUT", summary = "댓글 좋아요 추가, 삭제")
    @PutMapping("/{articleNo}/comment/{commentNo}/like")
    public ResponseEntity<?> putLikeComment(@PathVariable("articleNo") int articleNo,
                                            @PathVariable("commentNo") int commentNo,
                                            @RequestParam("like") boolean like,
                                            Authentication auth
    ){
        int userNo=user(auth).getUserNo();
        log.info("글{}댓글{}라이크{}사람{}",articleNo, commentNo, like, userNo);
        int result = boardService.commentLikeUp(articleNo, commentNo, like, userNo);
//        if(result>0) return ResponseEntity.status(201).build();
        log.info("in put like//{}",result);
        return ResponseEntity.status(201).body(result);
    }
}
