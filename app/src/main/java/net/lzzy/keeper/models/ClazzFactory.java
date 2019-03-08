package net.lzzy.keeper.models;

import net.lzzy.keeper.constants.DbConstants;
import net.lzzy.keeper.utils.AppUtils;
import net.lzzy.sqllib.SqlRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Administrator
 */
public class ClazzFactory {
    private static final ClazzFactory OUR_INSTANCE = new ClazzFactory();
    private SqlRepository<Clazz> repository;

    public static ClazzFactory getInstance() {
        return OUR_INSTANCE;
    }

    private ClazzFactory() {
        repository=new SqlRepository<>(AppUtils.getContext(),Clazz.class, DbConstants.packager);
    }

    public List<Clazz> get(){
        return repository.get();
    }

    public Clazz getById(String id){
        return  repository.getById(id);
    }

    public List<Clazz> searchClazz(String kw){
        try {
            List<Clazz> clazzes=repository.getByKeyword(kw,new String[]{Clazz.COLUMN_NAME,
                    Clazz.COLUMN_INTRO},false);
            Collections.sort(clazzes, new Comparator<Clazz>() {
                @Override
                public int compare(Clazz o1, Clazz o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            return clazzes;
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private boolean isClazzExists(Clazz clazz){
        try {
            return repository.getByKeyword(clazz.getName(),new String[]{Clazz.COLUMN_NAME},true)
                    .size()>0;
        } catch (IllegalAccessException|InstantiationException e) {
            e.printStackTrace();
            return true;
        }
    }

    public boolean addClazz(Clazz clazz){
        try{
            if(!isClazzExists(clazz)){
                repository.insert(clazz);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Clazz clazz){
        try{
            if(isClazzEmpty(clazz)){
                repository.delete(clazz);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void update(Clazz clazz){
        repository.update(clazz);
    }

    public int getClazzCount(Clazz clazz){
        return StudentFactory.getInstance().getByClazz(clazz.getId()).size();
    }

    private boolean isClazzEmpty(Clazz clazz){
        return getClazzCount(clazz)==0;
    }
}
