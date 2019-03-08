package net.lzzy.keeper.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lljjcoder.style.citylist.utils.CityListLoader;
import com.lljjcoder.style.citythreelist.CityBean;
import com.lljjcoder.style.citythreelist.ProvinceActivity;

import net.lzzy.keeper.R;
import net.lzzy.keeper.models.Clazz;
import net.lzzy.keeper.models.ClazzFactory;
import net.lzzy.keeper.models.Student;
import net.lzzy.keeper.models.StudentFactory;
import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Administrator
 */
public class StudentsActivity extends AppCompatActivity {
    private UUID classId;
    private List<Student> students;
    private StudentFactory factory;
    private GenericAdapter<Student> adapter;
    private ListView lvStudents;
    private AbsListView.MultiChoiceModeListener listener=new AbsListView.MultiChoiceModeListener() {
        List<Student> selectedStudents;
        int selectedCount;
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            Student student=adapter.getItem(position);
            if(checked){
                selectedStudents.add(student);
                selectedCount++;
            }else {
                selectedCount--;
                selectedStudents.remove(student);
            }
            TextView tv=new TextView(StudentsActivity.this);
            String title="已选中"+selectedCount+"项";
            tv.setTextColor(Color.WHITE);
            tv.setText(title);
            mode.setCustomView(tv);
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            selectedCount=0;
            selectedStudents=new ArrayList<>();
            mode.getMenuInflater().inflate(R.menu.menu_student,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            if(item.getItemId()==R.id.menu_student_delete){
                new AlertDialog.Builder(StudentsActivity.this)
                        .setMessage("确认要删除这"+selectedCount+"项吗？")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNegativeButton("取消",null)
                        .setPositiveButton("删除", (dialog, which) -> {
                            for(Student student:selectedStudents){
                                adapter.remove(student);
                            }
                            mode.finish();
                        }).show();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //region 1.标题栏搜索&透明状态栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_students);
        Toolbar bar = findViewById(R.id.activity_students_bar);
        setSupportActionBar(bar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //endregion
        //region 2.数据绑定
        //region 2.1相关数据
        classId = UUID.fromString(getIntent().getStringExtra(MainActivity.EXTRA_CLASS_ID));
        Clazz clazz = ClazzFactory.getInstance().getById(classId.toString());
        factory = StudentFactory.getInstance();
        students = factory.getByClazz(classId);
        //endregion
        //region 2.2列表视图初始化
        lvStudents = findViewById(R.id.activity_students_lv);
        View empty = findViewById(R.id.activity_students_layout_none);
        lvStudents.setEmptyView(empty);
        //endregion
        //region 2.3班级数据绑定
        ImageView img = findViewById(R.id.layout_class_img);
        img.setImageResource(clazz.getIcon());
        TextView tvName = findViewById(R.id.layout_class_tv_name);
        tvName.setText(clazz.getName());
        TextView tvIntro = findViewById(R.id.layout_class_tv_intro);
        tvIntro.setText(clazz.getIntro());
        //endregion
        bindStudents();
        //endregion
        CityListLoader.getInstance().loadProData(this);
        findViewById(R.id.activity_students_btn_add).setOnClickListener(v ->
                popDialog(false, null));
        //region 3.多选删除
        lvStudents.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        lvStudents.setMultiChoiceModeListener(listener);
        //endregion
        //region 4.更新数据
        lvStudents.setOnItemClickListener((parent, view, position, id) ->
                popDialog(true, adapter.getItem(position)));
        //endregion
    }

    private void bindStudents() {
        //region 2.4学生数据绑定
        adapter = new GenericAdapter<Student>(this, R.layout.student_item, students) {
            private float touchX1;
            private static final float TOUCH_MIN_DISTANCE = 100;
            private boolean isDeleting=false;
            @Override
            public void populate(ViewHolder holder, Student student) {
                int resId = student.isGender() ? R.drawable.m : R.drawable.fm;
                holder.setImageResource(R.id.student_item_img_gender, resId)
                        .setTextView(R.id.student_item_tv_score, String.valueOf(student.getScore()))
                        .setVisibility(R.id.student_item_btn_delete,View.GONE)
                        .setVisibility(R.id.student_item_tv_score,View.VISIBLE)
                        .setTextView(R.id.student_item_tv_name, student.getName())
                        .setTextView(R.id.student_item_tv_home, student.getHome());
                Button btnDel=holder.getView(R.id.student_item_btn_delete);
                TextView tvScore=holder.getView(R.id.student_item_tv_score);
                btnDel.setOnClickListener(v -> {
                    isDeleting=false;
                    adapter.remove(student);
                    if(!students.contains(student)){
                        Snackbar.make(lvStudents,"数据已删除",Snackbar.LENGTH_SHORT)
                                .setAction("撤销", v1 -> adapter.add(student))
                                .show();
                    }
                });
                holder.getConvertView().setOnTouchListener((v, event) -> {
                    slideToRemove(event,btnDel,tvScore,student);
                    return true;
                });
            }

            private void slideToRemove(MotionEvent event, Button btnDel, TextView tvScore, Student student) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        touchX1=event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        float touchX2=event.getX();
                        if(touchX2-touchX1>TOUCH_MIN_DISTANCE){
                            if(!isDeleting){
                                btnDel.setVisibility(View.VISIBLE);
                                tvScore.setVisibility(View.GONE);
                                isDeleting=true;
                            }
                        }else {
                            if(btnDel.isShown()){
                                btnDel.setVisibility(View.GONE);
                                tvScore.setVisibility(View.VISIBLE);
                                isDeleting=false;
                            }else if(!isDeleting){
                                popDialog(true,student);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public boolean persistInsert(Student student) {
                return factory.insert(student);
            }

            @Override
            public boolean persistDelete(Student student) {
                return factory.delete(student);
            }
        };
        lvStudents.setAdapter(adapter);
        //endregion
    }

    private TextView tvArea;
    private boolean gender;
    private EditText edtName;
    private EditText edtScore;
    private RadioGroup rgGender;

    private void popDialog(final boolean update, final Student student) {
        View view = getDialogView();
        if (update) {
            tvArea.setText(student.getHome());
            edtName.setText(student.getName());
            edtScore.setText(String.valueOf(student.getScore()));
            int resId = student.isGender() ? R.id.dialog_add_student_rb_male
                    : R.id.dialog_add_student_rb_female;
            rgGender.check(resId);
        }
        new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("确定", (dialog, which) -> {
                    String name = edtName.getText().toString();
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(StudentsActivity.this, "要有名称",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    float score;
                    try {
                        score = Float.parseFloat(edtScore.getText().toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast.makeText(StudentsActivity.this, "分数格式错误",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (update) {
                        student.setScore(score);
                        student.setName(name);
                        student.setGender(gender);
                        student.setHome(tvArea.getText().toString());
                        factory.update(student);
                        adapter.notifyDataSetChanged();
                    } else {
                        addStudent(name, score);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void addStudent(String name, float score) {
        Student student = new Student();
        student.setClazzId(classId);
        student.setGender(gender);
        student.setHome(tvArea.getText().toString());
        student.setName(name);
        student.setScore(score);
        adapter.add(student);
    }

    @NonNull
    private View getDialogView() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_student, null);
        view.findViewById(R.id.dialog_add_student_layout_area).setOnClickListener(v -> {
            Intent intent = new Intent(StudentsActivity.this, ProvinceActivity.class);
            startActivityForResult(intent, ProvinceActivity.RESULT_DATA);
        });
        tvArea = view.findViewById(R.id.dialog_add_student_tv_area);
        edtName = view.findViewById(R.id.dialog_add_student_edt_name);
        edtScore = view.findViewById(R.id.dialog_add_student_edt_score);
        gender = true;
        rgGender = view.findViewById(R.id.dialog_add_student_rg_gender);
        rgGender.setOnCheckedChangeListener((group, checkedId) ->
                gender = checkedId == R.id.dialog_add_student_rb_male);
        return view;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ProvinceActivity.RESULT_DATA) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    return;
                }
                //省份结果
                CityBean province = data.getParcelableExtra("province");
                //城市结果
                CityBean city = data.getParcelableExtra("city");
                //区域结果
                CityBean area = data.getParcelableExtra("area");
                tvArea.setText(province.getName().concat(city.getName() + area.getName()));
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_clazz, menu);
        MenuItem item = menu.findItem(R.id.menu_clazz_search);
        android.support.v7.widget.SearchView search =
                (android.support.v7.widget.SearchView) item.getActionView();
        search.setQueryHint("请输入关键词搜索");
        search.setIconifiedByDefault(false);
        android.support.v7.widget.SearchView.SearchAutoComplete auto =
                search.findViewById(R.id.search_src_text);
        auto.setHintTextColor(Color.WHITE);
        auto.setTextColor(Color.WHITE);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                students.clear();
                if (TextUtils.isEmpty(newText)) {
                    students.addAll(factory.getByClazz(classId));
                } else {
                    students.addAll(factory.searchStudents(newText,classId));
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
