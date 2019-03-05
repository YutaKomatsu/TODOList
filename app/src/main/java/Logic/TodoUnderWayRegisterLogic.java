package Logic;

import java.util.concurrent.CountDownLatch;

import DAO.TodoUnderWayPHP;
import model.TodoUnderWay;

public class TodoUnderWayRegisterLogic {
    public boolean execute(TodoUnderWay todoUnderWay) {
        boolean isLogic;
        //MYSQL
        CountDownLatch latch = new CountDownLatch(1);
        TodoUnderWayPHP todoUnderWayPHP = new TodoUnderWayPHP(todoUnderWay,latch);
        todoUnderWayPHP.execute("http://androidtest.php.xdomain.jp/RegisterTodoUnderWay.php",TodoUnderWayPHP.INSERT_TODO_UNDER_WAY);
        try {
            latch.await();
            isLogic = todoUnderWayPHP.getResult();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        //結果を返す
        return isLogic;
    }
}
