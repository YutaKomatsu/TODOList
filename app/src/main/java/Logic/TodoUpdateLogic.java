package Logic;

import android.content.Context;

import java.util.concurrent.CountDownLatch;

import DAO.TodoItemDAO;
import DAO.TodoItemPHP;
import model.TodoItem;

public class TodoUpdateLogic {
    public boolean execute(TodoItem todoItem, Context context) {
        boolean isLogic;
        //TODOを更新
        /*TodoItemDAO dao = new TodoItemDAO(context);
        boolean isLogic = dao.update(todoItem);*/
        //MYSQL
        CountDownLatch latch = new CountDownLatch(1);
        TodoItemPHP todoItemPHP = new TodoItemPHP(todoItem,null,latch);
        todoItemPHP.execute("http://androidtest.php.xdomain.jp/UpdateTodo.php",TodoItemPHP.UPDATE_TODO);
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
