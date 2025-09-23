package io.github.catchaos8;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;
import java.util.Random;

public class Snek {

    //Make random obj
    private final Random random = new Random();

    private int movesSinceLastApple = 0;

    private double time = 0;
    private final float gameSpeed;

    //Snake game dims
    private final int xDim;
    private final int yDim; //Min STARTSNAKELENGTH + SNAKEPADDING
    private final int startSnekLength;
    private final int snekPadding;
    private int snekLength;

    private boolean justAte = false;

    public enum Direction {
        UP,
        LEFT,
        DOWN,
        RIGHT
    }
    private Direction snekDir = Direction.RIGHT;

    private Vector2 appleCoords;

    private Vector2 snekHead;


    private int[][] snakeArray;

    private Direction prevDir;

    public Snek(int maxX, int maxY, int snekLength, int snekPadding, float gameSpeed) {
        this.xDim = maxX;
        this.yDim = maxY;
        this.startSnekLength = snekLength;
        this.snekPadding = snekPadding;
        this.snekLength = snekLength;
        this.gameSpeed = gameSpeed;

        prevDir = Direction.RIGHT;

        snakeArray = new int[yDim][xDim];
        //Make for loops for array for the render array to set it to clear
        for (int i = 0; i < xDim; i++) {
            for (int j = 0; j < yDim; j++) {
                //Sets it to 'o', wich is empty
                snakeArray[j][i] = 0;
            }
        }

        //Set starting snek pos
        for (int i = 0; i < startSnekLength; i++) {
            snakeArray[yDim /2][i+ this.snekPadding] = i+1;
        }
        //Set starting fruit pos
        snakeArray[yDim/2][startSnekLength + snekPadding*2] = -1;
        appleCoords = new Vector2(startSnekLength + snekPadding*2, yDim/2);



        //Make the snekhead
        snekHead = new Vector2( startSnekLength-1 + this.snekPadding, yDim/2);
    }

    public void logInConsole() {
        for (int i = 0; i < yDim; i++) {
            System.out.println(Arrays.toString(snakeArray[i]));
        }
    }

    public void render(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        //Grid size
        float gridSize = Math.min(800/xDim, 800/yDim);

        //Render snake
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                if(snakeArray[y][x] > 0) { //If snek
                    //Check if head
                    if(snakeArray[y][x] == snekLength) {
                        //Set colour to dark green cause its the head
                        renderer.setColor(0.25f, 0.65f, 0.25f, 1);
                    } else {
                        //set colour to green
                        renderer.setColor(0.25f, 0.85f, 0.25f, 1);
                    }
                    renderer.rect(x*gridSize, y*gridSize, gridSize, gridSize);
                } else if(snakeArray[y][x] < 0) { //If snek
                    //set colour to red
                    renderer.setColor(1,0,0,1);
                    renderer.rect(x*gridSize, y*gridSize, gridSize, gridSize);
                }
            }
        }


        renderer.end();
    }

    public boolean update(float delta) {
        this.time += delta;
        //If the snake is to update
        if(this.time > 1/gameSpeed) {
            movesSinceLastApple++;

            //update the snek
            this.time -= 1/gameSpeed;



            //Move head
            switch (snekDir) {
                case UP:
                    this.snekHead = this.snekHead.add(0,1);
                    break;
                case DOWN:
                    this.snekHead = this.snekHead.add(0,-1);
                    break;
                case LEFT:
                    this.snekHead = this.snekHead.add(-1,0);
                    break;
                case RIGHT:
                    this.snekHead = this.snekHead.add(1,0);
                    break;
            }
            //Reset justAte
            justAte = false;

            //set the prevDir
            prevDir = snekDir;
            //Check if snek hit itself or went out of bounds
            if(snekHead.y < 0 || snekHead.y >= yDim || snekHead.x < 0 || snekHead.x >= xDim) { //Went out of bounds
//                System.out.println("Out of Bounds");
                return false;
            } else if(snakeArray[(int) snekHead.y][(int) snekHead.x] > 0) { //Hit itself
//                System.out.println("Hit itself");
                return false;
            }

            //Loop moving the snake forwards
            for (int i = 0; i < xDim; i++) {
                for (int j = 0; j < yDim; j++) {
                    //Checks if the thing is a snek
                    if (snakeArray[j][i] > 0  && snakeArray[(int) snekHead.y][(int) snekHead.x] != -1) {
                        //Make shorter
                        snakeArray[j][i] -= 1;
                    }
                }
            }

            //Check if snek ate apple
            if(snakeArray[(int) snekHead.y][(int) snekHead.x] < 0) {
                //Increase size
                snekLength += 1;
                //make new apple
                makeApple();

                justAte = true;
            }

            //Add the head as a thing now
            snakeArray[(int) snekHead.y][(int) snekHead.x] = snekLength;
        }

        return true;

    }

    public boolean isJustAte() {
        return justAte;
    }

    private void makeApple() {

        //Make 2 rand nums for x and y
        int x, y;

        //Checks if the apple can be placed there, otherwise it calls itself again
        do {
            x = random.nextInt(xDim);
            y = random.nextInt(yDim);
        } while(snakeArray[y][x] != 0);

        snakeArray[y][x] = -1;
        appleCoords = new Vector2(x, y);
    }

    public Vector2 getAppleCoords() {
        return appleCoords;
    }

    public boolean ifWon() {
        return snekLength >= xDim * yDim;
    }

    public Direction getPrevDir() {
        return prevDir;
    }

    public int getSnekLength() {
        return snekLength;
    }

    public int getMovesSinceLastApple() {
        return movesSinceLastApple;
    }

    public int getSnekPadding() {
        return snekPadding;
    }

    public int getStartSnekLength() {
        return startSnekLength;
    }

    public int getxDim() {
        return xDim;
    }

    public int getyDim() {
        return yDim;
    }

    public int[][] getRenderArray() {
        return snakeArray;
    }

    public Vector2 getSnekHead() {
        return snekHead;
    }

    public void turnUp() {
        this.snekDir = Direction.UP;
    }
    public void turnDown() {
        this.snekDir = Direction.DOWN;
    }
    public void turnLeft() {
        this.snekDir = Direction.LEFT;
    }
    public void turnRight() {
        this.snekDir = Direction.RIGHT;
    }
}
