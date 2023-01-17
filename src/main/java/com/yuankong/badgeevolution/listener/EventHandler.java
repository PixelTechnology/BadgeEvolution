package com.yuankong.badgeevolution.listener;

import com.yuankong.badgeevolution.data.DataBase;
import com.yuankong.badgeevolution.init.Initiate;
import eos.moe.dragoncore.api.gui.event.CustomPacketEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventHandler implements Listener {
    @org.bukkit.event.EventHandler
    public void onEvent(CustomPacketEvent event) {
        if(!event.getIdentifier().equals("badge")){
            return;
        }
        if("evolution_points".equals(event.getData().get(0))){
            Initiate.pointsEvolution(event.getPlayer());
        }
        if("evolution_material".equals(event.getData().get(0))){
            Initiate.materialEvolution(event.getPlayer());
        }
    }

    @org.bukkit.event.EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        DataBase.putCacheData(event.getPlayer());

    }

    @org.bukkit.event.EventHandler
    public void onLeaveEvent(PlayerQuitEvent event) {
        DataBase.playerDataHash.remove(event.getPlayer());
    }
}
