package me.yugy.cnbeta.dao.dbinfo;

import me.yugy.app.common.database.Column;
import me.yugy.app.common.database.SQLiteTable;
import me.yugy.cnbeta.dao.datahelper.HotCommentsDataHelper;

/**
 * Created by yugy on 2014/9/8.
 */
public class HotCommentsDBInfo {

    public static final String SID = "sid";
    public static final String COMMENT = "comment";
    public static final String AUTHOR = "username";
    public static final String TITLE = "subject";
    public static final String CID = "cid";


    public static final SQLiteTable TABLE = new SQLiteTable(HotCommentsDataHelper.TABLE_NAME)
            .addColumn(SID, Column.DataType.INTEGER)
            .addColumn(COMMENT, Column.DataType.TEXT)
            .addColumn(AUTHOR, Column.DataType.TEXT)
            .addColumn(TITLE, Column.DataType.TEXT)
            .addColumn(CID, Column.DataType.INTEGER);
}
