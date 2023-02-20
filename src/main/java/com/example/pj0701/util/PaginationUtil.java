package com.example.pj0701.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PaginationUtil {

    public Map<String, Object> pagination(int thisPageNo, int rowPerPage, int totalRowCnt, int blockSize) {
        Map<String, Object> pagination = new HashMap<>();

        int totalPageCnt = (int) Math.ceil((double) totalRowCnt / (double) rowPerPage);

        int prevPageNo = (thisPageNo == 1 ? 1 : thisPageNo - 1);
        int nextPageNo = (thisPageNo == totalPageCnt ? totalPageCnt : thisPageNo + 1);

        int thisFirstPageNo = thisPageNo==1 ? 1 : ( thisPageNo-1 )/blockSize + 1;
        int thisLastPageNo = thisFirstPageNo + blockSize - 1;

        int prevBlockLastPageNo = thisFirstPageNo==1?1: thisFirstPageNo - 1;
        int nextBlockFistPageNo = thisLastPageNo==totalPageCnt?thisLastPageNo: thisLastPageNo + 1;

        List<Integer> list= Stream.iterate(thisFirstPageNo, a -> a + 1)
                .limit(thisLastPageNo - thisFirstPageNo + 1)
                .collect(Collectors.toList());

        pagination.put("thisPageNo",thisPageNo);
        pagination.put("prevPageNo",prevPageNo); //
        pagination.put("nextPageNo",nextPageNo); //
        pagination.put("thisFistPageNo",thisFirstPageNo);
        pagination.put("thisLastPageNo",thisLastPageNo);
        pagination.put("prevBlockLastPageNo",prevBlockLastPageNo);
        pagination.put("nextBlockFistPageNo",nextBlockFistPageNo);
        pagination.put("totalPageCnt",totalPageCnt);
        pagination.put("totalRowCnt", totalRowCnt);
        pagination.put("pageNoList", list);

        return pagination;
    }
}
