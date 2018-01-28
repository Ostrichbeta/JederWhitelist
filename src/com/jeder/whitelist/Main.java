package com.jeder.whitelist;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.*;
import java.util.*;

public class Main extends JavaPlugin{

    FileConfiguration fc = this.getConfig();
    @Override
    public void onEnable(){
        getLogger().info("JederWhitelist插件載入中");
        fc.addDefault("enabled" , false );
        fc.addDefault( "denyMessage" , "You are not whilelisted on this server, if you think this is an error, please contact the Administrator." );
        List <String> defList = new ArrayList();
        fc.addDefault( "enabledPlayers"  , defList );
        fc.addDefault( "existReturn" , "This player has already whitelisted!" );
        fc.addDefault( "doesntNotExistReturn" , "This player isn't in the whitelist!" );
        fc.addDefault( "haventPermissions" , "You do not have permissions to do that, if you think this is an error, please contact the Administrator." );
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender , Command cmd , String label , String args[] ) {
        //當指令鍵入的時候執行
        if (cmd.getName().equalsIgnoreCase("jederWhitelist")) {
            //當玩家或者主控台鍵入該指令時執行
            if (args[1].toLowerCase() == "add") {
                if ( args[2] == null ) {
                    return false;
                }
                //判斷執行該指令的玩家是否擁有權限
                if ( sender.hasPermission( "jederWhitelist.add" ) ) {
                    //先判斷該玩家是否存在於白名單內
                    List<String> playerlist = fc.getStringList("enabledPlayers");
                    //開始遍歷
                    for (String playerName : playerlist) {
                        if (playerName == args[2] ) {
                            //該玩家已經存在於白名單內
                            String dnes = fc.getString("existReturn");
                            sender.sendMessage(dnes);
                            return true;//跳出循環
                        }
                    }
                    playerlist.add( sender.getName() );
                    sender.sendMessage( "Succeed to add " + args[2] + "to the whitelist." );
                }
                else
                {
                    //該玩家沒有權限時
                    sender.sendMessage( fc.getString( "haventPermissions" ) );
                }
            }

            if ( args[1].toLowerCase() == "remove" ){
                if ( args[2] == null ){
                    return false;
                }
                //判斷該玩家是否不在白名單內
                List<String> playerList = fc.getStringList( "enabledPlayers" ) ;
                for (String playerName : playerList
                     ) {
                    if ( args[2] == playerName ) {
                        //找到該玩家
                        playerList.remove( args[2] ) ;
                        sender.sendMessage( "Succeed to remove " + args[2] + "from the Whitelist." );
                        //跳出循環
                        return true;
                    }

                }
                sender.sendMessage( fc.getString( "doesntNotExistReturn" ) );
                //跳出循環
                return true;
            }

            if ( args[1].toLowerCase() == "list" ) {
                //輸出所有在白名單內的玩家
                String output = "Whitelisted Players: " ;
                List<String> playerList = fc.getStringList( "enabledPlayers" ) ;
                for ( String s : playerList
                     ) {
                    output = output + s + ", " ;
                }
                //結束循環
                return true;
            }
        }
        return false;
    }



}

