import java.lang.reflect.Array;
import java.util.*;

import static java.util.Comparator.comparingInt;

public class Maze {
//  maze can now be used without passing in to a method as long as constructor is created
    char[][] maze;
//  use hashmap/hashset for time and space complexity
    HashMap<List<Integer>, Integer> small;
    HashSet<List<Integer>> visited;
//  row, col used for bfs
    private static int[] row = { -1, 0, 0, 1 };
    private static int[] col = { 0, -1, 1, 0 };
    public Maze(){
        char[][] maze = new char[51][51];
        HashSet<List<Integer>> visited = new HashSet<>();
        HashMap<List<Integer>, Integer> small = new HashMap<List<Integer>, Integer>();
        this.maze = maze;
        this.visited = visited;
        this.small = small;
    }
//  constructor for testing
    public Maze(int param){
        char[][] maze = new char[param][param];
        HashSet<List<Integer>> visited = new HashSet<>();
        HashMap<List<Integer>, Integer> small = new HashMap<List<Integer>, Integer>();
        this.maze = maze;
        this.visited = visited;
        this.small = small;
    }
//  maze generator for when you want dynamic value. because maze is square, only need one input
    char[][] generateMaze(){
        for (int x = 0; x < maze.length; x++){
            for(int y = 0; y < maze[x].length; y++){
//              rate is [0,1)
                double rate = Math.random();
//              1 for blocked and 0 for unblocked
                maze[x][y] = rate >= .28 ? ' ' : '#';
            }
        }
        visited = verifyMaze();
        if (!visited.contains(List.of(0, 0)) || visited == null){
            return generateMaze();
        }
        maze[0][0] = 'S';
        maze[maze.length-1][maze.length-1] = 'T';
        return maze;

    }

//  wrapper method
    HashSet<List<Integer>> verifyMaze(){
        HashSet<List<Integer>> visited = new HashSet<List<Integer>>();
//      do this each time so small can be both accessible anywhere and won't have any repeat cells
        small = new HashMap<List<Integer>, Integer>();
        verifyMaze(maze.length-1, maze.length-1, visited);

        return visited;
    }
//   BFS to verify if the there is a path from T to S; finds connected component(aka visitable cells); finds shortest path from each cell to T;
     void verifyMaze(int x, int y, HashSet<List<Integer>> visited){
//      fringe to store cells that need to be visited
        Queue<Agents.index> fringe = new LinkedList<>();
//      add beginning cell to fringe and visited
        fringe.add(new Agents.index(x,y, 0));
        visited.add(List.of(x,y));
        small.put(List.of(x,y), 0);
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
                if (0 <= currX && currX < maze.length && 0 <= currY && currY < maze[x].length && maze[currX][currY] != '#' &&  !visited.contains(List.of(currX, currY))){
                    visited.add(List.of(currX, currY));
                    fringe.add(new Agents.index(currX, currY, distance+1));
                    small.put(List.of(currX,currY), distance+1);
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

    static ArrayList runAgentOne(int ghosts){
        long startTime = System.nanoTime();
        long endTime;
        long duration;
        Maze maze = new Maze();
        ArrayList<Double> survivalRates = new ArrayList<>();
//      how many ghosts spawn, up to x ghosts
        for (int num =0; num < ghosts; num++) {
            int success = 0;
//            System.out.println("Ghosts #" + num);
//          how many mazes are made to solve for num number of ghosts
            for(int iter = 1; iter <= 100; iter++ ) {
                char[][] m = maze.generateMaze();
                if(maze.visited.contains(List.of(0, 0))) {
                    if (Agents.agentOne(num, m, maze.visited)) {
                        success += 1;

                    }
//                    endTime = System.nanoTime();
//                    duration = (endTime - startTime)/(long)Math.pow(10,9);
//                    System.out.println("Iter #" + iter + "; Success: " + success +"; Time: " + duration);
                }
            }
            survivalRates.add(success/100.0);

        }
        endTime = System.nanoTime();
        duration = (endTime - startTime)/(long)Math.pow(10,9);
        System.out.println("Agent One Total: " + duration);
        return survivalRates;

    }

    static ArrayList runAgentTwo(int ghosts){
        long startTime = System.nanoTime();
        long endTime;
        long duration;
        Maze maze = new Maze();
        ArrayList<Double> survivalRates = new ArrayList<>();
//      how many ghosts spawn, up to 100 ghosts
        for (int num = 0; num < ghosts; num++) {
            int success = 0;
//            System.out.println("Ghosts #" + num);
//          how many mazes are made to solve for num number of ghosts
            for(int iter = 1; iter <= 100; iter++ ) {
                char[][] m = maze.generateMaze();
                if(maze.visited.contains(List.of(0, 0))) {
                    if (Agents.agentTwo(num, m, maze.visited,0,0, null)) {
                        success += 1;

                    }
                }
//                endTime = System.nanoTime();
//                duration = (endTime - startTime)/(long)Math.pow(10,9);
//                System.out.println("Iter #" + iter + "; Success: " + success +"; Time: " + duration);
            }
//            System.out.println("Run #" + num+ ": " +success/100.0);
            survivalRates.add(success/100.0);

        }
        endTime = System.nanoTime();
        duration = (endTime - startTime)/(long)Math.pow(10,9);
        System.out.println("Agent Two Total: " + duration);
        return survivalRates;

    }
    static ArrayList runAgentThree(int ghosts){
        long startTime = System.nanoTime();
        long endTime;
        long duration;
        Maze maze = new Maze();
        ArrayList<Double> survivalRates = new ArrayList<>();
//      how many ghosts spawn, up to x ghosts
        for (int num =0; num < ghosts; num++) {
            int success = 0;
            System.out.println("Ghosts #" + num);
//          how many mazes are made to solve for num number of ghosts
            for(int iter = 1; iter <= 100; iter++ ) {
                char[][] m = maze.generateMaze();
                if(maze.visited.contains(List.of(0, 0))) {
                    if (Agents.agentThree(num, m, maze.visited, maze.small)) {
                        success += 1;

                    }
                    endTime = System.nanoTime();
                    duration = (endTime - startTime)/(long)Math.pow(10,9);
                    System.out.println("Iter #" + iter + "; Success: " + success +"; Time: " + duration);
                }
            }
            endTime = System.nanoTime();
            duration = (endTime - startTime)/(long)Math.pow(10,9);
            System.out.println("Run #" + num+ ": " +success/100.0+"; Time: " + duration);
            survivalRates.add(success/100.0);

        }
        return survivalRates;

    }
//    static ArrayList runAgentFour(int ghosts){
//        Maze maze = new Maze();
//        ArrayList<Double> survivalRates = new ArrayList<>();
////      how many ghosts spawn, up to 100 ghosts
//        for (int num = 0; num <= ghosts; num++) {
//            int success = 0;
////          how many mazes are made to solve for num number of ghosts
//            for(int iter = 1; iter <= 100; iter++ ) {
//                char[][] m = maze.generateMaze();
//                if(maze.visited.contains(List.of(maze.maze.length-1, maze.maze[0].length-1))) {
//                    if (Agents.agentFour(num, m, maze.visited)) {
//                        success += 1;
//                    }
//                }
//            }
//            survivalRates.add(success/100.0);
//
//        }
//        return survivalRates;
//
//    }
//    static ArrayList runAgentFive(int ghosts){
//        Maze maze = new Maze();
//        ArrayList<Double> survivalRates = new ArrayList<>();
////      how many ghosts spawn, up to 100 ghosts
//        for (int num = 0; num <= ghosts; num++) {
//            int success = 0;
////          how many mazes are made to solve for num number of ghosts
//            for(int iter = 1; iter <= 100; iter++ ) {
//                char[][] m = maze.generateMaze();
//                if(maze.visited.contains(List.of(maze.maze.length-1, maze.maze[0].length-1))) {
//                    if (Agents.agentFive(num, m, maze.visited)) {
//                        success += 1;
//                    }
//                }
//            }
//            survivalRates.add(success/100.0);
//
//        }
//        return survivalRates;
//
//    }


    public static void main(String[] args) {


//      maze Object created
        Maze maze = new Maze();
        int count = 0;

//      adds valid mazes to a stack of mazes
        long startTime = System.nanoTime();
        maze.generateMaze();
        maze.verifyMaze();
//        printMaze(maze.maze);


        char[][]m = maze.generateMaze();
        HashSet<List<Integer>> v = maze.verifyMaze();
        while (!v.contains(List.of(0, 0))){
            m = maze.generateMaze();
            v = maze.verifyMaze();
            break;

        }
//        System.out.println(v.toString());
//        printMaze(m);
//        for(int x = 0; x < m.length; x++) {
//            for (int y = 0; y < m.length; y++)
//                System.out.print(maze.small.get(List.of(x, y))+ " ");
//            System.out.println();
//        }


 //        Agents.agentOne(30,m , v);
//        System.out.println("Start:");
//        printMaze(m);
////        Agents.agentTwo(5, m,v, 0 , 0, null);
//        Agents.agentThree(1, m , v, maze.small);
//        System.out.println("End:");
//        printMaze(m);

        ArrayList<Double> agentOneSurvivalRates = runAgentOne(100);
////        int sumOne = 0;
//        ArrayList<Double> agentTwoSurvivalRates = runAgentTwo(100);
        ArrayList<Double> agentThreeSurvivalRates = runAgentThree(100);
        for (int x = 0; x < agentOneSurvivalRates.size(); x++) {
            System.out.println("Agent One #" + x + ":\t" + agentOneSurvivalRates.get(x));
//            sumOne += agentOneSurvivalRates.get(x);
//            System.out.println("Agent Two:\t" + agentTwoSurvivalRates.get(x));
            System.out.println("Agent Three:\t" + agentThreeSurvivalRates.get(x));
            System.out.println();
        }
//        System.out.println("Avg a1: " + (double)sumOne/100);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/(long)Math.pow(10,9);
        System.out.println(duration);


    }
}