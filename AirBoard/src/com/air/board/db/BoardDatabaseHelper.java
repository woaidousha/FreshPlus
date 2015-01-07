package com.air.board.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.air.board.BoardApplication;
import com.air.lib.communication.utils.LogTag;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class BoardDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = "BoardDatabaseHelper";

    private static final String DATABASE_NAME = "air_board.db";
    private static final int VERSION = 1;

    private static BoardDatabaseHelper sInstance;

    private Dao<TableDeviceData, Long> mDeviceDataDao;

    public BoardDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public static BoardDatabaseHelper getInstance() {
        if (sInstance == null) {
            sInstance = new BoardDatabaseHelper(BoardApplication.getApplication());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {

        try {
            TableUtils.createTableIfNotExists(connectionSource, TableDeviceData.class);
        } catch (SQLException e) {
            e.printStackTrace();
            LogTag.e(TAG, "create table error : ", e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
    }

    public Dao<TableDeviceData, Long> getDeviceDataDao() {
        if (mDeviceDataDao == null) {
            try {
                mDeviceDataDao = getDao(TableDeviceData.class);
            } catch (SQLException e) {
                LogTag.e(TAG, "get device data dao error:", e);
            }
        }
        return mDeviceDataDao;
    }

    @Override
    public void close() {
        super.close();
        mDeviceDataDao = null;
    }
}
