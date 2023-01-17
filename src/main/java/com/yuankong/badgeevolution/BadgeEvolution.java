package com.yuankong.badgeevolution;

import com.yuankong.badgeevolution.config.LoadConfig;
import com.yuankong.badgeevolution.data.DataBase;
import com.yuankong.badgeevolution.init.Initiate;
import com.yuankong.badgeevolution.init.PAPI;
import com.yuankong.badgeevolution.listener.EventHandler;
import com.yuankong.badgeevolution.listener.EventHandler2;
import eos.moe.dragoncore.network.PacketSender;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class BadgeEvolution extends JavaPlugin {
    public static JavaPlugin instance;
    private static Economy econ = null;
    static PlayerPoints points;
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        LoadConfig.load();

        Bukkit.getPluginManager().registerEvents(new EventHandler(),this);
        Bukkit.getPluginManager().registerEvents(new EventHandler2(),this);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "========BadgeEvolution已开启========");

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPI().register();
        }
        setPoints();
        if (!setupEconomy() ) {//判断前置插件是否存在
            getLogger().severe(String.format("[%s] - 未找到前置插件:Vault!", getDescription().getName()));
            //getServer().getPluginManager().disablePlugin(this);
            //return;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!"been".equalsIgnoreCase(command.getName())) {
            return false;
        }

        if (sender.isOp()) {
            command.setUsage("§a/been reload -重载 \n§a/been open -打开界面 \n§a/been set <等级> <player> -设置玩家等级 \n§a/been info -查看玩家等级");

            if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {

                LoadConfig.reload();

                List<Player> list = new ArrayList<>(DataBase.playerDataHash.keySet());
                for(Player player:list){
                    DataBase.playerDataHash.get(player).setLevel(LoadConfig.getLevelList().get(DataBase.playerDataHash.get(player).getLevelIndexOf()));
                }
                sender.sendMessage("§f[BadgeEvolution]§a配置已重新加载！");

                return true;
            }

            if (args.length == 3 && "set".equalsIgnoreCase(args[0])) {
                Initiate.setLevel(sender,args[2],args[1]);
                return true;
            }

            if (args.length == 3 && "setlevel".equalsIgnoreCase(args[0])) {
                if(Initiate.isMath(sender,args[2])){
                    Initiate.setLevel(sender,args[1],Integer.parseInt(args[2]));
                }
                return true;
            }
        }

        if(!(sender instanceof Player)){
            sender.sendMessage("该指令不能在控制台执行");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1 && "open".equalsIgnoreCase(args[0])) {
            PacketSender.sendOpenGui(player,LoadConfig.getGuiName());
            return true;
        }

        if (args.length >= 1 && args.length <= 2 && "info".equalsIgnoreCase(args[0])) {
            if(args.length == 1){
                Initiate.info(player,player);
            }else{
                Player lookPlayer = Bukkit.getPlayerExact(args[1]);
                Initiate.info(player,lookPlayer);
            }

            return true;
        }


        return !sender.isOp();
    }


    @Override
    public void onDisable() {}
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEcon(){
        return econ;
    }
    public void setPoints(){
        final Plugin plugin = this.getServer().getPluginManager().getPlugin("PlayerPoints");
        points = (PlayerPoints) plugin;
    }

    public static PlayerPoints getPoints(){
        return points;
    }
}
