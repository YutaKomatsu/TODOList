package com.websarva.wings.android.todo;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Listener.RecyclerItemClickListener;
import Logic.TodoDeleteLogic;
import Logic.TodoItemLogic;
import Logic.TodoListLogic;
import Logic.TodoSearchLogic;
import Logic.UserInfoLogic;
import model.DialogActionType;
import model.Search;
import model.TodoItem;
import model.User;

public class TodoListActivity extends AppCompatActivity{
    //ログインユーザーの情報を格納するprivate変数
    User user;
    TextView welcomeUser;
    RecyclerView view;
    //TODOリスト
    List<TodoItem> todoList;
    //TODOリスト検索の条件
    Search search;
    //検索解除ボタン
    Button searchCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        //ログインしたユーザーの情報を取得
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        //インテントからユーザー情報を削除
        intent.removeExtra("user");

        welcomeUser = findViewById(R.id.tvWelcomeUser);
        welcomeUser.setText(getString(R.string.tv_welcomeUser,user.getName()));

        //検索ボタンのセット
        Button searchButton = findViewById(R.id.tvTodoSearch);
        searchButton.setOnClickListener(new TodoSearch());

        //検索解除ボタンのセット
        searchCancelButton = findViewById(R.id.tvTodoSearchCancel);
        searchCancelButton.setOnClickListener(new TodoSearchCancel());
        searchCancelButton.setEnabled(false);

        //TODOリストをデータベースから取得する
        TodoListLogic todoListLogic = new TodoListLogic();
        todoList = todoListLogic.execute(TodoListActivity.this);

        // リストにTODOリストデータを受け渡す
        view = findViewById(R.id.tvTodoList);
        // LinearLayoutManagerオブジェクトを生成
        LinearLayoutManager layoutManager = new LinearLayoutManager(TodoListActivity.this);
        // RecyclerViewにレイアウトマネージャーとしてLinearLayoutManagerを設定
        view.setLayoutManager(layoutManager);
        // アダプターの生成
        RecyclerListAdapter adapter = new RecyclerListAdapter(todoList);
        // RecyclerViewにアダプターをセット
        view.setAdapter(adapter);
        //リスナのセット
        view.addOnItemTouchListener(
                new RecyclerItemClickListener(TodoListActivity.this,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO詳細画面へ遷移
                        Intent intent = new Intent(TodoListActivity.this,TodoInformationActivity.class);
                        intent.putExtra("todoId",todoList.get(position).getId());
                        intent.putExtra("user",user);
                        startActivity(intent);
                    }
                })
        );

        //スワイプの設定
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // RIGHT:: delete item
                if (direction == ItemTouchHelper.RIGHT) {
                    // 右にスワイプされたらTODOを消す
                    int swipedPosition = viewHolder.getAdapterPosition();
                    RecyclerListAdapter adapter = (RecyclerListAdapter) view.getAdapter();
                    adapter.remove(swipedPosition);
                // LEFT:: finished
                }else if(direction == ItemTouchHelper.LEFT){
                    //　左にスワイプされたらTODOを完了させる
                    int swipedPosition = viewHolder.getAdapterPosition();
                    RecyclerListAdapter adapter = (RecyclerListAdapter) view.getAdapter();
                    adapter.finished(swipedPosition);
                }
            }
        };
        ItemTouchHelper mIth = new ItemTouchHelper(callback);
        mIth.attachToRecyclerView(view);
    }

    @Override
    public void onRestart(){
        super.onRestart();

        //ユーザー情報を取得
        UserInfoLogic userInfoLogic = new UserInfoLogic();
        user = userInfoLogic.execute(user.getId(),TodoListActivity.this);
        welcomeUser.setText(getString(R.string.tv_welcomeUser,user.getName()));

        if(search != null && search.getStatus()){
            TodoSearchLogic todoSearchLogic = new TodoSearchLogic();
            todoList = todoSearchLogic.execute(search,TodoListActivity.this);
            searchCancelButton.setEnabled(true);
        }else {
            //TODOリストをデータベースから取得する
            TodoListLogic todoListLogic = new TodoListLogic();
            todoList = todoListLogic.execute(TodoListActivity.this);
            searchCancelButton.setEnabled(false);
        }

        // アダプターの生成
        RecyclerListAdapter adapter = new RecyclerListAdapter(todoList);
        // RecyclerViewにアダプターをセット
        view.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //メニューインフレーターの取得
        MenuInflater inflater = getMenuInflater();
        //オプションメニュー用xmlファイルをインフレート
        inflater.inflate(R.menu.menu_option_todo_list,menu);
        //親クラスの同名メソッドを呼び出し、その戻り値を返却
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //選択されたメニューのIDを取得
        int itemId = item.getItemId();
        Intent intent;
        //IDによる分岐
        switch (itemId){
            case R.id.todoListLogout:
                // ログアウト確認ダイアログフラグメントオブジェクトを生成
                CheckDialogFragment dialogFragment = new CheckDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title",getString(R.string.tv_check));
                bundle.putString("msg",getString(R.string.tv_logoutCheck));
                bundle.putString("button",getString(R.string.tv_logout));
                bundle.putInt("root", DialogActionType.LOGOUT);
                bundle.putBoolean("finishActivity",true);
                bundle.putString("fromClass","TodoListActivity");
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(),"CheckDialogFragment");
                break;
            case R.id.todoRegister:
                // TODO登録画面に遷移
                intent = new Intent(TodoListActivity.this, TodoRegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.todoUserInformation:
                //ユーザー情報画面に遷移
                intent = new Intent(TodoListActivity.this,UserInformationActivity.class);
                intent.putExtra("user",user);
                startActivityForResult(intent,1);
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    //戻るボタン無効化
    @Override
    public void onBackPressed() {
    }

    //検索ボタン押下時
    private class TodoSearch implements View.OnClickListener{
        @Override
        public void onClick(View view){
            // TODO登録画面に遷移
            Intent intent = new Intent(TodoListActivity.this, TodoSearchActivity.class);
            startActivityForResult(intent,0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //検索条件を取得
                search = (Search) data.getSerializableExtra("search");
            }
        }else if(requestCode == 1){
            if (resultCode == RESULT_OK) {
                //ユーザー情報を更新
                user = (User) data.getSerializableExtra("user");
            }
        }
    }

    //検索解除ボタン押下時
    private class TodoSearchCancel implements View.OnClickListener{
        @Override
        public void onClick(View view){
            // TODO登録画面に遷移
            search.changeStatus();
            onRestart();
        }
    }

    private class RecyclerListViewHolder extends RecyclerView.ViewHolder{
        //リストに表示する項目
        private TextView tvData1;
        private TextView tvData2;
        private TextView tvData3;
        private TextView tvData4;

        public RecyclerListViewHolder(View view){
            super(view);
            tvData1 = (TextView) view.findViewById(R.id.raw1);
            tvData2 = (TextView) view.findViewById(R.id.raw2);
            tvData3 = (TextView) view.findViewById(R.id.raw3);
            tvData4 = (TextView) view.findViewById(R.id.raw4);
        }
    }

    private class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListViewHolder> {
        private List<TodoItem> todoList;

        //TODOリストをセット
        public RecyclerListAdapter(List<TodoItem> todoList){
            this.todoList = todoList;
        }

        @Override
        public RecyclerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            //レイアウトインフレーターを取得
            LayoutInflater inflater = LayoutInflater.from(TodoListActivity.this);
            //todo_list_rowをインフレートし一行分の画面部品とする
            View view = inflater.inflate(R.layout.todo_list_raw, parent, false);
            //ビューホルダーオブジェクトを生成
            RecyclerListViewHolder holder = new RecyclerListViewHolder(view);
            //生成したビューのホルダーを返す
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerListViewHolder holder, int position){
            //TODOをTODOリストから取得
            TodoItem todoItem = todoList.get(position);
            //項目名を取得
            String workName = todoItem.getWorkName();
            //担当者を取得
            String userName = todoItem.getUserName();
            //期限日を取得
            String expireDate = todoItem.getExpireDate();
            //完了日を取得
            String finishedDate = todoItem.getFinishedDate();
            if(StringUtils.equals(finishedDate,"0000-00-00")){
                finishedDate = null;
            }
            //ホルダー内のTextViewに項目名、担当者をセット
            holder.tvData1.setText(workName);
            holder.tvData2.setText(userName);
            holder.tvData3.setText(expireDate);
            holder.tvData4.setText(finishedDate);
        }

        @Override
        public int getItemCount(){
            //TODOリストの件数を返す
            return todoList.size();
        }

        public void remove(int position) {
            //管理者ユーザーまたは同一ユーザーの場合のみ削除
            if(user.getPermissions() == 2 || StringUtils.equals(user.getId(),todoList.get(position).getUserId())) {
                // 削除確認ダイアログフラグメントオブジェクトを生成
                CheckDialogFragment dialogFragment = new CheckDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tv_check));
                bundle.putString("msg", getString(R.string.tv_deleteCheck)
                        + "\n" + getString(R.string.tv_workName) + ":" + todoList.get(position).getWorkName()
                        + "\n" + getString(R.string.tv_workUserName) + ":" + todoList.get(position).getUserName());
                bundle.putString("button", getString(R.string.tv_delete));
                bundle.putInt("root", DialogActionType.DELETE);
                bundle.putString("fromClass", "TodoListActivity");
                bundle.putBoolean("finishActivity", false);
                //削除するTODOの情報をを渡す
                bundle.putSerializable("todoItem", todoList.get(position));
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(), "CheckDialogFragment");
            }else{
                //エラーダイアログフラグメントオブジェクトを生成
                ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
                ArrayList<String> inputErrorList = new ArrayList<String>();
                inputErrorList.add("他のユーザーのTODOを削除することはできません。\n対象ユーザーまたは管理者に問い合わせください。");
                Bundle args = new Bundle();
                args.putStringArrayList("inputErrorList", inputErrorList);
                args.putBoolean("onResumeFlag",true);
                args.putString("fromClass", "TodoListActivity");
                errorDialogFragment.setArguments(args);
                errorDialogFragment.show(getSupportFragmentManager(), "ErrorDialogFragment");
            }
        }

        public void finished(int position) {
            //管理者ユーザーまたは同一ユーザーの場合のみ削除
            if (user.getPermissions() == 2 || StringUtils.equals(user.getId(), todoList.get(position).getUserId())) {
                TodoItemLogic todoItemLogic = new TodoItemLogic();
                TodoItem oldItem = todoItemLogic.execute(todoList.get(position).getId(), TodoListActivity.this);
                if (StringUtils.isBlank(oldItem.getFinishedDate())
                        || StringUtils.equals(oldItem.getFinishedDate(), "null")
                        || StringUtils.equals(oldItem.getFinishedDate(), "0000-00-00")) {
                    // 項目完了確認ダイアログフラグメントオブジェクトを生成
                    CheckDialogFragment dialogFragment = new CheckDialogFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("title", getString(R.string.tv_check));
                    bundle.putString("msg", getString(R.string.tv_finishedCheck)
                            + "\n" + getString(R.string.tv_workName) + ":" + todoList.get(position).getWorkName()
                            + "\n" + getString(R.string.tv_workUserName) + ":" + todoList.get(position).getUserName());
                    bundle.putString("button", getString(R.string.tv_finish));
                    bundle.putInt("root", DialogActionType.FINISHED);
                    bundle.putString("fromClass", "TodoListActivity");
                    bundle.putBoolean("finishActivity", false);
                    //完了させるTODOの情報を生成
                    Calendar now = Calendar.getInstance();
                    now.add(Calendar.MONTH, 1);
                    String finishedDate = now.get(Calendar.YEAR) + "-" + now.get(Calendar.MONTH) + "-" + now.get(Calendar.DAY_OF_MONTH);
                    TodoItem finishedTodoItem = new TodoItem(todoList.get(position).getId(), finishedDate);
                    //完了処理用のTODOをセット
                    bundle.putSerializable("todoItem", finishedTodoItem);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(), "CheckDialogFragment");
                } else {
                    //エラーダイアログフラグメントオブジェクトを生成
                    ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
                    ArrayList<String> inputErrorList = new ArrayList<String>();
                    inputErrorList.add("対象のTODOは既に完了しています");
                    Bundle args = new Bundle();
                    args.putStringArrayList("inputErrorList", inputErrorList);
                    args.putBoolean("onResumeFlag", true);
                    args.putString("fromClass", "TodoListActivity");
                    errorDialogFragment.setArguments(args);
                    errorDialogFragment.show(getSupportFragmentManager(), "ErrorDialogFragment");
                }
            }else{
                //エラーダイアログフラグメントオブジェクトを生成
                ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
                ArrayList<String> inputErrorList = new ArrayList<String>();
                inputErrorList.add("他のユーザーのTODOを完了することはできません。\n対象ユーザーまたは管理者に問い合わせください。");
                Bundle args = new Bundle();
                args.putStringArrayList("inputErrorList", inputErrorList);
                args.putBoolean("onResumeFlag",true);
                args.putString("fromClass", "TodoListActivity");
                errorDialogFragment.setArguments(args);
                errorDialogFragment.show(getSupportFragmentManager(), "ErrorDialogFragment");
            }
        }
    }
}