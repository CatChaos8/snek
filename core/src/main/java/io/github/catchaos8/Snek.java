package io.github.catchaos8;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.*;

public class Snek {
    Deque<Vector2> snakeParts = new LinkedList<>();

    public boolean arrayCounting = false;

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
        if(startSnekLength + this.snekPadding*2 < xDim) {
            for (int i = 0; i < startSnekLength; i++) {
                snakeArray[yDim / 2][i + this.snekPadding] = i + 1;
                snakeParts.add(new Vector2(i + this.snekPadding, yDim / 2));
            }
            //Set starting fruit pos
            snakeArray[yDim/2][startSnekLength + snekPadding*2] = -1;
            appleCoords = new Vector2(startSnekLength + snekPadding*2, yDim/2);


            //Make the snekhead
            snekHead = new Vector2( startSnekLength-1 + this.snekPadding, yDim/2);
        } else {
            for (int i = 0; i < 4; i++) {
                snakeArray[yDim / 2][i + this.snekPadding] = i + 1;
                snakeParts.add(new Vector2(i + this.snekPadding, yDim / 2));
            }
            //Set starting fruit pos
            snakeArray[yDim/2][snekPadding*2 + 3 ] = -1;
            appleCoords = new Vector2(snekPadding*2 + 3 , yDim/2);


            //Make the snekhead
            snekHead = new Vector2( snekPadding + 3, yDim/2);
        }


    }

    public void logInConsole() {
        for (int i = 0; i < yDim; i++) {
            System.out.println(Arrays.toString(snakeArray[i]));
        }
    }

    public void render(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        float gridSize = Math.min(800f / xDim, 800f / yDim);

        // Render snake body
        for (Vector2 part : snakeParts) {
            if (part.equals(snekHead)) {
                renderer.setColor(0.25f, 0.65f, 0.25f, 1); // head
            } else {
                renderer.setColor(0.25f, 0.85f, 0.25f, 1); // body
            }
            renderer.rect(part.x * gridSize, part.y * gridSize, gridSize, gridSize);
        }

        // Render apple
        renderer.setColor(1, 0, 0, 1);
        if(appleCoords != null) {
            renderer.rect(appleCoords.x * gridSize, appleCoords.y * gridSize, gridSize, gridSize);

        }

        renderer.end();
    }


    public boolean update(float delta) {
        this.time += delta;
        if (this.time < 1 / gameSpeed) return true; // not time to move yet
        this.time -= 1 / gameSpeed;
        movesSinceLastApple++;

        // Compute new head position
        Vector2 newHead = new Vector2(snekHead);
        switch (snekDir) {
            case UP: newHead.y += 1; break;
            case DOWN: newHead.y -= 1; break;
            case LEFT: newHead.x -= 1; break;
            case RIGHT: newHead.x += 1; break;
        }

        prevDir = snekDir;
        justAte = false;

        // Check collisions
        if (newHead.y < 0 || newHead.y >= yDim || newHead.x < 0 || newHead.x >= xDim) {
            System.out.println("Out of Bounds");
            return false;
        } else if (snakeArray[(int)newHead.y][(int)newHead.x] > 1) {
            System.out.println("Hit itself");
            return false;
        }

        // Check apple
        if (snakeArray[(int)newHead.y][(int)newHead.x] < 0) {
            snekLength++;
            makeApple();
            justAte = true;
            movesSinceLastApple = 0;
        }
        // Remove tail if no apple eaten
        if(arrayCounting) {
            if (!justAte && snakeParts.size() >= snekLength) {
                for (Vector2 v : snakeParts) {
                    snakeArray[(int) v.y][(int) v.x] -= 1;
                }

                Vector2 tail = snakeParts.removeFirst();
                snakeArray[(int) tail.y][(int) tail.x] = 0;
            }
        } else {
            if (!justAte && snakeParts.size() >= snekLength) {
                Vector2 tail = snakeParts.removeFirst();
                snakeArray[(int) tail.y][(int) tail.x] = 0;
            }
        }

        // Add new head
        snakeParts.addLast(newHead);
        snakeArray[(int)newHead.y][(int)newHead.x] = snekLength;
        snekHead = newHead;



        return true;
    }


    public boolean isJustAte() {
        return justAte;
    }

    private void makeApple() {
        List<Vector2> freeSpots = new ArrayList<>();

        // Collect all empty spots
        for (int yy = 0; yy < yDim; yy++) {
            for (int xx = 0; xx < xDim; xx++) {
                if (snakeArray[yy][xx] == 0) {
                    freeSpots.add(new Vector2(xx, yy));
                }
            }
        }

        // If no free spots, don't spawn apple
        if (freeSpots.isEmpty()) {
            appleCoords = null; // or leave as is
            return;
        }

        // Pick a random empty spot
        Vector2 chosen = freeSpots.get(random.nextInt(freeSpots.size()));
        snakeArray[(int) chosen.y][(int) chosen.x] = -1;
        appleCoords = chosen;
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
