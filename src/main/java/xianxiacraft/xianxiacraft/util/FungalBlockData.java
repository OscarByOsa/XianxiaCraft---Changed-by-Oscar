package xianxiacraft.xianxiacraft.util;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;

public class FungalBlockData {

    // Список материалов, которые преобразуются в МИЦЕЛИЙ грибным мануалом
    private static final ArrayList<Material> fungalToMycelium = new ArrayList<Material>(Arrays.asList(
        Material.DIRT, Material.DIRT_PATH, Material.COARSE_DIRT, Material.ROOTED_DIRT, // Типы земли
        Material.STONE, Material.END_STONE, Material.SMOOTH_STONE, // Каменные блоки
        Material.GRASS_BLOCK, // Травяные блоки
        Material.SAND, Material.RED_SAND, Material.GRAVEL, // Рыхлые материалы
        Material.NETHERRACK, Material.DEEPSLATE // Незерские и глубинные блоки
    ));

    // Список материалов, которые преобразуются в ГРИБНЫЕ БЛОКИ (красный грибной блок)
    private static final ArrayList<Material> fungalToMushroom = new ArrayList<Material>(Arrays.asList(
        Material.ACACIA_LEAVES, Material.AZALEA_LEAVES, Material.BIRCH_LEAVES, 
        Material.FLOWERING_AZALEA_LEAVES, Material.JUNGLE_LEAVES, Material.OAK_LEAVES, 
        Material.OAK_LEAVES, Material.DARK_OAK_LEAVES, Material.SPRUCE_LEAVES // Все типы листьев
    ));

    // Список материалов, которые преобразуются в ГРИБНЫЕ НОЖКИ (ножка гриба)
    private static final ArrayList<Material> fungalToStem = new ArrayList<Material>(Arrays.asList(
        Material.ACACIA_LOG, Material.BIRCH_LOG, Material.JUNGLE_LOG, Material.OAK_LOG, 
        Material.DARK_OAK_LOG, Material.OAK_LOG, Material.SPRUCE_LOG // Все типы бревен
    ));

    // Список материалов, которые преобразуются в ГРИБЫ (красный гриб вместо цветов)
    private static final ArrayList<Material> fungalToMushroomFlower = new ArrayList<Material>(Arrays.asList(
        Material.POPPY, Material.OXEYE_DAISY, Material.ORANGE_TULIP, Material.PINK_TULIP, 
        Material.CORNFLOWER, Material.ALLIUM, Material.RED_TULIP, Material.WHITE_TULIP, 
        Material.DANDELION, Material.AZURE_BLUET // Различные типы цветов
    ));

    // Проверка может ли блок быть преобразован в ГРИБ (вместо цветов)
    public static boolean checkToMushroomFlower(Material blockToBeChecked) {
        for(Material m : fungalToMushroomFlower){
            if(m == blockToBeChecked){
                return true; // Блок может быть преобразован
            }
        }
        return false; // Блок не может быть преобразован
    }

    // Проверка может ли блок быть преобразован в МИЦЕЛИЙ
    public static boolean checkMycelium(Material blockToBeChecked) {
        for(Material m : fungalToMycelium){
            if(m == blockToBeChecked){
                return true;
            }
        }
        return false;
    }

    // Проверка может ли блок быть преобразован в ГРИБНОЙ БЛОК
    public static boolean checkMushroom(Material blockToBeChecked) {
        for(Material m : fungalToMushroom){
            if(m == blockToBeChecked){
                return true;
            }
        }
        return false;
    }

    // Проверка может ли блок быть преобразован в ГРИБНУЮ НОЖКУ
    public static boolean checkStem(Material blockToBeChecked) {
        for(Material m : fungalToStem){
            if(m == blockToBeChecked){
                return true;
            }
        }
        return false;
    }
}