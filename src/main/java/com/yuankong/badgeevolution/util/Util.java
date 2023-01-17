package com.yuankong.badgeevolution.util;

import com.yuankong.badgeevolution.config.material.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Util {
    public static String removeColor(String str) {
        return str.replaceAll("[&§]+[a-z0-9]", "");
    }

    public static boolean compareLore(List<String> lore, List<String> config){
        int a = 0;
        for(String loreConfig : config){
            if(lore.contains(loreConfig )){
                a = a+1;
            }else{
                break;
            }
        }
        return a == config.size();
    }

    public static boolean compareType(ItemStack itemStack, Material material){
        if(itemStack.getType().getId() == Integer.parseInt(material.getType()[0])){
            return itemStack.getData().getData() == (byte) Integer.parseInt(material.getType()[1]);
        }
        return false;
    }
}
