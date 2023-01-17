package com.yuankong.badgeevolution.listener;

import com.yuankong.badgeevolution.BadgeEvolution;
import com.yuankong.badgeevolution.config.LoadConfig;
import com.yuankong.badgeevolution.data.DataBase;
import com.yuankong.easylib.event.ReloadConfigEvent;
import com.yuankong.easylib.event.SQLManagerFinishEvent;
import org.bukkit.event.Listener;

public class EventHandler2 implements Listener {
    @org.bukkit.event.EventHandler
    public void onEasyLibEvent(SQLManagerFinishEvent event) {
        DataBase.sqlManager = event.getSqlManager();
        DataBase.createTable(LoadConfig.getTableName());
        BadgeEvolution.instance.getLogger().info("初始化完成!");
    }

    @org.bukkit.event.EventHandler
    public void onEasyLibEvent2(ReloadConfigEvent event) {
        DataBase.sqlManager = event.getSqlManager();
    }
}
