package Logic;

import java.util.concurrent.CountDownLatch;

import DAO.TodoUnderWayPHP;
import model.TodoUnderWay;

public class TodoUnderWayLogic {
    public TodoUnderWay execute(TodoUnderWay todoUnderWay) {
        TodoUnderWay getTodoUnderWay = null;
        //MYSQL
        CountDownLatch latch = new CountDownLatch(1);
        TodoUnderWayPHP php = new TodoUnderWayPHP(todoUnderWay,latch);
        php.execute("http://androidtest.php.xdomain.jp/GetTodoUnderWay.php",TodoUnderWayPHP.GET_TODO_UNDER_WAY_ITEM);
        try {
            latch.await();
            getTodoUnderWay = php.getTodoUnderWayItem();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return getTodoUnderWay;
    }
}
