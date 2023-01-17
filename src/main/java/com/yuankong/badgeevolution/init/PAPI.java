package com.yuankong.badgeevolution.init;

import com.yuankong.badgeevolution.config.LoadConfig;
import com.yuankong.badgeevolution.data.DataBase;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PAPI extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "badge";
    }

    @Override
    public String getAuthor() {
        return "yuankong";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equals("texture1")){//%badge_texture1%
            Player player1 = player.getPlayer();
            return DataBase.playerDataHash.get(player1).getLevel().getTexture();
        }

        if(params.equals("texture2")){//%badge_texture2%
            Player player1 = player.getPlayer();
            if(DataBase.playerDataHash.get(player1).getLevelIndexOf()+1 >= LoadConfig.getLevelList().size()){
                return "";
            }
            return LoadConfig.getLevelList().get(DataBase.playerDataHash.get(player1).getLevelIndexOf()+1).getTexture();
        }

        if(params.equals("level")){//%badge_level%
            Player player1 = player.getPlayer();
            return DataBase.playerDataHash.get(player1).getLevel().getColorName();
        }

        if(params.equals("lore_now")){//%badge_lore_now%
            Player player1 = player.getPlayer();
            StringBuilder lore = new StringBuilder();
            for(String str:DataBase.playerDataHash.get(player1).getLevel().getLore()){
                lore.append(str).append("\n");
            }
            return lore.toString();
        }

        if(params.equals("lore_new")){//%badge_lore_new%
            Player player1 = player.getPlayer();
            if(DataBase.playerDataHash.get(player1).getLevelIndexOf()+1 >= LoadConfig.getLevelList().size()){
                return "";
            }
            StringBuilder lore = new StringBuilder();
            for(String str:LoadConfig.getLevelList().get(DataBase.playerDataHash.get(player1).getLevelIndexOf()+1).getLore()){
                lore.append(str).append("\n");
            }
            return lore.toString();
        }


        if(params.equals("display_now")){//%badge_display_now%
            Player player1 = player.getPlayer();
            StringBuilder display = new StringBuilder();
            for(String str:DataBase.playerDataHash.get(player1).getLevel().getDisplayLore()){
                display.append(str).append("\n");
            }
            return display.toString();
        }


        if(params.equals("display_new")){//%display_lore_new%
            Player player1 = player.getPlayer();
            if(DataBase.playerDataHash.get(player1).getLevelIndexOf()+1 >= LoadConfig.getLevelList().size()){
                return "";
            }
            StringBuilder display = new StringBuilder();
            for(String str:LoadConfig.getLevelList().get(DataBase.playerDataHash.get(player1).getLevelIndexOf()+1).getAttribute()){
                display.append(str).append("\n");
            }
            return display.toString();
        }

        if(params.equals("material")){//%badge_material%
            Player player1 = player.getPlayer();
            if(DataBase.playerDataHash.get(player1).getLevelIndexOf()+1 >= LoadConfig.getLevelList().size()){
                return "";
            }
            StringBuilder material = new StringBuilder();
            for(String str:LoadConfig.getLevelList().get(DataBase.playerDataHash.get(player1).getLevelIndexOf()+1).getColorMaterial()){
                material.append(str).append("\n");
            }
            return material.toString();
        }

        if(params.equals("points")){//%badge_points%
            Player player1 = player.getPlayer();
            if(DataBase.playerDataHash.get(player1).getLevelIndexOf()+1 >= LoadConfig.getLevelList().size()){
                return "";
            }
            return String.valueOf(LoadConfig.getLevelList().get(DataBase.playerDataHash.get(player1).getLevelIndexOf()+1).getIfPoints());
        }

        return null;
    }
}
