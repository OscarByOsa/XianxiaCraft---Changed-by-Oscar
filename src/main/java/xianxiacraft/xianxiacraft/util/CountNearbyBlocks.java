package xianxiacraft.xianxiacraft.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class CountNearbyBlocks {

    // Подсчет блоков определенного типа в радиусе 10 блоков от игрока (стандартный радиус)
    public static int countNearbyBlocks(Player player, Material material) {
        int playerX = player.getLocation().getBlockX();
        int playerY = player.getLocation().getBlockY();
        int playerZ = player.getLocation().getBlockZ();

        Set<Block> blocks = new HashSet<>(); // Использование Set для исключения дубликатов
        int radius = 10; // Стандартный радиус поиска

        // Поиск по кубической области вокруг игрока
        for (int x = playerX - radius; x <= playerX + radius; x++) {
            for (int y = playerY - radius; y <= playerY + radius; y++) {
                for (int z = playerZ - radius; z <= playerZ + radius; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == material) {
                        blocks.add(block); // Добавление блока в Set (дубликаты игнорируются)
                    }
                }
            }
        }

        return blocks.size(); // Возврат количества уникальных блоков
    }

    // Подсчет блоков определенного типа с кастомным радиусом от игрока
    public static int countNearbyBlocks(Player player, Material material, int radius) {
        int playerX = player.getLocation().getBlockX();
        int playerY = player.getLocation().getBlockY();
        int playerZ = player.getLocation().getBlockZ();

        Set<Block> blocks = new HashSet<>();

        // Поиск по кубической области с заданным радиусом
        for (int x = playerX - radius; x <= playerX + radius; x++) {
            for (int y = playerY - radius; y <= playerY + radius; y++) {
                for (int z = playerZ - radius; z <= playerZ + radius; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == material) {
                        blocks.add(block);
                    }
                }
            }
        }

        return blocks.size();
    }

    // Проверка наличия блока определенного типа под игроком на заданной глубине
    public static boolean checkIfBlockUnderPlayerSomewhere(Player player, Material material, int height){
        int playerX = player.getLocation().getBlockX();
        int playerY = player.getLocation().getBlockY();
        int playerZ = player.getLocation().getBlockZ();

        // Поиск блока под игроком на глубине до height блоков
        for(int i = 0; i < height; i++){
            Block block = player.getWorld().getBlockAt(playerX, playerY - i, playerZ);
            if (block.getType() == material) {
                return true; // Блок найден
            }
        }
        return false; // Блок не найден
    }
}