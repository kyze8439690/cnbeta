package me.yugy.cnbeta.dao.dbinfo;

import me.yugy.app.common.database.Column;
import me.yugy.app.common.database.SQLiteTable;
import me.yugy.cnbeta.dao.datahelper.NewsContentDataHelper;

/**
 * Created by gzyanghui on 2014/9/5.
 */
public class NewsContentDBInfo {

    public static final String SID = "sid";
    public static final String STRINGS = "strings";
    public static final String INTRO = "intro";
    public static final String SN = "sn";
    public static final String IMAGES = "images";

    public static final SQLiteTable TABLE = new SQLiteTable(NewsContentDataHelper.TABLE_NAME)
            .addColumn(SID, Column.DataType.INTEGER)
            .addColumn(STRINGS, Column.DataType.TEXT)
            .addColumn(INTRO, Column.DataType.TEXT)
            .addColumn(IMAGES, Column.DataType.TEXT)
            .addColumn(SN, Column.DataType.TEXT);

}
