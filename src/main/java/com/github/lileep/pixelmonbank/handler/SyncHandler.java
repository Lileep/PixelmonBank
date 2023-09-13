package com.github.lileep.pixelmonbank.handler;


import com.github.lileep.pixelmonbank.data.DataPack;
import com.github.lileep.pixelmonbank.data.ISerializer;
import com.github.lileep.pixelmonbank.database.PixelmonBankDBManager;
import com.github.lileep.pixelmonbank.lib.Reference;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import java.util.*;

public class SyncHandler {

    private static SyncHandler instance;

    private final Map<String, ISerializer> serializers = new HashMap<>();

    public static SyncHandler getInstance() {
        if (instance == null) {
            instance = new SyncHandler();
        }
        return instance;
    }

    public void register(ISerializer serializer) {
        serializers.put(serializer.getUniqueName(), serializer);
    }

    public void unregister(ISerializer serializer) {
        serializers.remove(serializer.getUniqueName());
    }

    public boolean sendOne(String playerUUID, Pokemon pokemon) {

        //Prepare data
        DataPack dataPack = new DataPack();

        ISerializer serializer = serializers.get(Reference.PIXELMON_SERIALIZER);
        dataPack.putData(serializer.getUniqueName(), serializer.serialize(pokemon));

        //Access db
        return PixelmonBankDBManager.getInstance().sendOne(
                playerUUID,
                dataPack
        ) > 0;
    }

    public Pokemon getOne(int id, String playerUUID) {

        //Access db & get data
        DataPack dataPack = PixelmonBankDBManager.getInstance().getOne(id, playerUUID);

        //Deal with NPE of dataPack
        if (Optional.ofNullable(dataPack).isPresent()) {
            return serializers.get(Reference.PIXELMON_SERIALIZER).deserialize(dataPack).get(0);
        }
        return null;

    }

    public List<Pokemon> getAll(String playerUUID) {
        List<DataPack> dataPackList = PixelmonBankDBManager.getInstance().getAll(playerUUID);
        //Not necessary to judge null since db will return a new list
        List<Pokemon> pokemonList = new ArrayList<>();
        //If the list has no elements, for-each block will not be executed
        dataPackList.forEach(dataPack -> pokemonList.add(serializers.get(Reference.PIXELMON_SERIALIZER).deserialize(dataPack).get(0)));
        return pokemonList;
    }

    public Map<Integer, Pokemon> getAllPageable(String playerUUID, int pageNum, int pageSize) {
        Map<Integer, DataPack> dataPackMap = PixelmonBankDBManager.getInstance().getAllPageable(playerUUID, (pageNum - 1) * pageSize, pageSize);
        //Not necessary to judge null since db will return a new list
//        List<Pokemon> pokemonList = new ArrayList<>();
        Map<Integer, Pokemon> pokemonMap = new LinkedHashMap<>();
        //If the list has no elements, for-each block will not be executed
        dataPackMap.forEach((id ,dataPack) -> pokemonMap.put(id, serializers.get(Reference.PIXELMON_SERIALIZER).deserialize(dataPack).get(0)));
        return pokemonMap;
    }

    public boolean delOne(int id, String playerUUID) {
        return PixelmonBankDBManager.getInstance().delOne(id, playerUUID) > 0;
    }

    public boolean delAll(String playerUUID) {
        return PixelmonBankDBManager.getInstance().delAll(playerUUID) > 0;
    }

    public boolean resetPlayerInfo(String playerUUID) {
        return PixelmonBankDBManager.getInstance().resetPlayerInfo(playerUUID) > 0;
    }

    public int getTotal(String playerUUID) {
        return PixelmonBankDBManager.getInstance().getPlayerInfo("total", playerUUID);
    }
    public int getRestrictCount(String playerUUID) {
        return PixelmonBankDBManager.getInstance().getPlayerInfo("restrict_count", playerUUID);
    }

    public boolean updateTotal(int amount, String playerUUID) {
        return PixelmonBankDBManager.getInstance().updatePlayerInfo("total", amount, playerUUID) > 0;
    }
    public boolean updateRestrictCount(int amount, String playerUUID) {
        return PixelmonBankDBManager.getInstance().updatePlayerInfo("restrict_count", amount, playerUUID) > 0;
    }
}
