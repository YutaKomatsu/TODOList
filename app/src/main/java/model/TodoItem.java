package model;

import java.io.Serializable;

public class TodoItem implements Serializable {
    //インスタンス変数
    private int id;
    private String workName;
    private String userId;
    private String userName;
    private String expireDate;
    private String finishedDate;

    //コンストラクタ
    public TodoItem() {}
    //削除用
    //コンストラクタ
    public TodoItem(int todoId) {
        this.id = todoId;
    }
    //検索用
    public TodoItem(String workName, String userName) {
        this.workName = workName;
        this.userName = userName;
    }
    //詳細検索用
    public TodoItem(String workName, String userName , String expireDate, String finishedDate) {
        this.workName = workName;
        this.userName = userName;
        this.expireDate = expireDate;
        this.finishedDate = finishedDate;
    }
    //Todo完了用
    public TodoItem(int id ,String finishedDate) {
        this.id = id;
        this.finishedDate = finishedDate;
    }
    //Todo登録用
    public TodoItem(String workName, String userId , String expireDate ) {
        this.workName = workName;
        this.userId = userId;
        this.expireDate = expireDate;
    }
    //todo更新用
    public TodoItem(int id, String workName, String userId , String expireDate ) {
        this.id = id;
        this.workName = workName;
        this.userId = userId;
        this.expireDate = expireDate;
    }
    public TodoItem(int id, String workName, String userId , String expireDate, String finishedDate) {
        this.id = id;
        this.workName = workName;
        this.userId = userId;
        this.expireDate = expireDate;
        this.finishedDate = finishedDate;
    }
    //todo一覧表示用
    public TodoItem(int id, String workName, String userId , String userName , String expireDate, String finishedDate ) {
        this.id = id;
        this.workName = workName;
        this.userId = userId;
        this.userName = userName;
        this.expireDate = expireDate;
        this.finishedDate = finishedDate;
    }

    //ゲッター
    public int getId() {
        return id;
    }
    public String getWorkName() {
        return workName;
    }
    public String getUserId() {
        return userId;
    }
    public String getUserName() {
        return userName;
    }
    public String getExpireDate(){
        return expireDate;
    }
    public String getFinishedDate() {
        return finishedDate;
    }
}
