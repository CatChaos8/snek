package io.github.catchaos8;

public class SnekComputing {
    //You can store variables here
    Snek snek;
    //This runs every time the snake moves(after), but u can use if(snek.justAteApple) to run it only if the snek ate an apple
    public SnekComputing(Snek snek) {
        this.snek = snek;
        //Must have this
        makeCycle();
    }

    public Snek computeSnek(Snek snek) {
        this.snek = snek;

        //Run the cycle
        runCycle();

        //Must have this
        return snek;
    }

    Runnable[][] cycle;

    private void runCycle() {
        Runnable turnCommand;
        turnCommand = cycle[snek.getyDim() - 1 - (int) snek.getSnekHead().y][(int) snek.getSnekHead().x];
        turnCommand.run();
    }

    private void makeCycle() {
        String[][] s = new String[snek.getyDim()][snek.getxDim()];

        //Make the array
        cycle = new Runnable[snek.getyDim()][snek.getxDim()];

        for (int y = 0; y < snek.getyDim(); y++) {
            if (y % 2 == 0) {

                int leftPart = 1;
                if(y == 0) {
                    leftPart = 0;
                }

                for (int x = 1; x < snek.getxDim()-leftPart; x++) {
                    cycle[y][x] = () -> attemptLeft(0);
                    s[y][x] = "L";

                }
            } else {
                int leftPart = 2;
                if(y == snek.getyDim() - 1) {
                    leftPart = 1;
                }
                for (int x = 0; x < snek.getxDim()-leftPart; x++) {
                    cycle[y][x] = () -> attemptRight(0);
                    s[y][x] = "R";

                }
            }
            //The things that make it go to the next bar
            if(y % 2 == 0) { //For the left rows
                if(cycle[y][0] == null) {
                    cycle[y][0] = () -> attemptDown(0);
                    s[y][0] = "D";
                }
            } else{ //Right rows
                if(cycle[y][snek.getxDim()-2] == null) {
                    cycle[y][snek.getxDim() - 2] = () -> attemptDown(0);
                    s[y][snek.getxDim() - 2] = "D";
                }
            }


            //Bar that goes up on the right side
            if(cycle[y][snek.getxDim()-1] == null) {
                cycle[y][snek.getxDim() - 1] = () -> attemptUp(0);
                s[y][snek.getxDim() - 1] = "U";
            }
        }

//        //Display for debug
//        for (int i = 0; i < snek.getyDim(); i++) {
//            System.out.println(Arrays.toString(s[i]));
//        }

    }

    public boolean isSafe(Snek snek, int dx, int dy) {
        int[][] snekArray = snek.getRenderArray();

        int x = (int) snek.getSnekHead().x;
        int y = (int) snek.getSnekHead().y;

        if(x+dx >= snek.getxDim() || y+dy >= snek.getyDim() || x+dx < 0 || y+dy < 0) return false;  //Out of bounds

        return snekArray[y+dy][x+dx] < 1;
    }

    public void attemptLeft(int counter) {

        if(isSafe(snek, -1, 0)) {
            snek.turnLeft();
        } else {
            if(counter < 6) {
                attemptUp(counter + 1);
            }
        }
    }
    public void attemptUp(int counter) {

        if(isSafe(snek, 0, 1)) {
            snek.turnUp();
        } else {
            if(counter < 6) {
                attemptRight(counter + 1);
            }
        }
    }
    public void attemptRight(int counter) {

        if(isSafe(snek, 1, 0)) {
            snek.turnRight();
        } else {
            if(counter < 6) {
                attemptDown(counter + 1);
            }
        }
    }
    public void attemptDown(int counter) {

        if(isSafe(snek, 0, -1)) {
            snek.turnDown();
        } else {
            if(counter < 6) {
                attemptLeft(counter + 1);
            }
        }
    }

}
