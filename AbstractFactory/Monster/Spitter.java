package AbstractFactory.Monster;

import Character.Enemies.Enemy;
import FlyWeight.FlyWeightFactory;
import FlyWeight.MonsterImage;
import Map.Map;
import Map.Tile.HiderTile;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Spitter extends Enemy {

    public Spitter(int HP, Map map, int rel_x, int rel_y){
        super(HP, map, rel_x, rel_y);
        monsterImage = createMonsterImage();
    }
    @Override
    protected MonsterImage createMonsterImage() {

        try {
            return FlyWeightFactory.getMonsterImage("FlyWeight/Images/spitter.png");
        } catch (SlickException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void drawCharacter(Graphics g, int x, int y, int HP) {
        if (map.getTileByLoc(rel_x, rel_y).getClass() != HiderTile.class) {
            this.getRealLoc();
            monsterImage.draw(g,this.x, this.y, this.HP);
        }
    }
}
