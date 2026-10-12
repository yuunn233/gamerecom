package com.example.gamerecom.entity;

import javax.persistence.*;

//收藏信息
@Table(name = "comment_info")
public class Comment_info {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gamesId")
    private Long gamesId;

    @Column(name = "userId")
    private Long userId;

    @Column(name = "level")
    private Integer level;

    @Column(name = "commentTime")
    private String commentTime;

    @Column(name = "content")
    private String content;

    @Column(name = "rating")
    private String rating;

    //所属用户名
    @Transient
    private String userName;
    @Transient
    private String gamesName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGamesId() {
        return gamesId;
    }

    public void setGamesId(Long gamesId) {
        this.gamesId = gamesId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGamesName() {
        return gamesName;
    }

    public void setGamesName(String gamesName) {
        this.gamesName = gamesName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
