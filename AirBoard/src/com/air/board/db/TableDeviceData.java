package com.air.board.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "device_data")
public class TableDeviceData {

    public static final String TABLE_NAME = "device_data";

    public static final int TYPE_DEVICE_INFO = 0;
    public static final int TYPE_DEVICE_DATA = 1;
    public static final int TYPE_FILTER_DATA = 2;
    public static final int TYPE_MOTOR_DATA = 3;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_UPLOAD = "upload";

    @DatabaseField(generatedId = true, useGetSet = true, columnName = COLUMN_ID)
    private Long id;
    @DatabaseField(useGetSet = true, dataType = DataType.LONG, columnName = COLUMN_TIME)
    private long time;
    @DatabaseField(useGetSet = true, dataType = DataType.STRING, columnName = COLUMN_DATA)
    private String data;
    @DatabaseField(useGetSet = true, dataType = DataType.INTEGER, columnName = COLUMN_TYPE)
    private int type;
    @DatabaseField(useGetSet = true, dataType = DataType.BOOLEAN, columnName = COLUMN_UPLOAD)
    private boolean upload;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean getUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }
}
