package com.jeder.whitelist;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public class Main extends JavaPlugin implements Listener{

    FileConfiguration fc = this.getConfig();
    @Override
    public void onEnable(){
        getLogger().info("JederWhitelist插件載入中");
        fc.addDefault("enabled" , false );
        fc.addDefault( "denyMessage" , "You are not whilelisted on this server, if you think this is an error, please contact the Administrator." );
        List<String> defList = new ArrayList();
        fc.addDefault( "enabledPlayers"  , defList );
        fc.addDefault( "existReturn" , "This player has already whitelisted!" );
        fc.addDefault( "doesntNotExistReturn" , "This player isn't in the whitelist!" );
        String hp = "You do not have permissions to do that, if you think this is an error, please contact the Administrator." ;
        fc.addDefault( "haventPermissions" , hp );
        fc.options().copyDefaults( true ) ;
        saveConfig();
        //註冊偵聽指令
        getServer().getPluginManager().registerEvents( this , this );
    }

    @Override
    public boolean onCommand(CommandSender sender , Command cmd , String label , String args[] ) {
        //當指令鍵入的時候執行
        if (cmd.getName().equalsIgnoreCase("jederWhitelist")) {
            //當玩家或者主控台鍵入該指令時執行
            if (args.length == 0){
                //僅輸入jw時
                sender.sendMessage( "I don't know what do you want to do." );
                return false;
            }
            if (args[0].toLowerCase().equals("add")) {
                if ( args[1].equals( "" ) ) {
                    return false;
                }
                //判斷執行該指令的玩家是否擁有權限
                if ( sender.hasPermission( "jederWhitelist.add" ) ) {
                    //先判斷該玩家是否存在於白名單內
                    List<String> playerlist = fc.getStringList("enabledPlayers");
                    //開始遍歷
                    for (String playerName : playerlist) {
                        if (playerName.equals(args[1]) ) {
                            //該玩家已經存在於白名單內
                            String dnes = fc.getString("existReturn");
                            sender.sendMessage(dnes);
                            return true;//跳出循環
                        }
                    }
                    playerlist.add( args[1] );
                    this.getConfig().set( "enabledPlayers" , playerlist );
                    sender.sendMessage( "Succeed to add " + args[1] + "to the whitelist." );
                    this.saveConfig();
                    return true;
                }
                else
                {
                    //該玩家沒有權限時
                    sender.sendMessage( fc.getString( "haventPermissions" ) );
                }
                return false;
            }

            if ( args[0].toLowerCase().equals( "remove" ) ){
                if ( args[1] == null ){
                    return false;
                }
                //判斷該玩家是否不在白名單內
                List<String> playerList = fc.getStringList( "enabledPlayers" ) ;
                for (String playerName : playerList
                     ) {
                    if ( args[1].equals(playerName) ) {
                        //找到該玩家
                        playerList.remove( args[1] ) ;
                        this.getConfig().set( "enabledPlayers" , playerList );
                        sender.sendMessage( "Succeed to remove " + args[1] + "from the Whitelist." );
                        this.saveConfig();
                        //跳出循環
                        return true;
                    }

                }
                sender.sendMessage( fc.getString( "doesntNotExistReturn" ) );
                //跳出循環
                return true;
            }

            if ( args[0].toLowerCase().equals("list") ) {
                //輸出所有在白名單內的玩家
                String output = "Whitelisted Players: " ;
                List<String> playerList = fc.getStringList( "enabledPlayers" ) ;
                for ( String s : playerList
                     ) {
                    output = output + s + ", " ;
                }
                //將結果輸出
                sender.sendMessage( output );

                //結束循環
                return true;
            }

            if ( args[0].toLowerCase().equals("on") ){
                if (this.getConfig().getBoolean("enabled"))
                {
                    //當已經開啟時
                    sender.sendMessage( "The Whitelist is already enabled!" );
                }
                else {
                    this.getConfig().set( "enabled" , true);
                    saveConfig();
                    sender.sendMessage( "The Whitelist is enabled now." );
                }
                return true;
            }

            if ( args[0].toLowerCase().equals( "off" ) ){
                if ( !this.getConfig().getBoolean( "enabled" )) {
                    //當已經關閉時
                    sender.sendMessage( "The Whitelist is already disabled!" );
                }
                else {
                    this.getConfig().set( "enabled" , false);
                    saveConfig();
                    sender.sendMessage( "The Whitelist is disabled now." );
                }
                return true;
            }
            return false;
        }
        return false;
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent e){
        if (getConfig().getBoolean( "enabled" )) {
            String playerName = e.getPlayer().getName();
            Player player = e.getPlayer();
            List<String> playerList = fc.getStringList("enabledPlayers");
            for (String playersName : playerList
                    ) {
                //尋找該玩家是否存在於白名單內
                if (playersName.equals(playerName)) {
                    //找到了該玩家
                    return;
                }
            }
            //沒有找到該玩家
            System.out.println( "404" );
            player.kickPlayer(fc.getString("denyMessage"));
            return;
        }
    }

}

