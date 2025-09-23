package io.github.catchaos8;

import com.badlogic.gdx.math.Vector2;

public class SnekComputing {
    //You can store variables here


    //This runs every time the snake moves(after), but u can use if(snek.justAteApple) to run it only if the snek ate an apple
    public Snek computeSnek(Snek snek) {
        //Check dir
        Snek.Direction dir = snek.getPrevDir();
        //Get apple coords
        Vector2 apple = snek.getAppleCoords();
        //Get snek head
        Vector2 head = snek.getSnekHead();

        //If the apple is to the left
        if(head.y > apple.y) {
            if(dir != Snek.Direction.UP) {
                snek.turnDown();
            } else {
                snek.turnLeft();
            }
        } else if (head.y < apple.y) {
            if(dir != Snek.Direction.DOWN) {
                snek.turnUp();
            } else {
                snek.turnRight();
            }
        } else if(head.x > apple.x) {
            if(dir != Snek.Direction.RIGHT) {
                snek.turnLeft();
            } else {
                snek.turnUp();
            }
        } else if(head.x < apple.x) {
            if(dir != Snek.Direction.LEFT) {
                snek.turnRight();
            } else {
                snek.turnDown();
            }
        }


        //Must have this
        return snek;
    }
}
