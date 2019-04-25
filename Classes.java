import java.util.Scanner;

public class Classes extends Character{
    Scanner input = new Scanner(System.in);
    public Classes(int dx, int dy){
        super(dx, dy,0,0,0,0,5,30, "Bob");
    }

    public int addExp(int enLvl, boolean boss){
        int A = enLvl * 2 + 10;
        int C = enLvl + this.Level + 10;
        int B;
        if (!boss)
            B = (rand.nextInt(75 - 35) + 35) * enLvl / 5;
        else
            B = (int)Math.floor((rand.nextInt(250 - 150) + 150) * enLvl / 5 * 1.5);
        int gain = (int)Math.floor(Math.floor(Math.sqrt(A) * Math.pow(A,2)) * B / Math.floor(Math.sqrt(C) * Math.pow(C,2))) + 1;
        this.exp += gain;
        return gain;
    }

    public void addStats(int hp, int atk, int def, int spd, int Mana){
        Level += 1;
        HP += hp;
        currHP += hp;
        Attack += atk;
        Defense += def;
        Speed += spd;
        mana += Mana;
        currMana += Mana;
    }

    public class Mage extends Classes{
        public Mage(int dx, int dy){
            super(dx,dy);
            System.out.println("Enter Your Name: ");
            String name = input.next();
            int HP = randStats(20,15,5,0);
            int atk = randStats(30,28,5,0);
            int def = randStats(22,17,5,0);
            int spd = randStats(7,1,5,0);
            this.setStats(HP,atk,def,spd,5, name);
        }

        public void increseStats(int enLvl, boolean boss){
            int gain = addExp(enLvl, boss);
            int nextLevel = this.Level + 1;
            int lvlup = (int)Math.floor(1.2 * Math.pow(nextLevel,3) - 15 * Math.pow(nextLevel,2) + 100 * nextLevel - 140);
            if (this.Level < 100)
                System.out.println("You Gained " + gain + " exp!");
            while (this.exp > lvlup){
                if (this.Level >= 100)
                    break;
                else if (this.Level < 100){
                    if(this.exp > lvlup) {
                        System.out.println("You gained a level!");
                        int hpGain = rand.nextInt(7 - 2) + 2;
                        int atkGain = (int) Math.floor((rand.nextInt(4 - 1) + 1) * 1.5);
                        int defGain = rand.nextInt(4 - 1) + 1;
                        int spdGain = (int) Math.floor((rand.nextInt(4 - 1) + 1) * 0.5);
                        int manaGain = (int) Math.floor((rand.nextInt(10 - 5) + 5) * 1.25);

                        addStats(hpGain, atkGain, defGain, spdGain, manaGain);

                        nextLevel = this.Level + 1;
                        lvlup = (int) Math.floor(1.2 * Math.pow(nextLevel, 3) - 15 * Math.pow(nextLevel, 2) + 100 * nextLevel - 140);
                    }
                }
            }
        }
    }

    public class Assassin extends Classes{
        public Assassin(int dx, int dy){
            super(dx,dy);
            System.out.println("Enter Your Name: ");
            String name = input.next();
            int HP = randStats(20,15,5,0);
            int atk = randStats(34,32,5,0);
            int def = randStats(14,9,5,0);
            int spd = randStats(17,15,5,0);
            setStats(HP,atk,def,spd,5, name);
        }

        public void increseStats(int enLvl, boolean boss){
            int gain = addExp(enLvl, boss);
            int nextLevel = this.Level + 1;
            int lvlup = (int)Math.floor(1.2 * Math.pow(nextLevel,3) - 15 * Math.pow(nextLevel,2) + 100 * nextLevel - 140);
            if (this.Level < 100)
                System.out.println("You Gained " + gain + " exp!");
            while (this.exp > lvlup){
                if (this.Level >= 100)
                    break;
                else if (this.Level < 100){
                    if(this.exp > lvlup) {
                        int hpGain = (int) Math.floor((rand.nextInt(7 - 2) + 2) * 0.5);
                        int atkGain = rand.nextInt(5 - 3) + 3;
                        int defGain = rand.nextInt(4 - 1) + 1;
                        int spdGain = rand.nextInt(5 - 3) + 3;
                        int manaGain = rand.nextInt(10 - 5) + 5;

                        addStats(hpGain, atkGain, defGain, spdGain, manaGain);

                        nextLevel = this.Level + 1;
                        lvlup = (int) Math.floor(1.2 * Math.pow(nextLevel, 3) - 15 * Math.pow(nextLevel, 2) + 100 * nextLevel - 140);
                    }
                }
            }
        }
    }

    public class Warrior extends Classes{
        public Warrior(int dx, int dy){
            super(dx,dy);
            System.out.println("Enter Your Name: ");
            String name = input.next();
            int HP = randStats(35,25,5,0);
            int atk = randStats(21,19,5,0);
            int def = randStats(22,20,5,0);
            int spd = randStats(7,1,5,0);
            setStats(HP,atk,def,spd,5, name);
        }

        public void increseStats(int enLvl, boolean boss){
            int gain = addExp(enLvl, boss);
            int nextLevel = Level + 1;
            int lvlup = (int)Math.floor(1.2 * Math.pow(nextLevel,3) - 15 * Math.pow(nextLevel,2) + 100 * nextLevel - 140);
            if (this.Level < 100)
                System.out.println("You Gained " + gain + " exp!");
            while (this.exp > lvlup){
                if (Level >= 100)
                    break;
                else if (this.Level < 100){
                    if(this.exp > lvlup) {
                        int hpGain = (int) Math.floor((rand.nextInt(7 - 2) + 2) * 0.5);
                        int atkGain = rand.nextInt(5 - 3) + 3;
                        int defGain = rand.nextInt(5 - 3) + 3;
                        int spdGain = rand.nextInt(4 - 1) + 1;
                        int manaGain = rand.nextInt(10 - 5) + 5;

                        addStats(hpGain, atkGain, defGain, spdGain, manaGain);

                        nextLevel = this.Level + 1;
                        lvlup = (int) Math.floor(1.2 * Math.pow(nextLevel, 3) - 15 * Math.pow(nextLevel, 2) + 100 * nextLevel - 140);
                    }
                }
            }
        }
    }

    public class Tank extends Classes{
        public Tank(int dx, int dy){
            super(dx,dy);
            System.out.println("Enter Your Name: ");
            String name = input.next();
            int HP = randStats(40,35,5,0);
            int atk = randStats(8,1,5,0);
            int def = randStats(31,29,5,0);
            int spd = randStats(6,1,5,0);
            setStats(HP,atk,def,spd,5, name);
        }

        public void increseStats(int enLvl, boolean boss){
            int gain = addExp(enLvl, boss);
            int nextLevel = this.Level + 1;
            int lvlup = (int)Math.floor(1.2 * Math.pow(nextLevel,3) - 15 * Math.pow(nextLevel,2) + 100 * nextLevel - 140);
            if (this.Level < 100)
                System.out.println("You Gained " + gain + " exp!");
            while (this.exp > lvlup){
                if (this.Level >= 100)
                    break;
                else if (this.Level < 100){
                    if(this.exp > lvlup) {
                        int hpGain = rand.nextInt(10 - 7) + 7;
                        int atkGain = (int) Math.floor((rand.nextInt(4 - 1) + 1) * 0.5);
                        int defGain = rand.nextInt(5 - 3) + 3;
                        int spdGain = rand.nextInt(4 - 1) + 1;
                        int manaGain = rand.nextInt(10 - 5) + 5;

                        addStats(hpGain, atkGain, defGain, spdGain, manaGain);

                        nextLevel = this.Level + 1;
                        lvlup = (int) Math.floor(1.2 * Math.pow(nextLevel, 3) - 15 * Math.pow(nextLevel, 2) + 100 * nextLevel - 140);
                    }
                }
            }
        }
    }

    public class Marksman extends Classes{
        public Marksman(int dx, int dy){
            super(dx,dy);
            System.out.println("Enter Your Name: ");
            String name = input.next();
            int HP = randStats(25,20,5,0);
            int atk = randStats(19,14,5,0);
            int def = randStats(19,11,5,0);
            int spd = randStats(22,20,5,0);
            setStats(HP,atk,def,spd,5, name);
        }

        public void increseStats(int enLvl, boolean boss){
            int gain = addExp(enLvl, boss);
            int nextLevel = this.Level + 1;
            int lvlup = (int)Math.floor(1.2 * Math.pow(nextLevel,3) - 15 * Math.pow(nextLevel,2) + 100 * nextLevel - 140);
            if (this.Level < 100)
                System.out.println("You Gained " + gain + " exp!");
            while (this.exp > lvlup){
                if (this.Level >= 100)
                    break;
                else if (this.Level < 100){
                    if(this.exp > lvlup) {
                        int hpGain = rand.nextInt(7 - 2) + 2;
                        int atkGain = rand.nextInt(4 - 1) + 1;
                        int defGain = (int) Math.floor((rand.nextInt(4 - 1) + 1) * 0.5);
                        int spdGain = (int) Math.floor((rand.nextInt(4 - 1) + 1) * 1.5);
                        int manaGain = rand.nextInt(10 - 5) + 5;

                        addStats(hpGain, atkGain, defGain, spdGain, manaGain);

                        nextLevel = this.Level + 1;
                        lvlup = (int) Math.floor(1.2 * Math.pow(nextLevel, 3) - 15 * Math.pow(nextLevel, 2) + 100 * nextLevel - 140);
                    }
                }
            }
        }
    }
}
