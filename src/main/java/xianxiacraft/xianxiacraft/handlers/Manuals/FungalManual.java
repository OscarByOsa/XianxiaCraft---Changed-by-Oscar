package xianxiacraft.xianxiacraft.handlers.Manuals;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xianxiacraft.xianxiacraft.QiManagers.PointManager;
import xianxiacraft.xianxiacraft.QiManagers.ScoreboardManager1;
import xianxiacraft.xianxiacraft.util.CountNearbyBlocks;

import java.util.HashSet;
import java.util.Set;

import static xianxiacraft.xianxiacraft.QiManagers.PointManager.*;
import static xianxiacraft.xianxiacraft.util.CountNearbyBlocks.countNearbyBlocks;
import static xianxiacraft.xianxiacraft.util.ManualUtils.getCultivationModifier;

public class FungalManual extends Manual{

    // Конструктор: устанавливает базовые характеристики мануала - низкий модификатор атаки, средние показатели защиты и атаки за стадию
    public FungalManual(){
        super("Fungal Manual",0.01,4,4);
    }

    // Способность QiMove для грибного мануала: при активации дает невидимость на 4 секунды (80 тиков), при деактивации убирает эффект невидимости
    public static void fungalManualQiMove(Player player, boolean bool){
        if(bool){
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,80,1,false,false,false));
        } else{
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }

    // Основной метод увеличения очков культивации для грибного мануала: работает только с подозрительным рагу, дает двойной бонус очков
    public static boolean fungalManualPointIncrement(Player player, ItemStack item) {

        // Проверка, что предмет является подозрительным рагу - основной ресурс для культивации по этому мануалу
        if (item.getType() == Material.SUSPICIOUS_STEW) {

            int cultivationModifier = getCultivationModifier(player);

            int stage = getStage(player);
            int points = getPoints(player);

            ItemStack itemInRightHand = player.getInventory().getItemInMainHand();
            ItemStack itemInLeftHand = player.getInventory().getItemInOffHand();

            // Закомментированный код потребления предмета - вероятно, потребление обрабатывается в другом месте
//            if (item.equals(itemInRightHand)) {
//                itemInRightHand.setAmount(itemInRightHand.getAmount() - 1);
//            } else if (item.equals(itemInLeftHand)) {
//                itemInLeftHand.setAmount(itemInLeftHand.getAmount() - 1);
//            }

            // Проверка достижения порога для прорыва на следующую стадию с учетом двойного бонуса очков
            if (points + 2 + (2*cultivationModifier) >= (int) (20 * Math.pow(10, (stage + 1) * Math.log10(2)) - 20)) {
                // Условие прорыва: игрок должен находиться рядом хотя бы с одним блоком мицелия
                if (!(CountNearbyBlocks.countNearbyBlocks(player,Material.MYCELIUM) >= 1)) {
                    player.sendMessage(ChatColor.GOLD + "Требование для прорыва не выполнено. Обратитесь к вашему мануалу.");
                    return false;
                }
                // Прорыв на новую стадию с двойным бонусом очков
                PointManager.addPoints(player, 2 + (2*cultivationModifier));
                ScoreboardManager1.updateScoreboard(player);
                return true;
            }

            // Обычное увеличение очков культивации с двойным бонусом
            PointManager.addPoints(player, 2+(2* cultivationModifier));
            ScoreboardManager1.updateScoreboard(player);
            return true;
        }
        return false;
    }
}