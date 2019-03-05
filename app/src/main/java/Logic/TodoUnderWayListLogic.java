package Logic;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import DAO.TodoUnderWayPHP;
import model.TodoUnderWay;

public class TodoUnderWayListLogic {
    public List<TodoUnderWay> execute(int todoId) {
        List<TodoUnderWay> todoUnderWayList = null;
        //MYSQL
        CountDownLatch latch = new CountDownLatch(1);
        TodoUnderWayPHP php = new TodoUnderWayPHP(new TodoUnderWay(todoId),latch);
        php.execute("http://androidtest.php.xdomain.jp/GetTodoUnderWayList.php",TodoUnderWayPHP.GET_TODO_UNDER_WAY_LIST);
        try {
            latch.await();
            todoUnderWayList = php.getTodoUnderWayList();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return todoUnderWayList;
    }

}
