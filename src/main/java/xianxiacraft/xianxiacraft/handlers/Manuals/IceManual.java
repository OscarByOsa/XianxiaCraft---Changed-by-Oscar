package xianxiacraft.xianxiacraft.handlers.Manuals;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xianxiacraft.xianxiacraft.QiManagers.PointManager;
import xianxiacraft.xianxiacraft.QiManagers.ScoreboardManager1;
import xianxiacraft.xianxiacraft.util.CountNearbyBlocks;

import java.util.HashSet;
import java.util.Set;

import static xianxiacraft.xianxiacraft.QiManagers.PointManager.*;
import static xianxiacraft.xianxiacraft.util.ManualUtils.getCultivationModifier;

public class IceManual extends Manual {

    // Конструктор ледяного мануала: устанавливает низкий модификатор атаки 0.01, высокую защиту 6 за стадию и среднюю атаку 4 за стадию
    // Высокая защита соответствует тематике льда - оборонительная специализация
    public IceManual(){
        super("Ice Manual",0.01,6,4);
    }

    // Основной метод прогрессии для ледяного мануала: использует лед как ресурс культивации, требует синего льда для прорыва
    public static void iceManualPointIncrement(ItemStack itemInHand, Player player){

        // Проверка что предмет в руке является льдом - основной ресурс для этого мануала
        if (itemInHand.getType() == Material.ICE){

            int cultivationModifier = getCultivationModifier(player); // Получение бонусных модификаторов культивации

            int stage = getStage(player); // Текущая стадия культивации игрока
            int points = getPoints(player); // Текущее количество очков культивации

            // Проверка достижения порога для прорыва на следующую стадию (экспоненциальная формула: 20 × 2^(стадия+1) - 20)
            if(points + 1 + cultivationModifier >=  (int) (20 * Math.pow(2,(stage+1)) - 20)){
                // Условие прорыва: игрок должен находиться рядом с определенным количеством блоков синего льда
                // Требуемое количество растет экспоненциально: 2^(стадия-1) блоков синего льда
                if(!(CountNearbyBlocks.countNearbyBlocks(player,Material.BLUE_ICE) >= (Math.pow(2,stage-1)))){
                    player.sendMessage(ChatColor.GOLD + "Требование для прорыва не выполнено. Обратитесь к вашему мануалу.");
                    return; // Прерывание если условие не выполнено
                }
                // Потребление льда и прорыв на следующую стадию
                itemInHand.setAmount(itemInHand.getAmount()-1);
                PointManager.addPoints(player,1 + cultivationModifier);
                ScoreboardManager1.updateScoreboard(player);
                return;
            }

            // Обычное увеличение очков культивации (не прорывной случай)
            itemInHand.setAmount(itemInHand.getAmount()-1);
            PointManager.addPoints(player,1+ cultivationModifier);
            ScoreboardManager1.updateScoreboard(player);
        }
    }
}