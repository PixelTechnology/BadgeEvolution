package com.yuankong.badgeevolution.data;

import com.yuankong.badgeevolution.config.LoadConfig;
import com.yuankong.badgeevolution.config.level.Level;
import github.saukiya.sxattribute.SXAttribute;
import github.saukiya.sxattribute.data.attribute.SXAttributeData;
import org.bukkit.entity.Player;

public class PlayerData {
    private Player player;
    private int levelIndexOf;
    private Level level;

    public PlayerData(Player player, int levelIndexOf) {
        this.player = player;
        this.levelIndexOf = levelIndexOf;

        level = LoadConfig.getLevelList().get(levelIndexOf);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getLevelIndexOf() {
        return levelIndexOf;
    }

    public void setLevelIndexOf(int levelIndexOf) {
        int old = this.levelIndexOf;
        this.levelIndexOf = levelIndexOf;
        level = LoadConfig.getLevelList().get(levelIndexOf);

        if(old == levelIndexOf){
            return;
        }
        DataBase.updateData(player.getUniqueId(), DataBase.Column.level, String.valueOf(levelIndexOf));
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        if(level == null){
            return;
        }
        this.level = level;

        int old = this.levelIndexOf;
        levelIndexOf = LoadConfig.getLevelList().indexOf(level);
        if(old == levelIndexOf){
            return;
        }
        DataBase.updateData(player.getUniqueId(), DataBase.Column.level, String.valueOf(levelIndexOf));
    }

    public void putAttribute(){
        SXAttributeData sxAttributeData = new SXAttributeData();
        if(!level.getAttribute().isEmpty()){
            sxAttributeData.add(SXAttribute.getApi().getLoreData(null,null,level.getAttribute()));
        }
        SXAttribute.getApi().setEntityAPIData(this.getClass(), player.getUniqueId(), sxAttributeData);
        SXAttribute.getApi().updateStats(player);
    }
}
