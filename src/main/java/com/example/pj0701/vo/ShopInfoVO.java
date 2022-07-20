package com.example.pj0701.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class ShopInfoVO {
    private int shopNo;
    private String shopName;
    private String shopDisc;
    private String shopLat;
    private String shopLong;
    private String rpstPhoto;
    private int star;
    private int userNo;
    private String insDate;
    private String upDate;
    private int chrgrNo;
}

