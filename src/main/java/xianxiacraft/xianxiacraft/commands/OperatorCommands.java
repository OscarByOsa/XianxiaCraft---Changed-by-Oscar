package xianxiacraft.xianxiacraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;

import static xianxiacraft.xianxiacraft.customItems.ToolItems.taijiPaintingItem;
import static xianxiacraft.xianxiacraft.util.ManualItems.*;
import static xianxiacraft.xianxiacraft.customItems.ToolItems.superHoeItem;
import static xianxiacraft.xianxiacraft.QiManagers.ManualManager.*;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.*;
import static xianxiacraft.xianxiacraft.QiManagers.ScoreboardManager1.updateScoreboard;

public class OperatorCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        // Проверка, что отправитель - игрок
        if(!(commandSender instanceof Player)){
            commandSender.sendMessage("Для использования этой команды необходимо быть игроком.");
            return true;
        }

        Player sender = (Player) commandSender;

        // Команда: obtain (получить предмет)
        if(command.getName().equalsIgnoreCase("obtain")){
            // Проверка прав доступа
            if(!(sender.hasPermission("xianxiacraft.utility.obtain"))){
                sender.sendMessage(ChatColor.RED + "У вас нет прав для использования этой команды.");
                return true;
            }
            
            // Проверка наличия аргумента
            if(strings.length == 1){
                switch(strings[0]){
                    case "Mycelium_Chronicle":
                        sender.getWorld().dropItem(sender.getLocation(),fungalManualItem);
                        break;
                    case "Lightning_Emperor_Legacy":
                        sender.getWorld().dropItem(sender.getLocation(),lightningManualItem);
                        break;
                    case "Infinite_Heart_Flow_Yin_Technique":
                        sender.getWorld().dropItem(sender.getLocation(),demonicManualItem);
                        break;
                    case "Sugar_Fiend_Ascension":
                        sender.getWorld().dropItem(sender.getLocation(),sugarFiendManualItem);
                        break;
                    case "Void_Lotus_Art":
                        sender.getWorld().dropItem(sender.getLocation(),spaceManualItem);
                        break;
                    case "Youqin_Xuanya_Guide_to_Toxicity":
                        sender.getWorld().dropItem(sender.getLocation(),poisonManualItem);
                        break;
                    case "Phoenix_Resurrection_Technique":
                        sender.getWorld().dropItem(sender.getLocation(),phoenixManualItem);
                        break;
                    case "Iron_Path_of_Indestructible_Vitality":
                        sender.getWorld().dropItem(sender.getLocation(),ironSkinManualItem);
                        break;
                    case "Celestial_Icebound_Path":
                        sender.getWorld().dropItem(sender.getLocation(),iceManualItem);
                        break;
                    case "Supreme_Gluttony_Scripture":
                        sender.getWorld().dropItem(sender.getLocation(),fattyManualItem);
                        break;
                    case "True_Essence_Hoe":
                        sender.getWorld().dropItem(sender.getLocation(),superHoeItem);
                        break;
                    case "Taiji_Painting":
                        sender.getWorld().dropItem(sender.getLocation(),taijiPaintingItem);
                        break;
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Используйте: /obtain название_предмета");
            }
            return true;
        }

        // Команда: addstage (добавить стадию)
        if(command.getName().equalsIgnoreCase("addstage")){
            if(!(sender.hasPermission("xianxiacraft.cultivation.addstage"))){
                sender.sendMessage(ChatColor.RED + "У вас нет прав для использования этой команды.");
                return true;
            }

            // Если указан игрок
            if(strings.length == 1){
                Player player = Bukkit.getPlayerExact(strings[0]);
                if(player != null){
                    // Формула прогрессии: 1 + 20 × 2^(текущая_стадия+1)
                    setPoints(player,1 + (int) (20 * Math.pow(2,getStage(sender)+1)));
                    updateScoreboard(player);
                } else {
                    sender.sendMessage(ChatColor.RED + "Игрок не найден.");
                }
                return true;
            }

            // Если игрок не указан - применяется к себе
            setPoints(sender,1 + (int) (20 * Math.pow(2,getStage(sender)+1)));
            updateScoreboard(sender);
            return true;
        }

        // Команда: checkstats (проверить статистику)
        if(command.getName().equalsIgnoreCase("checkstats")){
            // Проверка собственной статистики
            if(strings.length == 0){
                sender.sendMessage(ChatColor.GOLD + "Игрок: " + sender.getName() + 
                            "\nМануал: " + getManual(sender) +
                            "\nСтадия: " + getStage(sender) + " [" + percentToNextStage(sender) + "%]" +
                            "\nАтака: " + getManualAttackPerStage(getManual(sender))*getStage(sender) +
                            "\nЗащита: " + getManualDefensePerStage(getManual(sender))*getStage(sender) + 
                            "\nПознания Дао: " + daoAttainmentMap.getOrDefault(sender.getUniqueId(), new HashSet<>()).size() + "/3");
                return true;
            }

            // Проверка статистики другого игрока (требует прав)
            if(!(sender.hasPermission("xianxiacraft.cultivation.checkstats"))){
                sender.sendMessage(ChatColor.RED + "У вас нет прав для этого действия.");
                return true;
            }
            
            if(strings.length == 1){
                Player player = Bukkit.getPlayerExact(strings[0]);
                if(player != null){
                    sender.sendMessage(ChatColor.GOLD + "Игрок: " + player.getName() + 
                            "\nМануал: " + getManual(player) +
                            "\nСтадия: " + getStage(player) + " [" + percentToNextStage(player) + "%]" +
                            "\nАтака: " + getManualAttackPerStage(getManual(player))*getStage(player) +
                            "\nЗащита: " + getManualDefensePerStage(getManual(player))*getStage(player) + 
                            "\nПознания Дао: " + daoAttainmentMap.getOrDefault(sender.getUniqueId(), new HashSet<>()).size() + "/3");
                } else {
                    sender.sendMessage(ChatColor.RED + "Игрок не найден.");
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Используйте: '/checkstats игрок'");
            }
        }

        return true;
    }
}