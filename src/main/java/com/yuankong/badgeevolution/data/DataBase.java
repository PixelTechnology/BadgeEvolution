package com.yuankong.badgeevolution.data;

import cc.carm.lib.easysql.api.SQLManager;
import com.yuankong.badgeevolution.BadgeEvolution;
import com.yuankong.badgeevolution.config.LoadConfig;
import com.yuankong.badgeevolution.util.CallBack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DataBase {
    public static SQLManager sqlManager;
    public static HashMap<Player,PlayerData> playerDataHash = new HashMap<>();
    public static void createTable(String tableName){
        if(sqlManager == null){
            BadgeEvolution.instance.getLogger().info("未连接数据库！");
            return;
        }
        sqlManager.createTable(tableName)
                .addColumn("uuid", "VARCHAR(40) NOT NULL UNIQUE KEY")
                .addColumn("player_name", "VARCHAR(20) NOT NULL")
                .addColumn("level", "VARCHAR(5) NOT NULL")
                .build().executeAsync((success) -> {
                    //操作成功回调
                    BadgeEvolution.instance.getLogger().info("勋章表加载成功!");
                }, (exception, sqlAction) -> {
                    //操作失败回调
                    BadgeEvolution.instance.getLogger().info("勋章表加载失败，可能数据库连接失败!");
                });
    }

    public static void putCacheData(Player player){
        if(sqlManager == null){
            BadgeEvolution.instance.getLogger().info("未连接数据库！");
            return;
        }

        sqlManager.createQuery()
                .inTable(LoadConfig.getTableName())
                .selectColumns(Column.level.toString())
                .addCondition(Column.uuid.toString(),player.getUniqueId())
                .build().executeAsync((success) -> {
                    //操作成功回调
                    ConcurrentHashMap<String, String> data = new ConcurrentHashMap<>();
                    ResultSet resultSet = success.getResultSet();

                    while (resultSet.next()){
                        data.put(Column.level.toString(),resultSet.getString(Column.level.toString()));
                    }
                    Bukkit.getScheduler().runTask(BadgeEvolution.instance,()->{
                        if(data.containsKey(Column.level.toString())){
                            playerDataHash.put(player,new PlayerData(player,Integer.parseInt(data.get(Column.level.toString()))));

                            BadgeEvolution.instance.getLogger().info("玩家 " + player.getName() + " 数据加载成功!");
                            playerDataHash.get(player).putAttribute();
                        }else{
                            playerDataHash.put(player,new PlayerData(player,0));
                            insertData(player);
                        }

                    });

                }, (exception, sqlAction) -> {
                    //操作失败回调
                    //CustomDesignation.instance.getLogger().info("表加载失败，可能数据库连接失败!");
                    insertData(player);
                });
    }

    public static void insertData(Player player){
        if(sqlManager == null){
            BadgeEvolution.instance.getLogger().info("未连接数据库！");
            return;
        }
        sqlManager.createInsert(LoadConfig.getTableName())
                .setColumnNames(Column.uuid.toString(),Column.player_name.toString(),Column.level.toString())
                .setParams(player.getUniqueId(),player.getName(),"0")
                .executeAsync((success) -> {
                    //操作成功回调
                    putCacheData(player);
                },(exception, sqlAction) -> {
                    //操作失败回调
                    //BadgeEvolution.instance.getLogger().info("数据插入失败!");
                    BadgeEvolution.instance.getLogger().info("玩家 " + player.getName() + " 数据加载失败!");
                });
    }

    public static void updateData(UUID player, Column column, String data){
        updateData(player, column, data, new CallBack() {
            @Override
            public void onSuccess() {}
            @Override
            public void onFail() {}
        });
    }

    public static void updateData(UUID player, Column column, String data, CallBack callBack){
        if(sqlManager == null){
            BadgeEvolution.instance.getLogger().info("未连接数据库！");
            return;
        }
        sqlManager.createUpdate(LoadConfig.getTableName())
                .addCondition(Column.uuid.toString(),player)
                .setColumnValues(column.toString(),data)
                .build().executeAsync((success) -> {
                    //操作成功回调
                    callBack.onSuccess();
                },(exception, sqlAction) -> {
                    //操作失败回调
                    BadgeEvolution.instance.getLogger().info("数据更新失败");
                    callBack.onFail();
                });
    }

    public enum Column{
        uuid,
        player_name,
        level
    }
}
