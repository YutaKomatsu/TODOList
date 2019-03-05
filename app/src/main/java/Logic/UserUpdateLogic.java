package Logic;

import android.content.Context;

import java.util.concurrent.CountDownLatch;

import DAO.TodoUserDAO;
import DAO.TodoUserPHP;
import model.User;

public class UserUpdateLogic {
    public boolean execute(User user, Context context) {
        boolean isLogic;
        //ユーザー情報を更新
        /*TodoUserDAO dao = new TodoUserDAO(context);
        boolean isLogic = dao.update(user);*/
        //MYSQL
        CountDownLatch latch = new CountDownLatch(1);
        TodoUserPHP php = new TodoUserPHP(user,latch);
        php.execute("http://androidtest.php.xdomain.jp/UpdateUser.php",TodoUserPHP.UPDATE_USER);
        try {
            latch.await();
            isLogic = php.getResult();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        //結果を返す
        return isLogic;
    }
}
