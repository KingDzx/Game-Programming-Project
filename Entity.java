import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Entity{
    protected ArrayList<Animation> animations = null;
    protected Animation currAnimation;
    protected Random rand;
    protected int DX;
    protected int DY;

    protected String name;
    protected int HP;
    protected int currHP;
    protected int Attack;
    protected int Defense;
    protected int Speed;
    protected int Level;
    protected boolean alive;

    public Entity(int dx, int dy) {
        this.animations = new ArrayList<>();
        this.currAnimation = null;
        this.rand = new Random();

        this.DX = dx;
        this.DY = dy;

        this.name = "";
        this.HP = 0;
        this.currHP = HP;
        this.Attack = 0;
        this.Defense = 0;
        this.Speed = 0;
        this.Level = 0;

        alive = true;
    }

    public void addAnimation(Animation a){
        animations.add(a);
    }

    public void setAnimation(int num){
        currAnimation = animations.get(num);
    }

    public void setAnimationFromPrevious(int num){
        Animation temp = animations.get(num);
        temp.setX(currAnimation.getX());
        temp.setY(currAnimation.getY());
        currAnimation = temp;
    }

    public void setStats(int hp, int atk, int def, int spd, int lvl, String name){
        this.name = name;
        this.HP = hp;
        this.currHP = HP;
        this.Attack = atk;
        this.Defense = def;
        this.Speed = spd;
        this.Level = lvl;
    }

    protected int randStats(int upBound, int lowBound, int lvl, double scale){
        return Math.round((rand.nextInt(upBound - lowBound) + lowBound) + Math.round((rand.nextInt(2 - 1) + 1) * (Math.random() * scale) * lvl));
    }

    public void moveLeft () {
        setAnimationFromPrevious(1);
        int newX = currAnimation.getX()- DX;
        currAnimation.setX(newX);

        if (newX < 0) {					// hits left wall
            currAnimation.setX(0);
        }
    }

    public void moveRight () {
        setAnimationFromPrevious(2);
        int newX = currAnimation.getX()+ DX;
        currAnimation.setX(newX);

        if (newX + currAnimation.xSize >= currAnimation.dimension.width) {		// hits right wall
            currAnimation.setX(currAnimation.dimension.width - currAnimation.xSize);
        }

    }

    public void moveUp() {
        setAnimationFromPrevious(3);
        int newY = currAnimation.getY() - DY;
        currAnimation.setY(newY);

        if (newY < 0) {					// hits left wall
            currAnimation.setY(0);
        }
    }

    public void moveDown() {
        setAnimationFromPrevious(0);
        int newY = currAnimation.getY() + DY;
        currAnimation.setY(newY);

        if (newY + currAnimation.ySize >= currAnimation.dimension.height) {		// hits right wall
            currAnimation.setY(currAnimation.dimension.height - currAnimation.ySize);
        }
    }

    public int attack(Entity e){
        int a = rand.nextInt(71) + 30;
        int b = rand.nextInt(100) + 1;
        int damage = Math.round(((((2 * Level / 5 + 2) * Attack * a / e.Defense) / 50) + 2) * b / 100);
        e.setCurrHP(e.currHP - damage);
        return damage;
    }

    public void setCurrHP(int x){
        currHP = x;
    }

    public int getDefense(){
        return Defense;
    }

    public int getSpeed(){
        return Speed;
    }

    public int getCurrHP(){
        return currHP;
    }

    public int getHP(){
        return HP;
    }

    public boolean isAlive(){
        if (currHP <= 0){
            return false;
        }
        return true;
    }

    public void update(){
        currAnimation.update();
        //currHP = currHP - 1;
    }

    public void draw(Graphics g){
        currAnimation.draw(g);
    }

    public void drawString(Graphics g, int X, int Y){
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g.setColor(Color.RED);
        g.drawString("HP: " + currHP + "/" + HP, X, Y);
    }

    public int getX(){
        return currAnimation.getX();
    }

    public int getY(){ return currAnimation.getY(); }


    public Rectangle2D.Double getRect(){
        return currAnimation.getBoundingRectangle();
    }

    public boolean startBattle(Entity e){
        Rectangle2D.Double enemy = e.getRect();
        Rectangle2D.Double player = getRect();

        if (enemy.intersects(player))
            return true;
        else
            return false;
    }

    public Rectangle2D.Double getBoundingRectangle(){
        return currAnimation.getBoundingRectangle();
    }

    public void setX(int x){
        currAnimation.setX(x);
    }

    public void setY(int y){
        currAnimation.setY(y);
    }

    public void setDX(int x){
        DX = x;
    }

    public void setDY(int y){
        DY = y;
    }
}
