package com.github.lileep.pixelmonbank.database;

public class PixelmonBankQueries {

    //visible: 0: can be seen, 1: cannot be seen
    public static final String CREATE_TABLE = "create table if not exists `pixelmon_bank`(" +
            "player_uuid    varchar(64)     not null, " +
            "pixelmon_uuid  varchar(64)     not null, " +
            "pixelmon_data  BLOB            not null, " +
            "server_name    varchar(32), " +
            "time_point     timestamp       not null    default     current_timestamp, " +
            "visible        tinyint         not null    default     0," +
            "index(player_uuid)," +
            "unique(pixelmon_uuid)," +
            "index(pixelmon_data)," +
            "index(time_point)," +
            "index(visible)" +
            ")Engine=InnoDB default charset=utf8mb4;";

    public static final String SEND_ONE = "insert into `pixelmon_bank` " +
            "(player_uuid, pixelmon_uuid, pixelmon_data, server_name, time_point, visible) " +
            "values (?, ?, ?, ?, NOW(), default)";

    public static final String GET_ONE = "select pixelmon_data from `pixelmon_bank` where " +
            "player_uuid = ? and pixelmon_uuid = ? and visible = 0";

    public static final String GET_ALL = "select pixelmon_data from `pixelmon_bank` where " +
            "player_uuid = ? and visible = 0";

    //Use inner join to improve the efficiency
    public static final String GET_ALL_PAGEABLE = "select pixelmon_data from `pixelmon_bank` as t1 join (" +
            "select pixelmon_uuid from pixelmon_bank where player_uuid = ? and visible = 0 limit ?, ?" +
            ") as t2 on t1.pixelmon_uuid = t2.pixelmon_uuid and t1.visible = 0";

    //Not hard delete
    public static final String DEL_ONE = "update `pixelmon_bank` set visible = 1 where " +
            "player_uuid = ? and pixelmon_uuid = ?";

    public static final String DEL_ALL = "update `pixelmon_bank` set visible = 1 where " +
            "player_uuid = ? and visible = 0";
}
