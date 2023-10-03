package com.github.lileep.pixelmonbank.database;

public class PixelmonBankQueries {


    public static final String CREATE_DB = "create database if not exists %s;";

    public static final String CREATE_PBK_TABLE = "create table if not exists `%s`.`pixelmon_bank`(" +
            "id             bigint          primary key auto_increment, " +
            "player_uuid    varchar(64)     not null, " +
            "pixelmon_data  blob            not null, " +
            "server_name    varchar(32), " +
            "send_time      timestamp       not null    default     current_timestamp, " +
            "withdraw_time  timestamp       default     null, " +
            "pixelmon_name  varchar(16)     default     null, " +
            "index(player_uuid)," +
            "index(pixelmon_name)" +
            ")Engine=InnoDB default charset=utf8mb4;";

    public static final String CHECK_COL_ORDER = "show columns from `%s`.`pixelmon_bank`";

    public static final String REMOVE_P_UUID = "ALTER TABLE `%s`.`pixelmon_bank` DROP COLUMN pixelmon_uuid";
    public static final String ADD_ID = "ALTER TABLE `%s`.`pixelmon_bank` ADD COLUMN id bigint AUTO_INCREMENT PRIMARY KEY FIRST";

    public static final String RENAME_TIME_POINT = "alter table `%s`.`pixelmon_bank` rename column `time_point` to `send_time`";
    public static final String REMOVE_TIME_POINT_INDEX = "ALTER TABLE `%s`.`pixelmon_bank` DROP INDEX time_point";
    public static final String REMOVE_OLD_DATA = "delete from `%s`.`pixelmon_bank` where visible=1";
    public static final String REMOVE_VISIBLE = "ALTER TABLE `%s`.`pixelmon_bank` DROP COLUMN visible";
    public static final String ADD_WITHDRAW_TIME = "ALTER TABLE `%s`.`pixelmon_bank` ADD COLUMN withdraw_time timestamp DEFAULT NULL";
    public static final String ADD_PIXELMON_NAME = "ALTER TABLE `%s`.`pixelmon_bank` ADD COLUMN pixelmon_name varchar(16) DEFAULT NULL";
    public static final String ADD_PIXELMON_NAME_INDEX = "ALTER TABLE `%s`.`pixelmon_bank` ADD INDEX (pixelmon_name)";



    public static final String SEND_ONE = "insert into `%s`.`pixelmon_bank` " +
            "(player_uuid, pixelmon_data, server_name, pixelmon_name) " +
            "values (?, ?, ?, ?)";

    public static final String GET_ONE = "select pixelmon_data from `%s`.`pixelmon_bank` where " +
            "id = ? and player_uuid = ? and withdraw_time is null";

    public static final String GET_ALL = "select pixelmon_data from `%s`.`pixelmon_bank` where " +
            "player_uuid = ? and withdraw_time is null";

    //Use inner join to improve the efficiency
    public static final String GET_ALL_PAGEABLE = "SELECT id, pixelmon_data FROM `%s`.`pixelmon_bank` WHERE " +
            "player_uuid = ? AND withdraw_time IS NULL LIMIT ?, ?";

    //Not hard delete
    public static final String DEL_ONE = "update `%s`.`pixelmon_bank` set withdraw_time = now() where " +
            "id = ? and player_uuid = ? and withdraw_time is null";

    public static final String DEL_ALL = "update `%s`.`pixelmon_bank` set withdraw_time = now() where " +
            "player_uuid = ? and withdraw_time is null";

    public static final String GET_TOTAL = "select count(*) from `%s`.`pixelmon_bank` where " +
            "player_uuid = ? and withdraw_time is null";
    public static final String GET_POKEMON_NAMES = "select pixelmon_name from `%s`.`pixelmon_bank` where " +
            "player_uuid = ? and withdraw_time is null";
}
