package model;

import java.io.Serializable;

public class Search implements Serializable {
    //インスタンス変数
    private boolean status;
    private TodoItem todoItem;
    private String expireType;
    private String finishedType;

    //コンストラクタ
    public Search() {}
    //詳細検索用
    public Search(boolean status, TodoItem todoItem, String expireType, String finishedType) {
        this.status = status;
        this.todoItem = todoItem;
        this.expireType = expireType;
        this.finishedType = finishedType;
    }


    //ゲッター
    public boolean getStatus() {
        return status;
    }
    public void changeStatus(){
        if(status){
            status = false;
        }else{
            status = true;
        }
    }
    public TodoItem getTodoItem() {
        return todoItem;
    }
    public String getExpireType() {
        return expireType;
    }
    public String getFinishedType() {
        return finishedType;
    }
}
