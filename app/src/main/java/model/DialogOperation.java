package model;

import java.io.Serializable;

public class DialogOperation implements Serializable {
    //ダイアログの処理待ち用変数
    private boolean waitMode = false;
    //ダイアログの処理の成否確認用変数
    private boolean success = false;

    public void changeWaitMode(){
        if(waitMode){
            waitMode = false;
        }else{
            waitMode = true;
        }
    }

    public void changeSuccess(){
        if(success){
            success = false;
        }else{
            success = true;
        }
    }

    public boolean getWaitMode(){
        return waitMode;
    }

    public boolean getSuccess(){
            return success;
    }

}
