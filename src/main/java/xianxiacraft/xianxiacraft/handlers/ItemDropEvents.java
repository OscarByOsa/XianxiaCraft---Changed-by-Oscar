package xianxiacraft.xianxiacraft.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import xianxiacraft.xianxiacraft.XianxiaCraft;

import static xianxiacraft.xianxiacraft.util.ManualItems.*;

public class ItemDropEvents implements Listener {

    public ItemDropEvents(XianxiaCraft plugin){
        Bukkit.getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event){
        // Пропуск события если умер игрок (не моб)
        if(event.getEntity() instanceof Player){
            return;
        }

        // Генерация случайного числа от 1 до 50 (2% шанс выпадения мануала)
        int random = (int) (Math.random() * 50) +1;

        EntityType entityType = event.getEntityType();

        // Проверка выпадения мануала (шанс 1 из 50 = 2%)
        if(random == 2){
            // Выпадение мануалов в зависимости от типа моба с тематическим соответствием
            if(entityType == EntityType.PIG){
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), fattyManualItem); // Мануал обжоры от свиньи
            } else if(entityType == EntityType.MUSHROOM_COW){
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), fungalManualItem); // Грибной мануал от грибной коровы
            } else if(entityType == EntityType.ENDERMAN){
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), spaceManualItem); // Космический мануал от эндермена
            } else if(entityType == EntityType.VILLAGER){
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), demonicManualItem); // Демонический мануал от жителя
            } else if(entityType == EntityType.BLAZE){
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), phoenixManualItem); // Мануал феникса от ифрита
            } else if(entityType == EntityType.CAVE_SPIDER){
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), poisonManualItem); // Ядовитый мануал от пещерного паука
            } else if(entityType == EntityType.STRAY){
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), iceManualItem); // Ледяной мануал от зомби-скитальца
            } else if(entityType == EntityType.PIGLIN || entityType == EntityType.ZOMBIFIED_PIGLIN){
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), sugarFiendManualItem); // Мануал сахарного маньяка от пиглинов
            } else if(entityType == EntityType.CREEPER){
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), lightningManualItem); // Мануал молнии от крипера
            } else if(entityType == EntityType.IRON_GOLEM){
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), ironSkinManualItem); // Мануал железной кожи от железного голема
            }
        }
    }
}