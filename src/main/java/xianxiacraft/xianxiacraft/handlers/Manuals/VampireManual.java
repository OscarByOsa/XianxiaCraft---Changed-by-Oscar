package xianxiacraft.xianxiacraft.handlers.Manuals;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import xianxiacraft.xianxiacraft.QiManagers.PointManager;
import xianxiacraft.xianxiacraft.QiManagers.ScoreboardManager1;

import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getPoints;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getStage;
import static xianxiacraft.xianxiacraft.util.ManualUtils.getCultivationModifier;

public class VampireManual extends Manual {

    // Конструктор вампирского/демонического мануала: низкая регенерация ци 0.01, средние показатели атаки 5 и защиты 4 за стадию
    // Примечание: класс называется VampireManual, но в конструкторе передается имя "Demonic Manual" - возможно, несоответствие
    public VampireManual() {
        super("Demonic Manual", 0.01, 5, 4);
    }

    // Уникальный метод прогрессии для вампирского мануала: получает очки культивации через убийство определенных гуманоидных существ
    // В отличие от других мануалов, здесь прогресс достигается через комбат а не через потребление предметов
    public static void demonicManualManualPointIncrement(Player player, LivingEntity target) {

        int cultivationModifier = getCultivationModifier(player); // Бонусные модификаторы культивации

        int stage = getStage(player); // Текущая стадия культивации
        int points = getPoints(player); // Текущие очки культивации

        // Получение типа сущности цели для проверки допустимости жертвы
        EntityType e = target.getType();
        
        // Проверка что цель является одним из разрешенных гуманоидных существ:
        // - Деревенский житель (VILLAGER)
        // - Разоритель (PILLAGER) 
        // - Иллюзионист (ILLUSIONER)
        // - Заклинатель (EVOKER)
        // - Ведьма (WITCH)
        if (e == EntityType.VILLAGER || e == EntityType.PILLAGER || e == EntityType.ILLUSIONER || e == EntityType.EVOKER || e == EntityType.WITCH) {
            // Начисление очков культивации за убийство допустимой цели
            PointManager.addPoints(player, 1 + cultivationModifier);
            ScoreboardManager1.updateScoreboard(player);
        }
        // Если цель не является допустимым типом сущности, очки не начисляются
    }
}