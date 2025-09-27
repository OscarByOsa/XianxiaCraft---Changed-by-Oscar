package xianxiacraft.xianxiacraft.handlers.Manuals;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xianxiacraft.xianxiacraft.QiManagers.PointManager;
import xianxiacraft.xianxiacraft.QiManagers.ScoreboardManager1;
import xianxiacraft.xianxiacraft.util.CountNearbyBlocks;

import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getPoints;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getStage;
import static xianxiacraft.xianxiacraft.util.ManualUtils.getCultivationModifier;

public class LightningManual extends Manual{

    // Конструктор мануала молнии: высокая регенерация ци 0.03, очень высокая защита 7 за стадию, низкая атака 2 за стадию
    // Специализация на защите и выживаемости с тематикой электричества/молнии
    public LightningManual(){
        super("LightningManual",0.03,7,2);
    }

    // Основной метод прогрессии: потребление медных слитков для получения очков культивации, требует медных блоков для прорыва
    public static void lightningManualPointIncrement(ItemStack itemInHand, Player player){

        // Проверка что предмет в руке является медным слитком - основной ресурс для этого мануала (медь связана с электропроводностью)
        if (itemInHand.getType() == Material.COPPER_INGOT){

            int cultivationModifier = getCultivationModifier(player); // Бонусные модификаторы культивации

            int stage = getStage(player); // Текущая стадия культивации
            int points = getPoints(player); // Текущие очки культивации

            // Проверка достижения порога для прорыва (исправленная формула: 20 × 2^(стадия+1) - 20)
            if(points + 1 + cultivationModifier >= (int) (20 * Math.pow(2,(stage+1)) - 20)){
                // Условие прорыва: игрок должен находиться рядом с определенным количеством медных блоков (обычных или вощеных)
                // Суммируются оба типа медных блоков, требуется 2^(стадия-1) блоков
                if(!((CountNearbyBlocks.countNearbyBlocks(player,Material.COPPER_BLOCK) + CountNearbyBlocks.countNearbyBlocks(player,Material.WAXED_COPPER_BLOCK)) >= (Math.pow(2,stage-1)))){
                    player.sendMessage(ChatColor.GOLD + "Требование для прорыва не выполнено. Обратитесь к вашему мануалу.");
                    return; // Прерывание если недостаточно медных блоков вокруг
                }
                // Потребление слитка и прорыв на следующую стадию
                itemInHand.setAmount(itemInHand.getAmount()-1);
                PointManager.addPoints(player,1+ cultivationModifier);
                ScoreboardManager1.updateScoreboard(player);
                return;
            }

            // Обычное увеличение очков культивации (не прорывной случай)
            itemInHand.setAmount(itemInHand.getAmount()-1);
            PointManager.addPoints(player,1 + cultivationModifier);
            ScoreboardManager1.updateScoreboard(player);
        }
    }
}