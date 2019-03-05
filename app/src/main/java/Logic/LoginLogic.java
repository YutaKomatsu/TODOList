package Logic;
import android.content.Context;

import DAO.TodoUserPHP;
import model.User;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import DAO.TodoUserDAO;


public class LoginLogic {
    private User getUser = null;
    public boolean execute(User user, Context context) {

        //データベースからユーザーリストを取得
        //SQLite
        /*TodoUserDAO dao = new TodoUserDAO(context);
        User getUser = dao.find(user.getId());*/
        //MYSQL
        CountDownLatch latch = new CountDownLatch(1);
        TodoUserPHP php = new TodoUserPHP(user,latch);
        php.execute("http://androidtest.php.xdomain.jp/GetUser.php",TodoUserPHP.GET_USER_ITEM);
        try {
            latch.await();
            getUser = php.getUser();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        if(getUser != null) {
            //入力値とユーザーリストを比較
            if(StringUtils.equals(user.getId(),getUser.getId())) {
                if(StringUtils.equals(user.getPass(),getUser.getPass())) {
                    //一致した場合はユーザー名、権限をuserにセットした後、trueを返す
                    user.setName(getUser.getName());
                    user.setPermissons(getUser.getPermissions());
                    return true;
                }
            }
        }
        //一致しなかった場合falseを返す
        return false;
    }
}
