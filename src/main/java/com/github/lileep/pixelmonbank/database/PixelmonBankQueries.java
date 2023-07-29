package com.github.lileep.pixelmonbank.database;

public class PixelmonBankQueries {

    public static final String CREATE_DB = "create database if not exists %s;";

    //visible: 0: can be seen, 1: cannot be seen
    public static final String CREATE_PBK_TABLE = "create table if not exists `%s`.`pixelmon_bank`(" +
            "id             bigint          primary key auto_increment, " +
            "player_uuid    varchar(64)     not null, " +
            "pixelmon_data  blob            not null, " +
            "server_name    varchar(32), " +
            "send_time      timestamp       not null    default     current_timestamp, " +
            "withdraw_time  timestamp       default     null, " +
            "index(player_uuid)" +
            ")Engine=InnoDB default charset=utf8mb4;";

    public static final String CREATE_PLAYER_TABLE = "create table if not exists `%s`.`pbk_player_info`(" +
            "player_uuid    varchar(64)     not null, " +
            "total          int             not null    default     0, " +
            "restrict_count int             not null    default     0, " +
            "index(player_uuid)" +
            ")Engine=InnoDB default charset=utf8mb4;";

    public static final String CHECK_COL_ORDER = "show columns from `%s`.`pixelmon_bank`";

    public static final String CHECK_COL_TYPE = CHECK_COL_ORDER + " where Field='pixelmon_uuid'";

    public static final String REMOVE_P_UUID = "ALTER TABLE `%s`.`pixelmon_bank` DROP COLUMN pixelmon_uuid";
    public static final String ADD_ID = "ALTER TABLE `%s`.`pixelmon_bank` ADD COLUMN id bigint AUTO_INCREMENT PRIMARY KEY FIRST";

    public static final String RENAME_TIME_POINT = "alter table `%s`.`pixelmon_bank` rename column `time_point` to `send_time`";
    public static final String REMOVE_OLD_DATA = "delete from `%s`.`pixelmon_bank` where visible=1";
    public static final String REMOVE_VISIBLE = "ALTER TABLE `%s`.`pixelmon_bank` DROP COLUMN visible";
    public static final String ADD_WITHDRAW_TIME = "ALTER TABLE `%s`.`pixelmon_bank` ADD COLUMN withdraw_time timestamp DEFAULT NULL";

    public static final String REMOVE_TIME_POINT_INDEX = "ALTER TABLE `%s`.`pixelmon_bank` DROP INDEX time_point";



    public static final String CHANGE_COL = "alter table pixelmon_bank modify column player_uuid varchar(64) after pixelmon_uuid";

    public static final String CHANGE_NAME_TYPE = "ALTER TABLE pixelmon_bank CHANGE pixelmon_uuid id BIGINT AUTO_INCREMENT, ADD PRIMARY KEY (id)";
    public static final String CHANGE_TYPE_OLD = "ALTER TABLE pixelmon_bank MODIFY COLUMN pixelmon_uuid uuid";

    public static final String REGEN_UUID = "UPDATE pixelmon_bank SET pixelmon_uuid = UUID()";

    public static final String CHANGE_COL_NAME = "ALTER TABLE pixelmon_bank CHANGE COLUMN pixelmon_uuid uuid uuid";


    public static final String SEND_ONE = "insert into `%s`.`pixelmon_bank` " +
            "(player_uuid, pixelmon_data, server_name) " +
            "values (?, ?, ?)";

    public static final String GET_ONE = "select pixelmon_data from `%s`.`pixelmon_bank` where " +
            "id = ? and player_uuid = ? and withdraw_time is null";

    public static final String GET_ALL = "select pixelmon_data from `%s`.`pixelmon_bank` where " +
            "player_uuid = ? and withdraw_time is null";

    //Use inner join to improve the efficiency
    public static final String GET_ALL_PAGEABLE = "select pixelmon_data from `%s`.`pixelmon_bank` as t1 join (" +
            "select id from `%s`.`pixelmon_bank` where player_uuid = ? and withdraw_time is null limit ?, ?" +
            ") as t2 on t1.uuid = t2.uuid and t1.withdraw_time is null";

    //Not hard delete
    public static final String DEL_ONE = "update `%s`.`pixelmon_bank` set withdraw_time = now() where " +
            "id = ? and player_uuid = ?";

    public static final String DEL_ALL = "update `%s`.`pixelmon_bank` set withdraw_time = now() where " +
            "player_uuid = ? and withdraw_time is null";

    public static final String INIT_PLAYER_INFO = "insert into `%s`.`pbk_player_info` " +
            "(player_uuid) " +
            "values (?)";
    public static final String RESET_PLAYER_INFO = "update `%s`.`pbk_player_info` set total = 0 and restrict_count = 0 where " +
            "player_uuid = ?";
    public static final String SELECT_PLAYER_INFO = "select ? from `%s`.`pbk_player_info` where " +
            "player_uuid = ?";
    public static final String UPDATE_PLAYER_INFO = "update `%s`.`pbk_player_info` set ? = ? + ? where " +
            "player_uuid = ?";

}
