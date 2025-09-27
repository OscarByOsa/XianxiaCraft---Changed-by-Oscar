package xianxiacraft.xianxiacraft.handlers.Manuals;

public class Manual {

    private String name; // Название мануала (например, "LightningManual", "Ice Manual" и т.д.)
    private double qiRegeneration = 0.01; // Базовая регенерация ци в секунду (значение по умолчанию)
    private int attackDamagePerStage = 4; // Бонус атаки за каждую стадию культивации (значение по умолчанию)
    private int defensePerStage = 4; // Бонус защиты за каждую стадию культивации (значение по умолчанию)

    // Основной конструктор: принимает все параметры мануала - название, регенерацию ци, атаку и защиту за стадию
    public Manual(String name, double qiRegeneration,int attackDamagePerStage,int defensePerStage){
        this(name,qiRegeneration); // Вызов конструктора с названием и регенерацией
        this.attackDamagePerStage = attackDamagePerStage; // Установка кастомного значения атаки за стадию
        this.defensePerStage = defensePerStage; // Установка кастомного значения защиты за стадию
    }

    // Конструктор с названием и регенерацией ци: устанавливает стандартные значения атаки и защиты (4/4)
    public Manual(String name, double qiRegeneration){
        this(name); // Вызов базового конструктора с названием
        this.qiRegeneration = qiRegeneration; // Установка кастомного значения регенерации ци
    }

    // Базовый конструктор: устанавливает только название, использует стандартные значения регенерации (0.01), атаки и защиты (4/4)
    public Manual(String name){
        this.name = name; // Установка названия мануала
    }

    // Геттер для получения названия мануала
    public String getManualName(){
        return name;
    }

    // Геттер для получения скорости регенерации ци (возвращает значение в double для точности расчетов)
    public double getQiRegeneration(){
        return qiRegeneration;
    }

    // Геттер для получения бонуса атаки за стадию (умножается на текущую стадию игрока для расчета общей атаки)
    public int getAttackPerStage() { return attackDamagePerStage; }

    // Геттер для получения бонуса защиты за стадию (умножается на текущую стадию игрока для расчета общей защиты)
    public int getDefensePerStage() { return defensePerStage; }
}