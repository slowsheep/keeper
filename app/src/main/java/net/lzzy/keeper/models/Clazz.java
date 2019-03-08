package net.lzzy.keeper.models;

import net.lzzy.sqllib.Ignored;
import net.lzzy.sqllib.Sqlitable;

/**
 * @author Administrator
 */
public class Clazz extends BaseEntity implements Sqlitable {
    @Ignored
    public static final String COLUMN_NAME="name";
    @Ignored
    public static final String COLUMN_INTRO="intro";
    private String name;
    private String intro;
    private int icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    @Override
    public boolean needUpdate() {
        return false;
    }
}
