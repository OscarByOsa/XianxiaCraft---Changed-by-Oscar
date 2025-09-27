package xianxiacraft.xianxiacraft.util;

import org.bukkit.entity.Player;
import xianxiacraft.xianxiacraft.XianxiaCraft;

import java.util.HashSet;
import java.util.Set;

import static xianxiacraft.xianxiacraft.QiManagers.ManualManager.getManual;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.daoAttainmentMap;

public class ManualUtils {

    /**
     * Расчет модификатора культивации на основе достижений Дао игрока
     * Модификатор представляет собой бонус к очкам культивации за изучение других мануалов
     * 
     * @param player игрок для которого рассчитывается модификатор
     * @return модификатор культивации (0-3)
     */
    public static int getCultivationModifier(Player player){

        // Получение набора достижений Дао игрока (изученных мануалов)
        Set<String> daoAttainmentSet = new HashSet<>(daoAttainmentMap.getOrDefault(player.getUniqueId(), new HashSet<>()));
        
        // Удаление текущего мануала игрока из набора (бонус дается только за другие мануалы)
        daoAttainmentSet.remove(getManual(player));
        
        // Возврат количества изученных других мануалов, но не более 3 (максимальный бонус)
        return Math.min(daoAttainmentSet.size(), 3);
    }
}