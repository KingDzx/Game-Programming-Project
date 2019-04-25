import java.util.Random;

public class Monster extends Entity{
    String[] names = {"Phantomteeth", "Spitesnare", "Hellsnake", "Soulwing", "Dawnsoul"};
    public Monster(int dx, int dy, int lvl){
        super(dx,dy);
        int enLvl = rand.nextInt((lvl + 2) - (lvl - 3)) + (lvl - 3);
        if (enLvl < 1)
            enLvl = 1;
        if (enLvl > 100)
            enLvl = 100;

        int HP = randStats(10,5,enLvl,3);
        int atk = randStats(11,7,enLvl,2.5);
        int def = randStats(11,7,enLvl,2.5);
        int spd = randStats(11,7,enLvl,2.5);
        this.setStats(HP,atk,def,spd,enLvl,names[rand.nextInt(4)]);
    }
}
