package Logic;

import android.content.Context;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import DAO.TodoItemDAO;
import DAO.TodoItemPHP;
import model.TodoItem;

public class TodoListLogic {
    public List<TodoItem> execute(Context context) {
        List<TodoItem> todoItemList = null;
        //データベースからTODOリストを取得
        /*TodoItemDAO dao = new TodoItemDAO(context);
        List<TodoItem> todoItemList = dao.findAll();*/

        //MYSQL
        CountDownLatch latch = new CountDownLatch(1);
        TodoItemPHP todoItemPHP = new TodoItemPHP(null,null,latch);
        todoItemPHP.execute("http://androidtest.php.xdomain.jp/GetTodoList.php",TodoItemPHP.GET_TODO_LIST);
        try {
            latch.await();
            todoItemList = todoItemPHP.getTodoList();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return todoItemList;
    }
}
