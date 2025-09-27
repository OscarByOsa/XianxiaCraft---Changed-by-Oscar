package xianxiacraft.xianxiacraft.handlers.Manuals;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xianxiacraft.xianxiacraft.QiManagers.PointManager;
import xianxiacraft.xianxiacraft.QiManagers.ScoreboardManager1;

import java.util.HashSet;
import java.util.Set;

import static xianxiacraft.xianxiacraft.QiManagers.PointManager.*;
import static xianxiacraft.xianxiacraft.util.ManualUtils.getCultivationModifier;

public class FattyManual extends Manual{

    public FattyManual(){
        super("Fatty Manual",0.01,5,5);
    }


    public static void fattyManualQiMove(Player player, boolean bool){
        if(bool){
            // Активация абсолютной устойчивости к отталкиванию
            player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1.0);
        } else{
            // Деактивация - возврат к нормальной устойчивости
            player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0);
        }
    }

    public static void fattyManualPointIncrement(Player player, ItemStack item){
        // Получение модификатора культивации (бусты, бонусы и т.д.)
        int cultivationModifier = getCultivationModifier(player);

        int stage = getStage(player);
        int points = getPoints(player);

        // Определение, в какой руке находится предмет для потребления
        ItemStack itemInRightHand = player.getInventory().getItemInMainHand();
        ItemStack itemInLeftHand = player.getInventory().getItemInOffHand();

        // Потребление предмета (уменьшение количества на 1)
        if(item.equals(itemInRightHand)){
            itemInRightHand.setAmount(itemInRightHand.getAmount()-1);
        } else if (item.equals(itemInLeftHand)){
            itemInLeftHand.setAmount(itemInLeftHand.getAmount()-1);
        }

        // Проверка достижения порога следующей стадии
        if(points + 1 + cultivationModifier >=  (int) (20 * Math.pow(10,(stage+1) * Math.log10(2)) - 20)){
            // Особое условие для прорыва: игрок должен быть голоден
            if(player.getFoodLevel() > 0){
                player.sendMessage(ChatColor.GOLD + "Требование для прорыва не выполнено. Обратитесь к вашему мануалу.");
                return;
            }
            // Прорыв на новую стадию
            PointManager.addPoints(player,1+ cultivationModifier);
            ScoreboardManager1.updateScoreboard(player);
            // Полное восстановление сытости после прорыва
            player.setFoodLevel(20);
            player.setSaturation(20);
            return;
        }

        // Обычное увеличение очков
        PointManager.addPoints(player,1+ cultivationModifier);
        ScoreboardManager1.updateScoreboard(player);
        // Установка специфического уровня сытости для этого мануала
        player.setFoodLevel(19);
        player.setSaturation(8);
    }
}