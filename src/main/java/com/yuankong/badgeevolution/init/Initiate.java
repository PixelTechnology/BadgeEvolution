package com.yuankong.badgeevolution.init;

import com.yuankong.badgeevolution.BadgeEvolution;
import com.yuankong.badgeevolution.config.LoadConfig;
import com.yuankong.badgeevolution.config.level.Level;
import com.yuankong.badgeevolution.data.DataBase;
import com.yuankong.badgeevolution.data.PlayerData;
import com.yuankong.badgeevolution.util.CallBack;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.yuankong.badgeevolution.util.Util.compareLore;
import static com.yuankong.badgeevolution.util.Util.compareType;

public class Initiate {

    public static void setLevel(CommandSender sender,String playerName,String levelName){
        boolean flag = false;
        Level l = null;
        for(Level level: LoadConfig.getLevelList()){
            if(levelName.equals(level.getName())){
                l = level;
                flag = true;
                break;
            }
        }
        if(!flag){
            sender.sendMessage("不存在此勋章名称！");
            return;
        }

        Player player = Bukkit.getPlayerExact(playerName);
        if(player != null){
            DataBase.playerDataHash.get(player).setLevel(l);
            DataBase.playerDataHash.get(player).putAttribute();
            sender.sendMessage("玩家勋章等级设置成功!");
        }else{
            setOfflineLevel(sender,playerName,LoadConfig.getLevelList().indexOf(l));
        }
    }

    public static void setLevel(CommandSender sender, String playerName, int level){

        Player player = Bukkit.getPlayerExact(playerName);
        if(player != null){
            DataBase.playerDataHash.get(player).setLevelIndexOf(level);
            DataBase.playerDataHash.get(player).putAttribute();
            sender.sendMessage("玩家勋章等级设置成功!");
        }else{
            setOfflineLevel(sender,playerName,level);
        }
    }

    public static void setOfflineLevel(CommandSender sender,String playerName,int level){
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        DataBase.updateData(offlinePlayer.getUniqueId(), DataBase.Column.level, String.valueOf(level), new CallBack() {
            @Override
            public void onSuccess() {
                sender.sendMessage("设置玩家勋章等级成功!");
            }
            @Override
            public void onFail() {
                sender.sendMessage("设置玩家勋章等级失败!可能玩家不存在");
            }
        });
    }

    public static boolean isMath(CommandSender sender,String str){
        int x;
        try {
            x = Integer.parseInt(str);
        }catch (Exception e){
            sender.sendMessage("输入的参数不是数字!");
            return false;
        }
        if(x >= LoadConfig.getMaterialList().size()){
            sender.sendMessage("输入的参数过大!");
            return false;
        }else{
            return true;
        }
    }

    public static void info(Player sender,Player player){
        if(player == null){
            sender.sendMessage("抱歉，玩家可能不在线!");
            return;
        }

        sender.sendMessage(LoadConfig.getInfo().replaceAll("%info%",DataBase.playerDataHash.get(player).getLevel().getColorName()));
    }

    public static void materialEvolution(Player player){
        if(DataBase.playerDataHash.get(player).getLevelIndexOf()+1 >= LoadConfig.getLevelList().size()){
            player.sendMessage(LoadConfig.getNoLevel());
            return;
        }

        if(!takeAmount(player,false)){
            player.sendMessage(LoadConfig.getNoNeedMaterial());
            //inventoryView.close();
            return;
        }

        //金币材料
        if(LoadConfig.getLevelList().get(DataBase.playerDataHash.get(player).getLevelIndexOf()).getMoney()!=0.0){
            if(!takeMoney(player,false)){
                player.sendMessage(LoadConfig.getNoNeedMoney());
                //inventoryView.close();
                return;
            }
        }

        //先判断玩家所有材料（包括金币点券）是否足够，如果足够，再扣除
        takeAmount(player,true);
        takeMoney(player,true);

        evolution(player);

    }

    public static void pointsEvolution(Player player){

        if(DataBase.playerDataHash.get(player).getLevelIndexOf()+1 >= LoadConfig.getLevelList().size()){
            player.sendMessage(LoadConfig.getNoLevel());
            return;
        }

        if(LoadConfig.getLevelList().get(DataBase.playerDataHash.get(player).getLevelIndexOf()).getIfPoints()!=0){
            if(!takePoints(player,false)){
                player.sendMessage(LoadConfig.getNoNeedPoints());
                //inventoryView.close();
                return;
            }
        }
        takePoints(player,true);
        evolution(player);
    }

    public static void evolution(Player player){
        DataBase.playerDataHash.get(player).setLevelIndexOf(DataBase.playerDataHash.get(player).getLevelIndexOf()+1);

        Level level = LoadConfig.getLevelList().get(DataBase.playerDataHash.get(player).getLevelIndexOf());
        //执行指令
        if(level.isCommandEnable()){
            for(String str:level.getCommand(player)){
                if(str.contains("[CMD]") || str.contains("[cmd]")){
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),str.substring(str.indexOf("]")+1));
                }
                if(str.contains("[OP]") || str.contains("[op]")){
                    if(player.isOp()){
                        Bukkit.getServer().dispatchCommand(player,str.substring(str.indexOf("]")+1));
                    }else{
                        try {
                            player.setOp(true);
                            Bukkit.getServer().dispatchCommand(player,str.substring(str.indexOf("]")+1));
                            player.setOp(false);
                        }catch (Exception e){
                            BadgeEvolution.instance.getLogger().warning("出错了，执行OP指令失败,请检查玩家是否已经清除OP");
                        }finally {
                            player.setOp(false);
                        }
                    }
                }
                if(str.contains("[CHAT]") || str.contains("[chat]")){
                    player.chat(str.substring(str.indexOf("]")+1));
                }
            }
        }

        DataBase.playerDataHash.get(player).putAttribute();

        Bukkit.getScheduler().runTaskLater(BadgeEvolution.instance, player::closeInventory,10);
        player.sendMessage(LoadConfig.getSuccess());
    }


    public static boolean takeAmount(Player player, boolean isTake){
        PlayerData playerData = DataBase.playerDataHash.get(player);
        int indexOf = playerData.getLevelIndexOf()+1;
        HashMap<String, List<Integer>> slot = new HashMap<>();
        HashMap<String,Integer> hasAmount = new HashMap<>();
        Inventory inventory = player.getInventory();
        for(int i = 0;i < LoadConfig.getLevelList().get(indexOf).getMaterialName().size();i++){

            String materialName = LoadConfig.getLevelList().get(indexOf).getMaterialName().get(i);
            int materialAmount = 0;

            List<Integer> x = new ArrayList<>();
            for(int j = 0;j<inventory.getSize();j++){
                if(inventory.getItem(j)!=null && inventory.getItem(j).getItemMeta().hasDisplayName() && inventory.getItem(j).getItemMeta().hasDisplayName()){
                    ItemStack itemStack = inventory.getItem(j);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if(itemMeta.getDisplayName().equals(LoadConfig.getMaterialHash().get(materialName).getName()) && compareLore(itemMeta.getLore(),LoadConfig.getMaterialHash().get(materialName).getLore()) && compareType(itemStack,LoadConfig.getMaterialHash().get(materialName))){
                        materialAmount = materialAmount + itemStack.getAmount();
                        x.add(j);
                    }
                }
            }
            slot.put(materialName,x);
            hasAmount.put(materialName,materialAmount);
        }

        int b = 0;
        for(int i = 0;i<hasAmount.size();i++){
            String materialName = LoadConfig.getLevelList().get(indexOf).getMaterialName().get(i);
            int needAmount = LoadConfig.getLevelList().get(indexOf).getMaterialAmount().get(materialName);
            if(hasAmount.get(materialName) >= needAmount){
                b = b + 1;
            }
        }

        if(b == hasAmount.size()){
            if(isTake){
                for(int i = 0;i<hasAmount.size();i++){
                    String materialName = LoadConfig.getLevelList().get(indexOf).getMaterialName().get(i);
                    int amount = LoadConfig.getLevelList().get(indexOf).getMaterialAmount().get(materialName);
                    for (int x : slot.get(materialName)) {
                        if(amount > inventory.getItem(x).getAmount()){
                            amount = amount - inventory.getItem(x).getAmount();
                            inventory.getItem(x).setAmount(0);//扣除材料
                        }else{
                            int end = inventory.getItem(x).getAmount() - amount;
                            int take = Math.max(end, 0);
                            inventory.getItem(x).setAmount(take);//扣除材料
                            break;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    public static boolean takeMoney(Player player, boolean isTake){
        if(BadgeEvolution.getEcon().has(player,LoadConfig.getLevelList().get(DataBase.playerDataHash.get(player).getLevelIndexOf()+1).getMoney())){
            if(isTake){
                BadgeEvolution.getEcon().withdrawPlayer(player,LoadConfig.getLevelList().get(DataBase.playerDataHash.get(player).getLevelIndexOf()+1).getMoney());
            }
            return true;
        }
        return false;
    }

    public static boolean takePoints(Player player, boolean isTake){
        PlayerPoints points = BadgeEvolution.getPoints();

        System.out.println(points.getAPI().look(player.getUniqueId()));

        if(points.getAPI().look(player.getUniqueId()) >= LoadConfig.getLevelList().get(DataBase.playerDataHash.get(player).getLevelIndexOf()+1).getIfPoints()){
            if(isTake){
                points.getAPI().take(player.getUniqueId(),LoadConfig.getLevelList().get(DataBase.playerDataHash.get(player).getLevelIndexOf()+1).getIfPoints());
            }
            return true;
        }
        return false;
    }
}
