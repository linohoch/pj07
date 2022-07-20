package com.example.pj0701.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationVO {

    private int pageSize=10;  //-------------------상수
    private int firstPageNo;
    private int currentPageNo;  //------------------------사용자입력 디폴트1
    private int lastPageNo;

    private int firstArticleIndex;
    private int articleCountPerPage=25; //---------상수
    private int lastArticleIndex;

    private int totalPageCount;
    private int totalArticleCount;  //------------가장먼저DA


    public int getPageSize() {
        return pageSize;
    }
    //---------------------------------------------------------------
    public int getFirstPageNo() {
        int firstPageNo=1;
        firstPageNo=((getCurrentPageNo()-1)/getPageSize())*pageSize+1;
        return firstPageNo;
    }
    public int getCurrentPageNo() {
        return currentPageNo;
    }
    public int getLastPageNo() {
        int lastPageNo=3;
        lastPageNo=getFirstPageNo()+getPageSize()-1;
        if(lastPageNo>getTotalPageCount()) {
            lastPageNo = getTotalPageCount();
        }
        return lastPageNo;
    }
    //----------------------------DA-------------------------------
    public int getTotalArticleCount() {
        return totalArticleCount;
    }
    public int getFirstArticleIndex() {
        int firstArticleIndex=1;
        firstArticleIndex = (getCurrentPageNo() - 1)*getArticleCountPerPage()+1;

        return firstArticleIndex;
    }
    public int getLastArticleIndex() {
        int lastArticleIndex=5;
        lastArticleIndex = getCurrentPageNo()*getArticleCountPerPage();

        return lastArticleIndex;
    }
    //-----------------------------------------------------------------------
    public int getArticleCountPerPage() {
        int articleCountPerpage=5;
        return articleCountPerPage;
    }
    public int getTotalPageCount() {
        int totalPageCount;
        totalPageCount=((getTotalArticleCount()-1)/getArticleCountPerPage())+1;

        return totalPageCount;
    }

    //-------------------------------------------------------------------
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public void setArticleCountPerPage(int articleCountPerPage) {
        this.articleCountPerPage = articleCountPerPage;
    }

    public void setFirstPageNo(int firstPageNo) {
        this.firstPageNo = firstPageNo;
    }
    public void setLastPageNo(int lastPageNo) {
        this.lastPageNo = lastPageNo;
    }
    public void setCurrentPageNo(int currentPageNo) {
        this.currentPageNo = currentPageNo;
    }
    public void setFirstArticleIndex(int firstArticleIndex) {
        this.firstArticleIndex = firstArticleIndex;
    }
    public void setLastArticleIndex(int lastArticleIndex) {
        this.lastArticleIndex = lastArticleIndex;
    }
    public void setTotalPageCount(int totalPageCount) {
        this.totalPageCount = totalPageCount;
    }
    public void setTotalArticleCount(int totalArticleCount) {
        this.totalArticleCount = totalArticleCount;
    }

}
