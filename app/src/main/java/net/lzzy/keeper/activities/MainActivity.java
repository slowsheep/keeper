package net.lzzy.keeper.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import net.lzzy.keeper.R;
import net.lzzy.keeper.models.Clazz;
import net.lzzy.keeper.models.ClazzFactory;
import net.lzzy.keeper.models.StudentFactory;
import net.lzzy.keeper.receivers.AirModeReceiver;
import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;

import java.util.List;

/**
 * @author Administrator
 */
public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_CLASS_ID = "classId";
    private int[] imgs = new int[]{R.drawable.arsenal, R.drawable.chelsea, R.drawable.hotspur,
            R.drawable.mancity, R.drawable.manuni, R.drawable.swancity};
    private int index=0;
    private ClazzFactory factory;
    private GenericAdapter<Clazz> adapter;
    private List<Clazz> clazzes;
    private AirModeReceiver receiver;
    private BroadcastReceiver refreshReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(StudentFactory.ACTION_CLASS_REFRESH.equals(intent.getAction())){
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //region 1.初始化视图组件
        GridView gv=findViewById(R.id.activity_main_gv);
        findViewById(R.id.activity_main_btn_add).setOnClickListener(v -> popDialog());
        //endregion
        //region 1.1无数据视图
        View view= findViewById(R.id.activity_main_layout_none);
        gv.setEmptyView(view);
        //endregion
        //region 2.绑定数据到列表视图
        factory=ClazzFactory.getInstance();
        clazzes = factory.get();
        adapter=new GenericAdapter<Clazz>(this,R.layout.class_item, clazzes) {
            @Override
            public void populate(ViewHolder holder, Clazz clazz) {
                holder.setImageResource(R.id.class_item_img,clazz.getIcon())
                        .setTextView(R.id.class_item_tv_count,""+factory.getClazzCount(clazz))
                        .setTextView(R.id.class_item_tv_title,clazz.getName());
            }

            @Override
            public boolean persistInsert(Clazz clazz) {
                return factory.addClazz(clazz);
            }

            @Override
            public boolean persistDelete(Clazz clazz) {
                return factory.delete(clazz);
            }
        };
        gv.setAdapter(adapter);
        //endregion
        //region 3.表头搜索框
        Toolbar bar=findViewById(R.id.activity_main_bar);
        setSupportActionBar(bar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //endregion
        //region 4.长按使用SnackBar删除
        gv.setOnItemLongClickListener((parent, view12, position, id) -> {
            final Clazz clazz=adapter.getItem(position);
            adapter.remove(clazz);
            if(!clazzes.contains(clazz)){
                Snackbar.make(parent,"班级已经删除",Snackbar.LENGTH_LONG)
                        .setAction("撤销", v -> adapter.add(clazz)).show();
            }
            return true;
        });
        //endregion
        //region 5.更新数据
        gv.setOnItemClickListener((parent, view1, position, id) -> {
            Clazz clazz=adapter.getItem(position);
            Intent intent=new Intent(MainActivity.this,StudentsActivity.class);
            intent.putExtra(EXTRA_CLASS_ID,clazz.getId().toString());
            startActivity(intent);
        });
        //endregion
        //region 6.注册广播
        receiver=new AirModeReceiver();
        IntentFilter filter=new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(receiver,filter);
        IntentFilter refreshFilter=new IntentFilter(StudentFactory.ACTION_CLASS_REFRESH);
        registerReceiver(refreshReceiver,refreshFilter);
        //endregion
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(refreshReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_clazz,menu);
        MenuItem item=menu.findItem(R.id.menu_clazz_search);
        android.support.v7.widget.SearchView search=
                (android.support.v7.widget.SearchView) item.getActionView();
        search.setQueryHint("请输入关键词搜索");
        search.setIconifiedByDefault(false);
        android.support.v7.widget.SearchView.SearchAutoComplete auto=
                search.findViewById(R.id.search_src_text);
        auto.setHintTextColor(Color.WHITE);
        auto.setTextColor(Color.WHITE);
        search.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                clazzes.clear();
                if(TextUtils.isEmpty(newText)){
                    clazzes.addAll(factory.get());
                }else {
                    clazzes.addAll(factory.searchClazz(newText));
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void popDialog() {
        View view=getLayoutInflater().inflate(R.layout.dialog_add_class,null);
        final EditText edtName=view.findViewById(R.id.dialog_add_class_edt_name);
        final EditText edtIntro=view.findViewById(R.id.dialog_add_class_edt_intro);
        final ImageView img=view.findViewById(R.id.dialog_add_class_img);
        img.setImageResource(imgs[index]);
        img.setOnClickListener(v -> {
            index++;
            if(index==imgs.length){
                index=0;
            }
            img.setImageResource(imgs[index]);
        });
        new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("确定", (dialog, which) -> {
                    Clazz clazz=new Clazz();
                    String name=edtName.getText().toString();
                    if(TextUtils.isEmpty(name)){
                        Toast.makeText(MainActivity.this, "班级要有名称",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    clazz.setName(name);
                    clazz.setIntro(edtIntro.getText().toString());
                    clazz.setIcon(imgs[index]);
                    adapter.add(clazz);
                })
                .setNegativeButton("取消",null)
                .show();
    }

}
