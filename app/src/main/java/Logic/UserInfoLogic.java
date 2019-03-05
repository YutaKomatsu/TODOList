package Logic;

import android.content.Context;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import DAO.TodoUserDAO;
import DAO.TodoUserPHP;
import model.User;

public class UserInfoLogic {
    public User execute(String userId, Context context) {
        User getUser;
        //データベースからユーザーリストを取得
        /*TodoUserDAO dao = new TodoUserDAO(context);
        User user = dao.find(userId);*/
        //MYSQL
        CountDownLatch latch = new CountDownLatch(1);
        User setuser = new User(userId);
        TodoUserPHP php = new TodoUserPHP(setuser,latch);
        php.execute("http://androidtest.php.xdomain.jp/GetUser.php",TodoUserPHP.GET_USER_ITEM);
        try {
            latch.await();
            getUser = php.getUser();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return getUser;
    }
}
