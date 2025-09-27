package xianxiacraft.xianxiacraft.handlers.Manuals;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xianxiacraft.xianxiacraft.QiManagers.PointManager;
import xianxiacraft.xianxiacraft.QiManagers.ScoreboardManager1;

import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getPoints;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getStage;
import static xianxiacraft.xianxiacraft.util.ManualUtils.getCultivationModifier;

public class PoisonManual extends Manual{

    // Конструктор ядовитого мануала: низкая регенерация ци 0.01, средняя защита 4 за стадию, выше средней атака 5 за стадию
    // Специализация на токсичных атаках с умеренной защитой
    public PoisonManual(){
        super("Poison Manual",0.01,4,5);
    }

    // Метод прогрессии для ядовитого мануала: использует ядовитые предметы как ресурс, требует нахождения в болоте для прорыва
    public static boolean poisonManualPointIncrement(Player player, ItemStack item) {

        // Проверка что предмет является одним из ядовитых ресурсов: паучий глаз, рыба-фугу или ядовитый картофель
        if (item.getType() == Material.SPIDER_EYE || item.getType() == Material.PUFFERFISH || item.getType() == Material.POISONOUS_POTATO) {

            int cultivationModifier = getCultivationModifier(player); // Бонусные модификаторы культивации

            int stage = getStage(player); // Текущая стадия культивации
            int points = getPoints(player); // Текущие очки культивации

            // Определение в какой руке находится предмет для корректного потребления
            ItemStack itemInRightHand = player.getInventory().getItemInMainHand();
            ItemStack itemInLeftHand = player.getInventory().getItemInOffHand();

            // Потребление предмета из соответствующей руки
            if (item.equals(itemInRightHand)) {
                itemInRightHand.setAmount(itemInRightHand.getAmount() - 1);
            } else if (item.equals(itemInLeftHand)) {
                itemInLeftHand.setAmount(itemInLeftHand.getAmount() - 1);
            }

            // Проверка достижения порога для прорыва (сложная логарифмическая формула: 20 × 10^((стадия+1)×log10(2)) - 20)
            if (points + 1 + cultivationModifier >= (int) (20 * Math.pow(10, (stage + 1) * Math.log10(2)) - 20)) {
                // Условие прорыва: игрок должен находиться в биоме болота (тематическое требование для ядовитого мануала)
                if (!(player.getLocation().getBlock().getBiome() == Biome.SWAMP)) {
                    player.sendMessage(ChatColor.GOLD + "Требование для прорыва не выполнено. Обратитесь к вашему мануалу.");
                    return false; // Возврат false если условие не выполнено
                }
                // Прорыв на следующую стадию: добавление очков и установка сытости на 19
                PointManager.addPoints(player, 1 + cultivationModifier);
                ScoreboardManager1.updateScoreboard(player);
                player.setFoodLevel(19); // Установка специфического уровня сытости после прорыва
                return true;
            }

            // Обычное увеличение очков культивации (не прорывной случай)
            PointManager.addPoints(player, 1+ cultivationModifier);
            ScoreboardManager1.updateScoreboard(player);
            player.setFoodLevel(19); // Постоянное поддержание сытости на уровне 19 для этого мануала
            return true;
        }
        return false; // Возврат false если предмет не является подходящим ресурсом
    }
}