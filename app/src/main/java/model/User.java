package model;

import java.io.Serializable;

//ユーザーの情報を保持するクラス
public class User implements Serializable {
    //インスタンス変数
    private String id;
    private String name;
    private String pass;
    private int permissions;

    //コンストラクタ
    public User() {}
    public User(String id){
        this.id = id;
    }
    //ログイン用
    public User(String id, String pass) {
        this.id = id;
        this.name = "";
        this.pass = pass;
        this.permissions = 0;
    }
    //ユーザー情報保持用
    public User(String id, String name, String pass,int permissions) {
        this.id = id;
        this.name = name;
        this.pass = pass;
        this.permissions = permissions;
    }

    //ゲッター
    public String getId() {
        return id;
    }
    public String getPass() {
        return pass;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getPermissions() {
        return permissions;
    }
    public void setPermissons(int permissions) {
        this.permissions = permissions;
    }
}
