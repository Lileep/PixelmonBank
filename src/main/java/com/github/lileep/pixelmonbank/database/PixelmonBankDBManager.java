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
import java.util.List;

public class PixelmonBankDBManager {

    private static PixelmonBankDBManager instance;

    public static PixelmonBankDBManager getInstance() {
        if (instance == null) {
            instance = new PixelmonBankDBManager();
        }
        return instance;
    }

    public int sendOne(String playerUUID, String pokemonUUID, DataPack dataPack) {
        try (Connection connection = PixelmonBank.instance.getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(PixelmonBankQueries.SEND_ONE)
        ) {
            statement.setString(1, playerUUID);
            statement.setString(2, pokemonUUID);
            statement.setBlob(3, dataPack.toStream());
            statement.setString(4, PixelmonBankConfig.SERVER_NAME);
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public DataPack getOne(String playerUUID, String pokemonUUID) {
        DataPack pack = null;
        try (Connection connection = PixelmonBank.instance.getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(PixelmonBankQueries.GET_ONE)
        ) {
            statement.setString(1, playerUUID);
            statement.setString(2, pokemonUUID);
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
             PreparedStatement statement = connection.prepareStatement(PixelmonBankQueries.GET_ALL)
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

    public List<DataPack> getAllPageable(String playerUUID, int pageStart, int pageSize) {
        List<DataPack> packList = new ArrayList<>();
        try (Connection connection = PixelmonBank.instance.getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(PixelmonBankQueries.GET_ALL_PAGEABLE)
        ) {
            statement.setString(1, playerUUID);
            statement.setInt(2, pageStart);
            statement.setInt(3, pageSize);
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

    public int delOne(String playerUUID, String pokemonUUID) {
        try (Connection connection = PixelmonBank.instance.getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(PixelmonBankQueries.DEL_ONE)
        ) {
            statement.setString(1, playerUUID);
            statement.setString(2, pokemonUUID);
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int delAll(String playerUUID){
        try (Connection connection = PixelmonBank.instance.getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(PixelmonBankQueries.DEL_ALL)
        ) {
            statement.setString(1, playerUUID);
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int count(String playerUUID) {
        try (Connection connection = PixelmonBank.instance.getDatabase().getConnection();
             PreparedStatement statement = connection.prepareStatement(PixelmonBankQueries.COUNT)
        ) {
            statement.setString(1, playerUUID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
