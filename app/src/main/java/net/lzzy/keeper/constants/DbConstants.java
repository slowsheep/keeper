package net.lzzy.keeper.constants;

import net.lzzy.keeper.R;
import net.lzzy.keeper.utils.AppUtils;
import net.lzzy.sqllib.DbPackager;

/**
 * @author Administrator
 */
public final class DbConstants {
    private DbConstants(){}
    /** 数据库名称 **/
    private static final String DB_NAME="student.db";
    /** 数据库版本 **/
    private static final int DB_VERSION=1;

    public static DbPackager packager;

    static {
        packager=DbPackager.getInstance(AppUtils.getContext(),DB_NAME,DB_VERSION, R.raw.models);
    }
}
