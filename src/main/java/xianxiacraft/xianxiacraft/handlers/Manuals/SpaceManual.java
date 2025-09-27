package xianxiacraft.xianxiacraft.handlers.Manuals;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xianxiacraft.xianxiacraft.QiManagers.PointManager;
import xianxiacraft.xianxiacraft.QiManagers.ScoreboardManager1;

import java.util.Random;

import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getPoints;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getStage;
import static xianxiacraft.xianxiacraft.util.CountNearbyBlocks.countNearbyBlocks;
import static xianxiacraft.xianxiacraft.util.ManualUtils.getCultivationModifier;

public class SpaceManual extends Manual{

    // Конструктор космического мануала: низкая регенерация ци 0.01, средняя защита 4 за стадию, ниже средней атака 3 за стадию
    // Специализация на пространственных манипуляциях и телепортации
    public SpaceManual(){
        super("Space Manual",0.01,4,3);
    }

    // Метод прогрессии для космического мануала: использует плод хоруса как ресурс, требует шалкербоксов для прорыва
    public static void spaceManualPointIncrement(Player player, ItemStack item) {

        // Проверка что предмет является плодом хоруса - тематический ресурс связанный с Эндом и пространственными искажениями
        if (item.getType() == Material.CHORUS_FRUIT) {

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
            if (points + 1 + cultivationModifier>= (int) (20 * Math.pow(10, (stage + 1) * Math.log10(2)) - 20)) {
                // Условие прорыва: игрок должен находиться рядом с экспоненциально растущим количеством шалкербоксов (2^(стадия-1))
                // Шалкербоксы тематически связаны с пространственными манипуляциями и измерением Энда
                if (!(countNearbyBlocks(player, Material.SHULKER_BOX) >= (Math.pow(2,stage-1)))) {
                    player.sendMessage(ChatColor.GOLD + "Требование для прорыва не выполнено. Обратитесь к вашему мануалу.");
                    return; // Прерывание если недостаточно шалкербоксов вокруг
                }
                // Прорыв на следующую стадию
                PointManager.addPoints(player, 1 + cultivationModifier);
                ScoreboardManager1.updateScoreboard(player);
                return;
            }

            // Обычное увеличение очков культивации (не прорывной случай)
            PointManager.addPoints(player, 1+ cultivationModifier);
            ScoreboardManager1.updateScoreboard(player);
        }
    }

    // Вспомогательный метод для генерации случайной локации в пределах заданного радиуса от центральной точки
    // Используется для пространственных способностей телепортации или случайного перемещения
    public static Location getRandomLocationWithinRadius(Location center, double radius) {
        Random random = new Random();
        double randomAngle = random.nextDouble() * Math.PI * 2; // Случайный угол от 0 до 2π радиан
        double randomRadius = random.nextDouble() * radius; // Случайное расстояние от центра (0 до radius)
        double randomYOffset = random.nextDouble() * 10 - 5; // Случайное смещение по Y от -5 до +5 блоков

        // Преобразование полярных координат в декартовы
        double offsetX = Math.cos(randomAngle) * randomRadius;
        double offsetZ = Math.sin(randomAngle) * randomRadius;

        // Возврат новой локации с применением смещений к исходной точке
        return center.clone().add(offsetX, randomYOffset, offsetZ);
    }
}