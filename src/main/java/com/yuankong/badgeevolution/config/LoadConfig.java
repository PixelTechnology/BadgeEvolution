package com.yuankong.badgeevolution.config;

import com.yuankong.badgeevolution.BadgeEvolution;
import com.yuankong.badgeevolution.config.level.Level;
import com.yuankong.badgeevolution.config.material.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadConfig {

    private static String noLevel;
    private static String tableName;
    private static String noNeedMaterial;
    private static String noNeedMoney;
    private static String noNeedPoints;
    private static String success;
    private static String info;
    private static String guiName;
    private static List<Material> materialList;
    private static HashMap<String,Material> materialHash;
    private static List<Level> levelList;
    public static void load() {
        Configuration config = BadgeEvolution.instance.getConfig();

        tableName = config.getString("tableName","badge_evolution");
        noLevel = config.getString("lang.noLevel");
        noNeedMaterial = config.getString("lang.noNeedMaterial");
        noNeedMoney = config.getString("lang.noNeedMoney");
        noNeedPoints = config.getString("lang.noNeedPoints");
        success = config.getString("lang.success");
        info = config.getString("lang.info");
        guiName = config.getString("龙核界面文件名");

        materialList = new ArrayList<>();
        materialHash = new HashMap<>();
        ConfigurationSection cs = config.getConfigurationSection("材料配置");
        for(String key:cs.getKeys(false)){
            materialList.add(new Material(key,cs.getString(key+".name"),cs.getStringList(key+".lore"),cs.getString(key+".type")));
            materialHash.put(key,new Material(key,cs.getString(key+".name"),cs.getStringList(key+".lore"),cs.getString(key+".type")));
        }

        levelList = new ArrayList<>();
        ConfigurationSection level = config.getConfigurationSection("勋章配置");
        for(String key:level.getKeys(false)){
            levelList.add(new Level(level,key));
        }
    }
    public static void reload(){
        BadgeEvolution.instance.reloadConfig();
        load();
    }

    public static String getTableName() {
        return tableName;
    }

    public static String getNoLevel() {
        return noLevel;
    }

    public static String getNoNeedMaterial() {
        return noNeedMaterial;
    }

    public static String getNoNeedMoney() {
        return noNeedMoney;
    }

    public static String getNoNeedPoints() {
        return noNeedPoints;
    }

    public static String getSuccess() {
        return success;
    }

    public static String getInfo() {
        return info;
    }

    public static String getGuiName() {
        return guiName;
    }

    public static List<Material> getMaterialList() {
        return materialList;
    }

    public static HashMap<String, Material> getMaterialHash() {
        return materialHash;
    }

    public static List<Level> getLevelList() {
        return levelList;
    }
}
