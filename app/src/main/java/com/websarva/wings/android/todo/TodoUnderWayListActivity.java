package com.websarva.wings.android.todo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import ActionType.FromClass;
import Listener.RecyclerItemClickListener;
import Logic.TodoUnderWayListLogic;
import ActionType.DialogActionType;
import model.TodoItem;
import model.TodoUnderWay;
import model.User;

public class TodoUnderWayListActivity extends AppCompatActivity {
    //private変数
    private TodoItem todoItem;
    private User user;
    private RecyclerView view;
    private List<TodoUnderWay> todoUnderWayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_under_way_list);

        //TODO情報を取得
        Intent intent = getIntent();
        todoItem = (TodoItem)intent.getSerializableExtra("todoItem");
        user = (User)intent.getSerializableExtra("user");
        intent.removeExtra("todoItem");
        intent.removeExtra("user");

        //項目名、担当者をセット
        TextView workNameView = findViewById(R.id.tvWorkNameView);
        workNameView.setText("：" + todoItem.getWorkName());
        TextView workUserNameView = findViewById(R.id.tvWorkUserNameView);
        workUserNameView.setText("：" + todoItem.getUserName());

        //ボタンのセット
        Button underWayButton = findViewById(R.id.tvTodoUnderWayRegister);
        underWayButton.setOnClickListener(new TodoUnderWayRegister());
        Button underWayCloseButton = findViewById(R.id.tvTodoUnderWayClose);
        underWayCloseButton.setOnClickListener(new TodoUnderWayClose());

        //進捗報告リストの取得
        TodoUnderWayListLogic todoUnderWayListLogic = new TodoUnderWayListLogic();
        todoUnderWayList = todoUnderWayListLogic.execute(todoItem.getId());

        // リストにTODO進捗報告リストデータを受け渡す
        view = findViewById(R.id.tvTodoUnderWayList);
        // LinearLayoutManagerオブジェクトを生成
        LinearLayoutManager layoutManager = new LinearLayoutManager(TodoUnderWayListActivity.this);
        // RecyclerViewにレイアウトマネージャーとしてLinearLayoutManagerを設定
        view.setLayoutManager(layoutManager);
        // アダプターの生成
        RecyclerListAdapter adapter = new RecyclerListAdapter(todoUnderWayList);
        // RecyclerViewにアダプターをセット
        view.setAdapter(adapter);
        //リスナのセット
        view.addOnItemTouchListener(
                new RecyclerItemClickListener(TodoUnderWayListActivity.this,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // 進捗詳細画面へ遷移
                        Intent intent = new Intent(TodoUnderWayListActivity.this,TodoUnderWayInformationActivity.class);
                        intent.putExtra("todoUnderWay",todoUnderWayList.get(position));
                        intent.putExtra("user",user);
                        intent.putExtra("todoItem",todoItem);
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
                    // 右にスワイプされたらTODO進捗報告を消す
                    int swipedPosition = viewHolder.getAdapterPosition();
                    RecyclerListAdapter adapter = (RecyclerListAdapter) view.getAdapter();
                    adapter.remove(swipedPosition);
                }else{
                    //　左にスワイプされたら画面を更新させる
                    onRestart();
                }
            }
        };
        ItemTouchHelper mIth = new ItemTouchHelper(callback);
        mIth.attachToRecyclerView(view);
    }


    @Override
    public void onRestart() {
        super.onRestart();
        //進捗報告リストの取得
        TodoUnderWayListLogic todoUnderWayListLogic = new TodoUnderWayListLogic();
        todoUnderWayList = todoUnderWayListLogic.execute(todoItem.getId());
        // アダプターの生成
        RecyclerListAdapter adapter = new RecyclerListAdapter(todoUnderWayList);
        // RecyclerViewにアダプターをセット
        view.setAdapter(adapter);
    }

    //進捗報告ボタン押下時
    private class TodoUnderWayRegister implements View.OnClickListener{
        @Override
        public void onClick(View view){
            // TODO登録画面に遷移
            Intent intent = new Intent(TodoUnderWayListActivity.this, TodoUnderWayRegisterActivity.class);
            intent.putExtra("todoId",todoItem.getId());
            startActivity(intent);
        }
    }

    private class TodoUnderWayClose implements View.OnClickListener{
        @Override
        public void onClick(View view){
            // 画面を閉じる
            finish();
        }
    }


    private class RecyclerListViewHolder extends RecyclerView.ViewHolder{
        //リストに表示する項目
        private TextView tvData1;
        private TextView tvData2;
        private TextView tvData3;

        public RecyclerListViewHolder(View view){
            super(view);
            tvData1 = (TextView) view.findViewById(R.id.raw1);
            tvData2 = (TextView) view.findViewById(R.id.raw2);
            tvData3 = (TextView) view.findViewById(R.id.raw3);
        }
    }

    private class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListViewHolder> {
        private List<TodoUnderWay> todoUnderWayList;

        //TODOリストをセット
        public RecyclerListAdapter(List<TodoUnderWay> todoUnderWayList){
            this.todoUnderWayList = todoUnderWayList;
        }

        @Override
        public RecyclerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            //レイアウトインフレーターを取得
            LayoutInflater inflater = LayoutInflater.from(TodoUnderWayListActivity.this);
            //todo_list_rowをインフレートし一行分の画面部品とする
            View view = inflater.inflate(R.layout.todo_under_way_raw, parent, false);
            //ビューホルダーオブジェクトを生成
            RecyclerListViewHolder holder = new RecyclerListViewHolder(view);
            //生成したビューのホルダーを返す
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerListViewHolder holder, int position){
            //TODOをTODOリストから取得
            TodoUnderWay todoUnderWay = todoUnderWayList.get(position);
            //進捗掲題を取得
            String underWayTitle = todoUnderWay.getUnderWayTitle();
            //更新日を取得
            String updateDate = todoUnderWay.getUpdateDate();
            //ホルダー内のTextViewに項目名、担当者をセット
            holder.tvData1.setText((position + 1) + "");
            holder.tvData2.setText(underWayTitle);
            holder.tvData3.setText(updateDate);
        }

        @Override
        public int getItemCount(){
            //TODOリストの件数を返す
            return todoUnderWayList.size();
        }

        public void remove(int position) {
            if(user.getPermissions() == 2 || StringUtils.equals(user.getId(),todoItem.getUserId())) {
                // 削除確認ダイアログフラグメントオブジェクトを生成
                CheckDialogFragment dialogFragment = new CheckDialogFragment();

                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tv_check));
                bundle.putString("msg", getString(R.string.tv_deleteCheck)
                        + "\n" + getString(R.string.tv_underWayTitle) + ":" + todoUnderWayList.get(position).getUnderWayTitle());
                bundle.putString("button", getString(R.string.tv_delete));
                bundle.putInt("root", DialogActionType.UNDER_WAY_DELETE);
                bundle.putInt("fromClass",FromClass.TODO_UNDER_WAY_LIST_ACTIVITY);
                bundle.putBoolean("finishActivity", false);
                //削除する進捗の情報をを渡す
                bundle.putSerializable("todoUnderWay", todoUnderWayList.get(position));
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(), "CheckDialogFragment");
            }else{
                //エラーダイアログフラグメントオブジェクトを生成
                ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
                ArrayList<String> inputErrorList = new ArrayList<String>();
                inputErrorList.add("他のユーザーの進捗報告を削除することはできません。\n対象ユーザーまたは管理者に問い合わせください。");
                Bundle args = new Bundle();
                args.putStringArrayList("inputErrorList", inputErrorList);
                args.putBoolean("onResumeFlag",true);
                args.putInt("fromClass",FromClass.TODO_UNDER_WAY_LIST_ACTIVITY);
                errorDialogFragment.setArguments(args);
                errorDialogFragment.show(getSupportFragmentManager(), "ErrorDialogFragment");
            }
        }
    }
}
