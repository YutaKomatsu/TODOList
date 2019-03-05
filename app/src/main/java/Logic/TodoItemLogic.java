package Logic;

import android.content.Context;

import java.util.concurrent.CountDownLatch;

import
DAO.TodoItemDAO;
import DAO.TodoItemPHP;
import model.TodoItem;

public class TodoItemLogic {
    public TodoItem execute(int todoId, Context context) {
        boolean isLogic;
        TodoItem returnTodoItem;
        //データベースからTODOを取得
        /*TodoItemDAO dao = new TodoItemDAO(context);
        TodoItem todoItem = dao.find(todoId);*/

        //MYSQL
        TodoItem todoItem = new TodoItem(todoId);
        CountDownLatch latch = new CountDownLatch(1);
        TodoItemPHP todoItemPHP = new TodoItemPHP(todoItem,null,latch);
        todoItemPHP.execute("http://androidtest.php.xdomain.jp/GetTodo.php",TodoItemPHP.GET_TODO_ITEM);
        try {
            latch.await();
            returnTodoItem = todoItemPHP.getTodoItem();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return returnTodoItem;
    }

}
