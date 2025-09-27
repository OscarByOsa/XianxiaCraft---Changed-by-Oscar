package xianxiacraft.xianxiacraft.handlers.Manuals;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xianxiacraft.xianxiacraft.QiManagers.PointManager;
import xianxiacraft.xianxiacraft.QiManagers.ScoreboardManager1;

import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getPoints;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getStage;
import static xianxiacraft.xianxiacraft.util.CountNearbyBlocks.countNearbyBlocks;
import static xianxiacraft.xianxiacraft.util.ManualUtils.getCultivationModifier;

public class SugarFiendManual extends Manual{

    // Конструктор мануала сахарного маньяка: очень высокая регенерация ци 0.05, средние показатели атаки 5 и защиты 4 за стадию
    // Специализация на скорости и мобильности с тематикой сладостей
    public SugarFiendManual(){
        super("Sugar Fiend",0.05,5,4);
    }

    // Способность QiMove для сахарного мануала: дает эффект скорости, уровень которого зависит от стадии культивации
    public static void sugarFiendQiMove(Player player,boolean bool){
        if(bool){
            // Активация: добавляет эффект скорости с уровнем равным текущей стадии игрока на неограниченное время
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE,getStage(player),false,false,false));
        } else{
            // Деактивация: убирает эффект скорости
            player.removePotionEffect(PotionEffectType.SPEED);
        }
    }

    // Метод прогрессии для сахарного мануала: работает только в Нижнем мире и использует сладости как ресурс
    public static boolean sugarFiendManualPointIncrement(Player player, ItemStack item) {

        // Основное условие: игрок должен находиться в Нижнем мире (тематическое требование - связь с "адскими" сладостями)
        if (player.getWorld().getName().equals("world_nether")) {
            // Проверка что предмет является одним из сладких ресурсов: торт, печенье, тыквенный пирог или бутылка меда
            if (item.getType() == Material.CAKE || item.getType() == Material.COOKIE || item.getType() == Material.PUMPKIN_PIE || item.getType() == Material.HONEY_BOTTLE) {

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
                    // Условие прорыва: игрок должен находиться рядом с экспоненциально растущим количеством тортов (2^(стадия-1))
                    if (!(countNearbyBlocks(player, Material.CAKE) >= (Math.pow(2, stage - 1)))) {
                        player.sendMessage(ChatColor.GOLD + "Требование для прорыва не выполнено. Обратитесь к вашему мануалу.");
                        return false; // Прерывание если недостаточно тортов вокруг
                    }
                    // Прорыв на следующую стадию
                    PointManager.addPoints(player, 1 + cultivationModifier);
                    ScoreboardManager1.updateScoreboard(player);
                    player.setFoodLevel(19); // Установка сытости на 19 после прорыва
                    return true;
                }

                // Обычное увеличение очков культивации (не прорывной случай)
                PointManager.addPoints(player, 1+ cultivationModifier);
                ScoreboardManager1.updateScoreboard(player);
                player.setFoodLevel(19); // Постоянное поддержание сытости на уровне 19
                return true;
            }
            return false; // Возврат false если предмет не является сладостью
        }
        return false; // Возврат false если игрок не в Нижнем мире
    }
}