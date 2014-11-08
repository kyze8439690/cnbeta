package me.yugy.cnbeta.dao.dbinfo;

import android.provider.BaseColumns;

import me.yugy.cnbeta.dao.datahelper.RealTimeNewsDataHelper;
import me.yugy.cnbeta.utils.database.Column;
import me.yugy.cnbeta.utils.database.SQLiteTable;

/**
 * Created by yugy on 2014/7/3.
 */
public class RealTimeNewsDBInfo implements BaseColumns {

    public static final String SID = "sid";
    public static final String TITLE = "subject";
    public static final String HOMETEXT_SHOW = "hometext_show";
    public static final String LOGO = "logo";
    public static final String TIME = "time";
    public static final String TYPE = "type";
    public static final String READ = "read";

    public static final SQLiteTable TABLE = new SQLiteTable(RealTimeNewsDataHelper.TABLE_NAME)
            .addColumn(SID, Column.Constraint.UNIQUE, Column.DataType.INTEGER)
            .addColumn(TITLE, Column.DataType.TEXT)
            .addColumn(HOMETEXT_SHOW, Column.DataType.TEXT)
            .addColumn(LOGO, Column.DataType.TEXT)
            .addColumn(TIME, Column.DataType.INTEGER)
            .addColumn(TYPE, Column.DataType.INTEGER)
            .addColumn(READ, Column.DataType.INTEGER);

}
