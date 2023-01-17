package com.yuankong.badgeevolution.config.level;

import com.yuankong.badgeevolution.config.LoadConfig;
import com.yuankong.badgeevolution.config.material.Material;
import com.yuankong.badgeevolution.util.Util;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Level {
    private final String levelKey;
    private final String name;
    private final String texture;
    private final List<String> lore;
    private final List<String> material;
    private final int ifPoints;
    private final boolean commandEnable;
    private final List<String> command;
    private final List<String> attribute;
    private final List<String> displayLore;
    private double money = 0;
    private final HashMap<String,Integer> materialAmount = new HashMap<>();
    private final List<String> materialName = new ArrayList<>();
    private final List<Material> materials = new ArrayList<>();

    public Level(ConfigurationSection config,String key) {
        this.levelKey = key;
        this.name = config.getString(key+".name");
        this.texture = config.getString(key+".texture");
        this.lore = config.getStringList(key+".lore");
        this.material = config.getStringList(key+".material");
        this.ifPoints = config.getInt(key+".ifPoints");
        this.commandEnable = config.getBoolean(key+".commandEnable");
        this.command = config.getStringList(key+".command");
        this.attribute = config.getStringList(key+".attribute");
        this.displayLore = config.getStringList(key+".displayLore");

        if(!material.isEmpty()){
            for(String str:material){
                str = Util.removeColor(str);
                String[] str2 = str.split("\\*");
                if(str2[0].contains("金币") || str2[0].contains("money")){
                    money = new BigDecimal(str2[1]).doubleValue();
                    continue;
                }
                materialAmount.put(str2[0],Integer.parseInt(str2[1]));
                materialName.add(str2[0]);
                for(Material m: LoadConfig.getMaterialList()){
                    if(m.getKey().equals(str2[0])){
                        materials.add(m);
                    }
                }
            }
        }


    }

    public String getLevelKey() {
        return levelKey;
    }

    public String getName() {
        return Util.removeColor(name);
    }
    public String getColorName() {
        return name;
    }
    public String getTexture() {
        return texture;
    }

    public List<String> getLore() {
        return lore;
    }

    public List<String> getColorMaterial() {
        return material;
    }

    //无颜色，无金币，无数量的单纯材料名
    public List<String> getMaterialName() {
        return materialName;
    }

    public int getIfPoints() {
        return ifPoints;
    }

    public boolean isCommandEnable() {
        return commandEnable;
    }

    public List<String> getCommand(Player player) {
        List<String> list = new ArrayList<>(command);
        list.replaceAll(s -> s.replaceAll("%player%",player.getName()));
        return command;
    }

    public List<String> getAttribute() {
        return attribute;
    }

    public List<String> getDisplayLore() {
        return displayLore;
    }

    public double getMoney() {
        return money;
    }

    public HashMap<String, Integer> getMaterialAmount() {
        return materialAmount;
    }

    public List<Material> getMaterials() {
        return materials;
    }
}
