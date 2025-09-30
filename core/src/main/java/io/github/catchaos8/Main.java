package io.github.catchaos8;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private float time;

    SnekComputing computer;

    private int movesSinceLastApple;

    private int attempts = 1;
    private int highscore = 0;
    private SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    private BitmapFont font;

    //Snek object
    Snek snek;

    final boolean ARRAYCOUNTING = false; //If true, the render array will have the amount of moves left until the part disappears
    //^Faster on large boards if it is false
    final int XDIM = 396; //Must be more than 3+snekPadding*2
    final int YDIM = 396;
    final int SNEKLENGTH = 5270; //Starting length
    final int SNEKPADDING = 3; //padding from the side
    final float GAMESPEED = 2500;
    final float VISUALFPS = 30; //Capping ur visual fps really helps with the game spead on higher snake sizes


    boolean isAlive = true;
    boolean isWon = false;

    @Override
    public void create() {
        time = 0;

        if(GAMESPEED > Gdx.graphics.getDisplayMode().refreshRate) {
            Gdx.graphics.setVSync(false);
        }

        movesSinceLastApple = 0;

        snek = new Snek(XDIM, YDIM, SNEKLENGTH, SNEKPADDING,GAMESPEED);
        snek.arrayCounting = ARRAYCOUNTING;

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        font = new BitmapFont();

        computer = new SnekComputing(snek);


    }

    @Override
    public void render() {
        time += Gdx.graphics.getDeltaTime();

        //Check if u have a new high score
        if(snek.getSnekLength() > highscore) {
            highscore = snek.getSnekLength();
        }

        if(!isWon) {
            if (isAlive && movesSinceLastApple < snek.getxDim()*snek.getyDim()*1.5) {
                //Update snek
                isAlive = snek.update(Gdx.graphics.getDeltaTime());
                isWon = snek.ifWon();

                movesSinceLastApple = snek.getMovesSinceLastApple();

                snek = computer.computeSnek(snek);
            } else {

                movesSinceLastApple = 0;
                attempts += 1;
                snek = new Snek(XDIM, YDIM, SNEKLENGTH, SNEKPADDING, GAMESPEED);
                isAlive = true;
            }
        }
        //Render snek
        if(time > 1 /VISUALFPS) {
            time -= 1/VISUALFPS;
            ScreenUtils.clear(0,0,0,1);
            snek.render(shapeRenderer);
        }

        //Render attempts + snek length
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //Set colour to gray
        shapeRenderer.setColor(0.25f,0.25f,0.25f,1);
        //Draw the rect
        shapeRenderer.rect(0,800,800,100);
        //End renderer to draw the text for stuff
        shapeRenderer.end();

        //Set font size
        font.getData().setScale(2);
        // Draw text (using batch)
        batch.begin();
        //Draw highscore + others
        font.draw(batch, "Highscore: " + highscore, 10, 850);     // bottom-left aligned
        font.draw(batch, "Length: " + snek.getSnekLength(), 800/3f, 850);
        font.draw(batch, "Attempts: " + attempts, 800/3f*2, 850);
        batch.end();



        //do the movement
        if((Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP) && snek.getPrevDir() != Snek.Direction.DOWN)) {
            //Go up
            snek.turnUp();
        } else if((Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN))&& snek.getPrevDir() != Snek.Direction.UP) {
            //Go down
            snek.turnDown();
        } else if((Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT))&& snek.getPrevDir() != Snek.Direction.RIGHT) {
            //Go up
            snek.turnLeft();
        } else if((Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))&& snek.getPrevDir() != Snek.Direction.LEFT) {
            //Go down
            snek.turnRight();
        }




    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
