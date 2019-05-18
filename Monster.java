import java.util.Random;

public class Monster extends Entity{
    String[] names = {"Phantomteeth", "Spitesnare", "Hellsnake", "Soulwing", "Dawnsoul"};
    public Monster(int dx, int dy, int floor){
        super(dx,dy);
        int enLvl = rand.nextInt((floor + 6) - (floor - 2)) + (floor - 2);
        if (enLvl < 1)
            enLvl = 1;
        if (enLvl > 100)
            enLvl = 100;

        int HP = randStats(13,9,enLvl,3);
        int atk = randStats(12,9,enLvl,2.5);
        int def = randStats(12,9,enLvl,2.5);
        int spd = randStats(12,9,enLvl,2.5);
        this.setStats(HP,atk,def,spd,enLvl,names[rand.nextInt(4)]);
    }
}
