package io.github.catchaos8;

import com.badlogic.gdx.math.Vector2;

import java.util.*;

public class SnekComputingOld {
    //You can store variables here
    Snek snek;


    private static final int[][] DIRECTIONS = {
        {1, 0}, {-1, 0}, {0, 1},{0,-1}
    };

    static class Node {
        int x, y;
        double g, h;

        Node parent;

        Node(int x, int y, double g, double h, Node parent) {
            this.x = x;
            this.y = y;
            this.g = g;
            this.h = h;
            this.parent = parent;
        }


        double f() {
            return g + h;
        }

    }

    //This runs every time the snake moves(after), but u can use if(snek.justAteApple) to run it only if the snek ate an apple
    public Snek computeSnek(Snek snek) {
        this.snek = snek;
        if(snek.getSnekLength() < 20) {
            greedy(snek);

        } else { //Use the a* algorithm
            int[][] grid = snek.getRenderArray();
            Vector2 head = snek.getSnekHead();
            Vector2 apple = snek.getAppleCoords();

            List<Node> path = aStar(grid,
                (int) head.x, (int) head.y,
                (int) apple.x, (int) apple.y);

            if (path.size() > 1) {
                Node next = path.get(1); // step after head
                int dx = next.x - (int) head.x;
                int dy = next.y - (int) head.y;

                if (dx == 1) snek.turnRight();
                else if (dx == -1) snek.turnLeft();
                else if (dy == 1) snek.turnUp();
                else if (dy == -1) snek.turnDown();
            } else {
                // No path found -> fallback to greedy
                System.out.println("Greeddyyy!");
                greedy(snek);
            }
        }


        //Must have this
        return snek;
    }

    // fallback = your greedy goto apple
    private void greedy(Snek snek) {
        Vector2 apple = snek.getAppleCoords();
        Vector2 head = snek.getSnekHead();

        if (head.y > apple.y) {
            attemptDown(snek, 0);
        } else if (head.y < apple.y) {
            attemptUp(snek, 0);
        } else if (head.x > apple.x) {
            attemptLeft(snek, 0);
        } else if (head.x < apple.x) {
            attemptRight(snek, 0);
        }
    }

    private List<Node> aStar(int[][] grid, int startX, int startY, int goalX, int goalY) {
        //Basically make a list of things to go to
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::f));
        //Cost of the movement so far
        Map<String, Double> costSoFar = new HashMap<>();

        Node start = new Node(startX, startY, 0, heuristic(startX, startY, goalX, goalY), null);
        open.add(start);
        costSoFar.put(key(startX, startY), 0.0);

        while (!open.isEmpty()) {
            Node current = open.poll();

            if (current.x == goalX && current.y == goalY) {
                return reconstructPath(current);
            }

            for (int[] d : DIRECTIONS) {
                int nx = current.x + d[0];
                int ny = current.y + d[1];

                if (!inBounds(nx, ny, grid)) continue;
                if (grid[ny][nx] > 0 && grid[ny][nx] > current.g + 1) continue; // snake body is obstacle

                double wallBias = getWallBias(nx, ny, grid);
                double bodyBias = getBodyBias(nx, ny, grid);
                double newCost = current.g + 1 - wallBias - bodyBias;

                String k = key(nx, ny);

                if (!costSoFar.containsKey(k) || newCost < costSoFar.get(k)) {
                    costSoFar.put(k, newCost);
                    Node neighbor = new Node(nx, ny, newCost,
                        heuristic(nx, ny, goalX, goalY), current);
                    open.add(neighbor);
                }
            }
        }

        return Collections.emptyList(); // no path found
    }
    private double getBodyBias(int x, int y, int[][] grid) {
        // Check if adjacent to the snake body (not including the current tile)
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;

                int nx = x + dx;
                int ny = y + dy;

                if (ny < 0 || ny >= grid.length || nx < 0 || nx >= grid[0].length)
                    continue;

                if (grid[ny][nx] > 0) {
                    return 0.75; // touching the snake body
                }
            }
        }
        return 0.0;
    }


    private double getWallBias(int x, int y, int[][] grid) {
        int width = grid[0].length;
        int height = grid.length;

        // If touching any wall, return 0.5 bias
        if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
            return 0.25;
        }
        return 0.0;
    }



    private boolean inBounds(int x, int y, int[][] grid) { //Checks if the snake is in bounds or something
        return y >= 0 && y < grid.length && x >= 0 && x < grid[0].length;
    }

    private double heuristic(int x, int y, int goalX, int goalY) { //Manhattan distance? Basically the x + y
        return Math.abs(x - goalX) + Math.abs(y - goalY);
    }
    private String key(int x, int y) { //To store if we already went into the cell
        return x + "," + y;
    }

    private List<Node> reconstructPath(Node goal) { //make the path from the snake to apple(a* makes it backwards)
        List<Node> path = new ArrayList<>();
        for (Node n = goal; n != null; n = n.parent) {
            path.add(n);
        }
        Collections.reverse(path); // so it's from start → goal
        return path;
    }






    public boolean isSafe(Snek snek, int dx, int dy) {
        int[][] snekArray = snek.getRenderArray();

        int x = (int) snek.getSnekHead().x;
        int y = (int) snek.getSnekHead().y;

        if(x+dx >= snek.getxDim() || y+dy >= snek.getyDim() || x+dx < 0 || y+dy < 0) return false;  //Out of bounds

        return snekArray[y+dy][x+dx] < 1;
    }

    public void attemptLeft(Snek snek, int counter) {

        if(isSafe(snek, -1, 0)) {
            snek.turnLeft();
        } else {
            if(counter < 6) {
                attemptUp(snek, counter + 1);
            } else {
                System.out.println("Boxed in");
            }
        }
    }
    public void attemptUp(Snek snek, int counter) {

        if(isSafe(snek, 0, 1)) {
            snek.turnUp();
        } else {
            if(counter < 6) {
                attemptRight(snek, counter + 1);
            } else {
                System.out.println("Boxed in");
            }
        }
    }
    public void attemptRight(Snek snek, int counter) {

        if(isSafe(snek, 1, 0)) {
            snek.turnRight();
        } else {
            if(counter < 6) {
                attemptDown(snek, counter + 1);
            } else {
                System.out.println("Boxed in");
            }
        }
    }
    public void attemptDown(Snek snek, int counter) {

        if(isSafe(snek, 0, -1)) {
            snek.turnDown();
        } else {
            if(counter < 6) {
                attemptLeft(snek, counter + 1);
            } else {
                System.out.println("Boxed in");
            }
        }
    }





}
