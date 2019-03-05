package Logic;

import android.content.Context;

import java.util.concurrent.CountDownLatch;

import DAO.TodoUserDAO;
import DAO.TodoUserPHP;
import model.User;

public class UserRegisterLogic {
    public boolean execute(User user, Context context) {
        boolean isLogic;
        //データベースからユーザーリストを取得
        /*TodoUserDAO dao = new TodoUserDAO(context);
        boolean isLogic = dao.insert(user);*/
        //MYSQL
        CountDownLatch latch = new CountDownLatch(1);
        TodoUserPHP php = new TodoUserPHP(user,latch);
        php.execute("http://androidtest.php.xdomain.jp/RegisterUser.php",TodoUserPHP.INSERT_USER);
        try {
            latch.await();
            isLogic = php.getResult();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return isLogic;
    }
}
