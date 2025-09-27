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

public class IronSkinManual extends Manual {

    // Конструктор мануала железной кожи: низкая регенерация ци 0.01, низкая защита 2 за стадию, но очень высокая атака 7 за стадию
    // Специализация на атаке в ущерб защите - классический стек дамага
    public IronSkinManual(){
        super("Ironskin Manual",0.01,2,7);
    }

    // Основной метод прогрессии: потребление железных слитков для получения очков культивации, требует железных блоков для прорыва
    public static void ironSkinManualPointIncrement(ItemStack itemInHand,Player player){

        // Проверка что предмет в руке является железным слитком - основной ресурс для этого мануала
        if (itemInHand.getType() == Material.IRON_INGOT){

            int cultivationModifier = getCultivationModifier(player); // Бонусные модификаторы культивации

            int stage = getStage(player); // Текущая стадия культивации
            int points = getPoints(player); // Текущие очки культивации

            // Проверка достижения порога для прорыва (формула: 20 × 2^(стадия+1) - 20)
            // Комментарий разработчика указывает на исправление формулы для корректной работы на всех стадиях
            if(points + 1 + cultivationModifier >= (int) (20 * Math.pow(2,(stage+1)) - 20)){
                // Условие прорыва: игрок должен находиться рядом с экспоненциально растущим количеством железных блоков
                // Формула: 2^(стадия-1) блоков - чем выше стадия, тем больше блоков требуется
                if(!(CountNearbyBlocks.countNearbyBlocks(player,Material.IRON_BLOCK) >= (Math.pow(2,stage-1)))){
                    player.sendMessage(ChatColor.GOLD + "Требование для прорыва не выполнено. Обратитесь к вашему мануалу.");
                    return; // Прерывание если недостаточно железных блоков вокруг
                }
                // Потребление слитка и прорыв на следующую стадию
                itemInHand.setAmount(itemInHand.getAmount()-1);
                PointManager.addPoints(player,1+cultivationModifier);
                ScoreboardManager1.updateScoreboard(player);
                return;
            }

            // Обычное увеличение очков (не прорывной случай)
            itemInHand.setAmount(itemInHand.getAmount()-1);
            PointManager.addPoints(player,1+ cultivationModifier);
            ScoreboardManager1.updateScoreboard(player);
        }
    }

    /*
    // Закомментированная оригинальная реализация подсчета железных блоков - заменена на универсальную утилиту CountNearbyBlocks
    // Метод сканировал область 10 блоков вокруг игрока и подсчитывал все железные блокы
    private static int countNearbyIronBlocks(Player player) {
        int playerX = player.getLocation().getBlockX();
        int playerY = player.getLocation().getBlockY();
        int playerZ = player.getLocation().getBlockZ();

        Set<Block> ironBlocks = new HashSet<>();
        int radius = 10;

        for (int x = playerX - radius; x <= playerX + radius; x++) {
            for (int y = playerY - radius; y <= playerY + radius; y++) {
                for (int z = playerZ - radius; z <= playerZ + radius; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == Material.IRON_BLOCK) {
                        ironBlocks.add(block);
                    }
                }
            }
        }

        return ironBlocks.size();
    }
    */
}