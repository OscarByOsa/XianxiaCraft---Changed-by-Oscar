package xianxiacraft.xianxiacraft.commands;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xianxiacraft.xianxiacraft.QiManagers.ManualManager;
import xianxiacraft.xianxiacraft.XianxiaCraft;

import static xianxiacraft.xianxiacraft.QiManagers.ScoreboardManager1.hideScoreboard;
import static xianxiacraft.xianxiacraft.QiManagers.ScoreboardManager1.showScoreboard;
import static xianxiacraft.xianxiacraft.util.ManualItems.tutorialBookItem;

public class PlayerCommands implements CommandExecutor {

    private XianxiaCraft plugin;

    public PlayerCommands(XianxiaCraft plugin){
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        // Проверка, что отправитель - игрок
        if(!(commandSender instanceof Player)){
            commandSender.sendMessage("Для использования этой команды необходимо быть игроком.");
            return true;
        }

        Player sender = (Player) commandSender;

        // Команда: cultutorial (учебник по культивации)
        if(command.getName().equalsIgnoreCase("cultutorial")){
            // Выдача учебной книги игроку
            sender.getWorld().dropItem(sender.getLocation(), tutorialBookItem);
            return true;
        }

        // Команда: manualaccept (принять мануал)
        if(command.getName().equalsIgnoreCase("manualaccept")){
            // Активация процесса принятия мануала
            ManualManager.accept(sender, plugin);
            return true;
        }

        // Команда: dantianscoreboard (управление интерфейсом даньтяня)
        if(command.getName().equalsIgnoreCase("dantianscoreboard")){
            // Проверка корректности аргументов
            if(strings.length != 1){
                sender.sendMessage(ChatColor.RED + "Использование: " + command.getUsage());
                return true;
            }
            
            if(strings[0].equals("show")){
                // Показать интерфейс культивации
                showScoreboard(sender);
            } else if(strings[0].equals("hide")){
                // Скрыть интерфейс культивации
                hideScoreboard(sender);
            } else {
                sender.sendMessage(ChatColor.RED + "Использование: " + command.getUsage());
            }
            return true;
        }

        return true;
    }
}