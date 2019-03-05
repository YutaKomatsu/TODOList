package Logic;

import android.content.Context;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import DAO.TodoUserDAO;
import DAO.TodoUserPHP;
import model.User;

public class UserListLogic {
    public List<User> execute(Context context) {
        List<User> todoUserList = null;
        //データベースからユーザーリストを取得
        /*TodoUserDAO dao = new TodoUserDAO(context);
        List<User> todoUserList = dao.findAll();*/
        //MYSQL
        CountDownLatch latch = new CountDownLatch(1);
        TodoUserPHP php = new TodoUserPHP(null,latch);
        php.execute("http://androidtest.php.xdomain.jp/GetUserList.php",TodoUserPHP.GET_USER_LIST);
        try {
            latch.await();
            todoUserList = php.getUserList();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return todoUserList;
    }
}

