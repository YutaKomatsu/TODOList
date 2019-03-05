package model;

import java.io.Serializable;

public class TodoUnderWay implements Serializable {
    //private変数
    private int todoId;
    private int subId;
    private String underWayTitle;
    private String underWay;
    private String updateDate;

    //コンストラクタ
    public TodoUnderWay(){}
    //表示対象取得用
    public TodoUnderWay(int todoId){
        this.todoId = todoId;
    }
    //登録用
    public TodoUnderWay(int todoId, String underWayTitle, String underWay){
        this.todoId = todoId;
        this.underWayTitle = underWayTitle;
        this.underWay = underWay;
    }
    //更新用
    public TodoUnderWay(int todoId,int subId, String underWayTitle, String underWay){
        this.todoId = todoId;
        this.subId = subId;
        this.underWayTitle = underWayTitle;
        this.underWay = underWay;
    }

    //表示用
    public TodoUnderWay(int todoId,int subId, String underWayTitle, String underWay, String updateDate){
        this.todoId = todoId;
        this.subId = subId;
        this.underWayTitle = underWayTitle;
        this.underWay = underWay;
        this.updateDate = updateDate;
    }

    //削除用
    public TodoUnderWay(int todoId,int subId){
        this.todoId = todoId;
        this.subId = subId;
    }

    //ゲッター
    public int getTodoId(){
        return todoId;
    }

    public int getSubId() {
        return subId;
    }

    public String getUnderWayTitle(){
        return underWayTitle;
    }

    public String getUnderWay(){
        return underWay;
    }

    public String getUpdateDate(){
        return updateDate;
    }
}
