package com.github.lileep.pixelmonbank.database;

import com.github.lileep.pixelmonbank.PixelmonBank;
import com.github.lileep.pixelmonbank.config.PixelmonBankConfig;
import com.github.lileep.pixelmonbank.data.DataPack;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PixelmonBankDBManager {

    private static PixelmonBankDBManager instance;

    public static PixelmonBankDBManager getInstance() {
        if (instance == null) {
            instance = new PixelmonBankDBManager();
        }
        return instance;
    }

    public int sendOne(String playerUUID, DataPack dataPack) {
        try (Connection connection = PixelmonBank.instance.getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(String.format(PixelmonBankQueries.SEND_ONE, PixelmonBankConfig.DB_DBNAME))
        ) {
            statement.setString(1, playerUUID);
            statement.setBlob(2, dataPack.toStream());
            statement.setString(3, PixelmonBankConfig.SERVER_NAME);
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public DataPack getOne(int id, String playerUUID) {
        DataPack pack = null;
        try (Connection connection = PixelmonBank.instance.getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(String.format(PixelmonBankQueries.GET_ONE, PixelmonBankConfig.DB_DBNAME))
        ) {
            statement.setInt(1, id);
            statement.setString(2, playerUUID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                InputStream stream = resultSet.getBlob(1).getBinaryStream();
                if (stream != null) {
                    pack = DataPack.toDataPack(stream);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pack;
    }

    public List<DataPack> getAll(String playerUUID) {
        List<DataPack> packList = new ArrayList<>();
        try (Connection connection = PixelmonBank.instance.getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(String.format(PixelmonBankQueries.GET_ALL, PixelmonBankConfig.DB_DBNAME))
        ) {
            statement.setString(1, playerUUID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                InputStream stream = resultSet.getBlob(1).getBinaryStream();
                if (stream != null) {
                    packList.add(DataPack.toDataPack(stream));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return packList;
    }


    public Map<Integer, DataPack> getAllPageable(String playerUUID, int pageStart, int pageSize) {
//        List<DataPack> packList = new ArrayList<>();
        Map<Integer, DataPack> packMap = new LinkedHashMap<>();
        try (Connection connection = PixelmonBank.instance.getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(String.format(PixelmonBankQueries.GET_ALL_PAGEABLE, PixelmonBankConfig.DB_DBNAME, PixelmonBankConfig.DB_DBNAME))
        ) {
            statement.setString(1, playerUUID);
            statement.setInt(2, pageStart);
            statement.setInt(3, pageSize);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                InputStream stream = resultSet.getBlob(2).getBinaryStream();
                if (stream != null) {
                    packMap.put(resultSet.getInt(1), DataPack.toDataPack(stream));
//                    packList.add(DataPack.toDataPack(stream));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return packMap;
    }

    public int delOne(int id, String playerUUID) {
        try (Connection connection = PixelmonBank.instance.getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(String.format(PixelmonBankQueries.DEL_ONE, PixelmonBankConfig.DB_DBNAME))
        ) {
            statement.setInt(1, id);
            statement.setString(2, playerUUID);
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int delAll(String playerUUID) {
        try (Connection connection = PixelmonBank.instance.getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(String.format(PixelmonBankQueries.DEL_ALL, PixelmonBankConfig.DB_DBNAME))
        ) {
            statement.setString(1, playerUUID);
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int resetPlayerInfo(String playerUUID) {
        try (Connection connection = PixelmonBank.instance.getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(String.format(PixelmonBankQueries.RESET_PLAYER_INFO, PixelmonBankConfig.DB_DBNAME))
        ) {
            statement.setString(1, playerUUID);
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getPlayerInfo(String info, String playerUUID) {
        try (Connection connection = PixelmonBank.instance.getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(String.format(PixelmonBankQueries.SELECT_PLAYER_INFO, PixelmonBankConfig.DB_DBNAME))
        ) {
            statement.setString(1, info);
            statement.setString(2, playerUUID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int updatePlayerInfo(String info, int amount, String playerUUID) {
        try (Connection connection = PixelmonBank.instance.getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(String.format(PixelmonBankQueries.UPDATE_PLAYER_INFO, PixelmonBankConfig.DB_DBNAME))
        ) {
            statement.setString(1, info);
            statement.setString(2, info);
            statement.setInt(3, amount);
            statement.setString(4, playerUUID);
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
