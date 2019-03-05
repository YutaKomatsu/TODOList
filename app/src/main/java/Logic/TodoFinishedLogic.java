package Logic;

import android.content.Context;

import java.util.Calendar;
import java.util.concurrent.CountDownLatch;

import DAO.TodoItemDAO;
import DAO.TodoItemPHP;
import model.TodoItem;

public class TodoFinishedLogic {
    public boolean execute(TodoItem todoItem, Context context) {
        boolean isLogic;
        //データベースにTODOを登録
        /*TodoItemDAO dao = new TodoItemDAO(context);
        boolean isLogic = dao.finished(todoItem);*/
        //MYSQL
        CountDownLatch latch = new CountDownLatch(1);
        TodoItemPHP todoItemPHP = new TodoItemPHP(todoItem,null,latch);
        todoItemPHP.execute("http://androidtest.php.xdomain.jp/FinishedTodo.php",TodoItemPHP.FINISHED_TODO);
        try {
            latch.await();
            isLogic = todoItemPHP.getResult();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        //結果を返す
        return isLogic;
    }
}
