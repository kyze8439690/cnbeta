package me.yugy.cnbeta.dao.dbinfo;

import android.provider.BaseColumns;

import me.yugy.cnbeta.dao.datahelper.AllNewsDataHelper;
import me.yugy.cnbeta.utils.database.Column;
import me.yugy.cnbeta.utils.database.SQLiteTable;

/**
 * Created by yugy on 2014/7/3.
 */
public class AllNewsDBInfo implements BaseColumns {

    public static final String SID = "sid";
    public static final String TITLE_SHOW = "title_show";
    public static final String HOMETEXT_SHOW_SHORT = "hometext_show_short";
    public static final String LOGO = "logo";
    public static final String TIME = "time";
    public static final String TYPE = "type";
    public static final String READ = "read";

    public static final SQLiteTable TABLE = new SQLiteTable(AllNewsDataHelper.TABLE_NAME)
            .addColumn(SID, Column.Constraint.UNIQUE, Column.DataType.INTEGER)
            .addColumn(TITLE_SHOW, Column.DataType.TEXT)
            .addColumn(HOMETEXT_SHOW_SHORT, Column.DataType.TEXT)
            .addColumn(LOGO, Column.DataType.TEXT)
            .addColumn(TIME, Column.DataType.INTEGER)
            .addColumn(TYPE, Column.DataType.INTEGER)
            .addColumn(READ, Column.DataType.INTEGER);

}
