package net.lzzy.keeper.models;

import net.lzzy.sqllib.Column;
import net.lzzy.sqllib.Ignored;
import net.lzzy.sqllib.Sqlitable;
import net.lzzy.sqllib.Table;

import java.util.UUID;

/**
 * @author Administrator
 */
@Table(name = "students")
public class Student extends BaseEntity implements Sqlitable {
    @Ignored
    static final String COLUMN_CLASS_ID="classId";
    @Ignored
    static final String COLUMN_NAME="name";
    @Ignored
    static final String COLUMN_HOME="home";
    private String name;
    private boolean gender;
    private String home;
    private float score;
    @Column(name = "classId",type = Column.DbType.STRING)
    private UUID clazzId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public UUID getClazzId() {
        return clazzId;
    }

    public void setClazzId(UUID clazzId) {
        this.clazzId = clazzId;
    }

    @Override
    public boolean needUpdate() {
        return false;
    }
}
