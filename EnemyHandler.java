import Tile.FieryTile;
import org.lwjgl.Sys;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnemyHandler extends CharacterHandler
{

    public EnemyHandler(int enemyId) throws IOException {
        this.characterId = enemyId;
        Random rng = new Random();
        EnemyFactory factory = new EnemyFactory();
        characterModel = factory.createEnemy(11, 0, 5);
        characterModel.id = enemyId;
        Turnline.getInstance().Add(characterModel);
    }
    @Override
    public void run() {
        Turnline turnline = Turnline.getInstance();
        synchronized (turnline)
        {
//            Turnline turnline = Turnline.getInstance();
            if ( turnline.getCharacter() != null && turnline.getCharacter() instanceof Enemy && turnline.getCharacter().id == characterId)
            {
//                turnline.Remove(characterModel);
                turnline.Next();
                PacketBuilder builder;
                if(turnline.getCharacter() != null && turnline.getCharacter() instanceof Player)
                {
                    System.out.println("HP = "+ characterModel.getHP());

                    characterModel.updateCharacter((Player)turnline.getCharacter());

                    List<Area> newAreas = Server.map.getAreas(characterModel.getY(), characterModel.getX());
                    List<Area> oldOnes = new ArrayList<>(this.areas);
                    System.out.println(this.areas.size() + " areas");
                    oldOnes.removeAll(newAreas);
                    for (Area area : oldOnes){
                        area.removeCharacter(this);
                    }
                    for (Area area : newAreas){
                        area.addCharacter(this);
                    }

                    builder = new DamagePlayerPacketBuilder();
                    PacketDirector.constructDamagePlayerPacket(builder, (Player)turnline.getCharacter());
                    Packet p = builder.getPacket();
                    /*Packet p = new Packet(-1, -1, -1, true);
                    p.setAttack(true);
                    p.setHP(turnline.getCharacter().getHP());*/
                    try {
                        Server.clients.get(0).sendPacket(p);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    if(turnline.getCharacter().getHP() <= 0)
                        turnline.Remove(turnline.getCharacter());

                    builder = new ChangeOfEnemyPositionPacketBuilder();
                    PacketDirector.constructChangeOfEnemyPositionPacket(builder, (Enemy) characterModel);

                    Packet packet = builder.getPacket();
                    try {
                        Server.broadcastPacket(packet);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (Server.map.getTileByLoc(characterModel.getRel_x(), characterModel.getRel_y()).getClass() == FieryTile.class) {
                    characterModel.damageCharacter();
                }
            }
        }
    }
    @Override
    public void sendPacket(Packet packet) throws IOException {}
}
