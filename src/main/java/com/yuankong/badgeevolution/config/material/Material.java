package com.yuankong.badgeevolution.config.material;

import java.util.List;

public class Material {
    private final String key;
    private final String name;
    private final List<String> lore;
    private final String type;

    public Material(String key, String name, List<String> lore, String type) {
        this.key = key;
        this.name = name;
        this.lore = lore;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public String[] getType() {
        return type.split(":");
    }
}
