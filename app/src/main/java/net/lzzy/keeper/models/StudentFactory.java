package net.lzzy.keeper.models;

import android.content.Intent;

import net.lzzy.keeper.constants.DbConstants;
import net.lzzy.keeper.utils.AppUtils;
import net.lzzy.sqllib.SqlRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * @author Administrator
 */
public class StudentFactory {
    private static final StudentFactory OUR_INSTANCE = new StudentFactory();
    private SqlRepository<Student> repository;
    public static StudentFactory getInstance() {
        return OUR_INSTANCE;
    }

    private StudentFactory() {
        repository=new SqlRepository<>(AppUtils.getContext(),Student.class, DbConstants.packager);
    }

    public List<Student> getByClazz(UUID clazzId){
        try {
            return repository.getByKeyword(clazzId.toString(),new String[]{Student.COLUMN_CLASS_ID}
            ,true);
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Student> searchStudents(String kw,UUID classId){
        try {
            List<Student> result=new ArrayList<>();
            List<Student> students=repository.getByKeyword(kw,new String[]{Student.COLUMN_NAME,
                    Student.COLUMN_HOME},false);
            for(Student student:students){
                if(student.getClazzId().equals(classId)){
                    result.add(student);
                }
            }
            Collections.sort(result, new Comparator<Student>() {
                @Override
                public int compare(Student o1, Student o2) {
                    if(o1.getScore()>o2.getScore()){
                        return 1;
                    } else if(o1.getScore()<o2.getScore()){
                        return -1;
                    }else {
                        return o1.getName().compareTo(o2.getName());
                    }
                }
            });
            return result;
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static final String ACTION_CLASS_REFRESH="net.lzzy.keeper.refresh";
    public boolean insert(Student student){
        try{
            repository.insert(student);
            AppUtils.getContext().sendBroadcast(new Intent(ACTION_CLASS_REFRESH));
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Student student){
        try{
            repository.delete(student);
            AppUtils.getContext().sendBroadcast(new Intent(ACTION_CLASS_REFRESH));
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void update(Student student){
        repository.update(student);
    }
}
