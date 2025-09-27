package xianxiacraft.xianxiacraft.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xianxiacraft.xianxiacraft.XianxiaCraft;

import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getStage;

public class FreezeEffect {

    private static XianxiaCraft plugin;

    public FreezeEffect(XianxiaCraft plugin){
        this.plugin = plugin;
    }

    // Длительность заморозки в секундах на стадию (7 секунд за каждую стадию)
    private static final int FREEZE_DURATION_PER_STAGE = 7;

    // Создание льда в указанной локации (если там воздух)
    public static void createIce(Location location) {
        Block block = location.getBlock();
        if(block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR){
            block.setType(Material.ICE); // Преобразование воздуха в лед
        }
    }

    // Удаление льда в указанной локации (если там лед)
    public static void removeIce(Location location) {
        Block block = location.getBlock();
        if (block.getType() == Material.ICE) {
            block.setType(Material.AIR); // Преобразование льда обратно в воздух
        }
    }

    // Применение эффекта заморозки к цели (визуальное окружение льдом)
    public void applyFreezeDamage(LivingEntity target, Player attacker) {
        // Сохранение текущей локации цели
        Location playerLocation = target.getLocation();

        // Настройки визуального эффекта
        Material ice = Material.PACKED_ICE; // Используемый материал льда
        int radius = 1; // Радиус эффекта (1 блок во все стороны)

        World world = target.getWorld();

        // Изменение блоков вокруг цели на лед (создание ледяной клетки)
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; y <= radius; y++) {
                    Location blockLocation = playerLocation.clone().add(x, y+1, z); // Смещение на 1 блок вверх
                    if (blockLocation.getBlock().getType() != ice) {
                        world.getBlockAt(blockLocation).setType(ice); // Установка льда
                    }
                }
            }
        }

        // Планирование задачи для восстановления оригинальных блоков после заморозки
        new BukkitRunnable() {
            @Override
            public void run() {
                // Восстановление блоков в увеличенной области (радиус + 1) для надежности
                for (int x = -radius-1; x <= radius+1; x++) {
                    for (int z = -radius-1; z <= radius+1; z++) {
                        for(int y = -radius-1; y <= radius+1; y++){
                            Location blockLocation = playerLocation.clone().add(x, y+1, z);
                            if (blockLocation.getBlock().getType() == ice) {
                                world.getBlockAt(blockLocation).setType(Material.AIR); // Восстановление воздуха
                            }
                        }
                    }
                }
            }
        // Длительность заморозки: 7 секунд × стадия атакующего (в тиках: × 20)
        }.runTaskLater(plugin, FREEZE_DURATION_PER_STAGE * getStage(attacker) * 20L);
    }
}