package com.example.gamerecom.entity;

import javax.persistence.*;
import java.util.List;

//游戏详情信息
@Table(name = "games_info")
public class Games_info {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "details")
    private String details;

    @Column(name = "price")
    private Double price;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "scores")
    private Integer scores;

    @Column(name = "sales")
    private Integer sales;

    @Column(name = "hot")
    private Integer hot;

    //是否推荐
    @Column(name = "isRecommend")
    private String isRecommend = "否";

    public Integer getHot() {
        return hot;
    }

    public void setHot(Integer hot) {
        this.hot = hot;
    }

    public String getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(String isRecommend) {
        this.isRecommend = isRecommend;
    }

    @Column(name = "maker")
    private String maker;

    @Column(name = "launchDate")
    private String launchDate;

    @Column(name = "typeId")
    private Long typeId;

    @Column(name = "pictures")
    private String pictures;

    //类型名称 对应typeId
    @Transient
    private String typeName;

    //图片id所属列表 对应pictures
    @Transient
    private List<Long> fileList;

    //收藏id
    @Transient
    private Long cartInfoId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Integer getScores() {
        return scores;
    }

    public void setScores(Integer scores) {
        this.scores = scores;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }


    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(String launchDate) {
        this.launchDate = launchDate;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getPictures() {
        return pictures;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<Long> getFileList() {
        return fileList;
    }

    public void setFileList(List<Long> fileList) {
        this.fileList = fileList;
    }

    public Long getCartInfoId() {
        return cartInfoId;
    }

    public void setCartInfoId(Long cartInfoId) {
        this.cartInfoId = cartInfoId;
    }
}
