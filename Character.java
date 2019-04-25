import java.util.ArrayList;

public class Character extends Entity {
    protected int mana;
    protected int currMana;
    protected int exp;
    protected ArrayList<String> inventory;
    public Character(int dx, int dy, int HP, int Atk, int Def, int Spd, int lvl, int mana, String Name){
        super(dx,dy);
        this.setStats(HP,Atk,Def,Spd,lvl,Name);
        this.mana = mana;
        this.currMana = mana;
        this.exp = (int)Math.floor(1.2 * Math.pow(lvl,3) - 15 * Math.pow(lvl,2) + 100 * lvl - 140);
        this.inventory = new ArrayList<>();
        this.inventory.add("Mango");
        this.inventory.add("Chenette");
        this.inventory.add("Doubles");
    }
}
