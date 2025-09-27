package xianxiacraft.xianxiacraft.handlers.Manuals;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xianxiacraft.xianxiacraft.QiManagers.PointManager;
import xianxiacraft.xianxiacraft.QiManagers.ScoreboardManager1;

import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getPoints;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getStage;
import static xianxiacraft.xianxiacraft.util.ManualUtils.getCultivationModifier;

public class PhoenixManual extends Manual{

    // Конструктор мануала феникса: очень высокая регенерация ци 0.05, высокая защита 6 за стадию, средняя атака 4 за стадию
    // Тематика возрождения и бессмертия феникса отражается в высокой регенерации и защите
    public PhoenixManual(){
        super("Phoenix Manual",0.05,6,4);
    }

    // Уникальный метод прогрессии: использует уровни опыта игрока вместо предметов для получения очков культивации
    public static void phoenixManualPointIncrement(Player player){

        int level = player.getLevel(); // Получение текущего уровня опыта игрока - основной ресурс для этого мануала

        // Проверка что у игрока есть хотя бы один уровень опыта для культивации
        if (level > 0){

            int cultivationModifier = getCultivationModifier(player); // Бонусные модификаторы культивации

            int stage = getStage(player); // Текущая стадия культивации
            int points = getPoints(player); // Текущие очки культивации

            // Проверка достижения порога для прорыва на следующую стадию (формула: 20 × 2^(стадия+1) - 20)
            // Количество получаемых очков равно уровню игрока + бонус от модификатора (level + level * cultivationModifier)
            if(points + level + (level * cultivationModifier) >=  (int) (20 * Math.pow(2,(stage+1)) - 20)){
                // Условие прорыва: эффективный уровень (с учетом модификаторов) должен быть >= 10 × текущая стадия
                // Это означает что для прорыва на высокие стадии требуются огромные уровни опыта
                if(!(level + (level * cultivationModifier) >= 10*getStage(player))){
                    player.sendMessage(ChatColor.GOLD + "Требование для прорыва не выполнено. Обратитесь к вашему мануалу.");
                    return; // Прерывание если уровень опыта недостаточен для прорыва
                }
                // Прорыв на следующую стадию: добавление очков равных эффективному уровню опыта
                PointManager.addPoints(player,level + (level * cultivationModifier));
                ScoreboardManager1.updateScoreboard(player);
                return;
            }

            // Обычное увеличение очков культивации (не прорывной случай)
            // Количество очков равно эффективному уровню опыта игрока
            PointManager.addPoints(player,level+(level * cultivationModifier));
            ScoreboardManager1.updateScoreboard(player);
        }
    }
}