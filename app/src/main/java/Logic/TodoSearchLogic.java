package Logic;

import android.content.Context;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import DAO.TodoItemDAO;
import DAO.TodoItemPHP;
import model.Search;
import model.TodoItem;

public class TodoSearchLogic {
    public List<TodoItem> execute(Search search, Context context) {
        List<TodoItem> todoItemList = null;
        //TODOを検索
        /*TodoItemDAO dao = new TodoItemDAO(context);
        List<TodoItem> todoItemList = dao.search(search);*/

        //MYSQL
        CountDownLatch latch = new CountDownLatch(1);
        TodoItemPHP todoItemPHP = new TodoItemPHP(null,search,latch);
        todoItemPHP.execute("http://androidtest.php.xdomain.jp/SearchTodo.php",TodoItemPHP.SEARCH_TODO);
        try {
            latch.await();
            todoItemList = todoItemPHP.getTodoList();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        //検索結果を返す
        return todoItemList;
    }
}
