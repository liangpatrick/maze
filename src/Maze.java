import java.lang.reflect.Array;
import java.util.*;

import static java.util.Comparator.comparingInt;

public class Maze {
//  maze can now be used without passing in to a method as long as constructor is created
//    char[][] maze;
//  use hashmap/hashset for time and space complexity
    HashMap<List<Integer>, Integer> visited;
//  row, col used for bfs
    private static int[] row = { -1, 0, 0, 1 };
    private static int[] col = { 0, -1, 1, 0 };
    public Maze(){
//        char[][] maze = new char[51][51];
        HashMap<List<Integer>, Integer> visited =  new HashMap<>();
//        this.maze = maze;
        this.visited = visited;
    }
//  constructor for testing
    public Maze(int param){
//        char[][] maze = new char[param][param];
        HashMap<List<Integer>, Integer> visited = new HashMap<>();
//        this.maze = maze;
        this.visited = visited;
    }
//  maze generator for when you want dynamic value. because maze is square, only need one input
    char[][] generateMaze(){
        char[][] maze = new char[51][51];
        for (int x = 0; x < maze.length; x++){
            for(int y = 0; y < maze[x].length; y++){
//              rate is [0,1)
                double rate = Math.random();
//              1 for blocked and 0 for unblocked
                maze[x][y] = rate >= .28 ? ' ' : '#';
            }
        }
        visited = verifyMaze(maze);
        if (!visited.containsKey(List.of(0, 0)) || visited == null){
            return generateMaze();
        }
        maze[0][0] = 'S';
        maze[maze.length-1][maze.length-1] = 'T';
        return maze;

    }

//  wrapper method
    HashMap<List<Integer>, Integer> verifyMaze(char[][] maze){
        HashMap<List<Integer>, Integer> visited = new HashMap<List<Integer>, Integer>();
//      do this each time so small can be both accessible anywhere and won't have any repeat cells
        verifyMaze(maze.length-1, maze.length-1, visited,maze);

        return visited;
    }
//   BFS to verify if the there is a path from T to S; finds connected component(aka visitable cells); finds shortest path from each cell to T;
     void verifyMaze(int x, int y, HashMap<List<Integer>, Integer> visited, char[][] maze){
//      fringe to store cells that need to be visited
        Queue<Agents.index> fringe = new LinkedList<>();
//      add beginning cell to fringe and visited
        fringe.add(new Agents.index(x,y, 0));
        visited.put(List.of(x,y), 0);
        while(!fringe.isEmpty()) {
//          use poll instead of remove so no errors are thrown
            Agents.index curr = fringe.poll();
//          indX and indY hold current positions, didn't want to keep using curr.x or curr.y for laziness sakes
            int indX = curr.x, indY = curr.y, distance = curr.distance;
//          checks all neighbors to see if they are eligible to be added to the fringe
            for (int i = 0; i < row.length; i++) {
                int currX = indX + row[i];
                int currY = indY + col[i];
//                System.out.println(currX +", " + currY);
                if (0 <= currX && currX < maze.length && 0 <= currY && currY < maze[x].length && maze[currX][currY] != '#' &&  !visited.containsKey(List.of(currX, currY))){
                    fringe.add(new Agents.index(currX, currY, distance+1));
                    visited.put(List.of(currX,currY), distance+1);
                }
            }

        }
    }



//  prints maze
    static void printMaze(char [][] maze){
        for (char[] x : maze)
        {
            for (char y : x)
            {
                System.out.print(y + " ");
            }
            System.out.println();
        }
    }



//    public static void main(String[] args) {
//
//
//
//    }
}