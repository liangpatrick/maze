import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RunAgents extends Thread{
    int ghosts;
    HashMap<List<Integer>, Integer> visited;
    static double[] survivalRatesOne = new double[101];
    static double[] survivalRatesTwo = new double[101];
    static double[] survivalRatesThree = new double[101];
    static double[] survivalRatesFour = new double[101];

//    initialize ghosts to run threads
    public RunAgents(int ghosts){
        this.ghosts = ghosts;
    }
    @Override
    public void run(){
        Maze maze = new Maze();
        long startTime = System.nanoTime();
        long endTime;
        long duration;
        for(int iter = 1; iter <= 100; iter++ ) {
            char[][] m = maze.generateMaze(51);
            if(Agents.agentOne(ghosts, m, maze.visited,0, 0, null)){
                survivalRatesOne[ghosts] += 1;
            }
            m = maze.generateMaze(51);
            if(maze.visited.isEmpty())
                System.out.println("fuck??");
            if(Agents.agentTwo(ghosts, m, maze.visited,0, 0, null)){
                survivalRatesTwo[ghosts] += 1;
            }
            m = maze.generateMaze(51);
            if(Agents.agentThree(ghosts, m, maze.visited,0, 0, null)){
                survivalRatesThree[ghosts] += 1;
            }
//            m = maze.generateMaze();
//            if(Agents.agentFour(ghosts, m, maze.visited,0, 0, null)){
//                survivalRatesFour[ghosts] += 1;
//            }

        }
        endTime = System.nanoTime();
        duration = (endTime - startTime)/(long)Math.pow(10,9);
        System.out.println("Ghosts #" + ghosts + "; Agents Time: " + duration);
        System.out.println("a1 ghosts #" +  ghosts + ": " +survivalRatesOne[ghosts]/100);
        System.out.println("a2 ghosts #" +  ghosts + ": " +survivalRatesTwo[ghosts]/100);
        System.out.println("a3 ghosts #" +  ghosts + ": " +survivalRatesThree[ghosts]/100);
//        System.out.println("a4 ghosts #" +  ghosts + ": " +survivalRatesFour[ghosts]/100);




    }
//  method that spawns all threads
    public static void running(int ghosts) throws InterruptedException {
//      used to keep track of time
        long startTime = System.nanoTime();
        long endTime;
        long duration;
//      how many ghosts spawn, up to 100 ghosts
        List<Thread> threadList = new ArrayList<Thread>();
        Thread t = null;
//      spawns all threads
        for (int num = 0; num <= ghosts; num++) {
            t = new RunAgents(num);
            t.start();
            threadList.add(t);
        }
        System.out.println("waiting");
        for(Thread tt : threadList) {
            // waits for this thread to die
            tt.join();
        }
        endTime = System.nanoTime();
        duration = (endTime - startTime)/(long)Math.pow(10,9);
        System.out.println("Finished Time: " + duration);
        for( int x = 0; x < 101; x++) {
            System.out.println("A1 ghosts #" +  x + ": " +survivalRatesOne[x]/100);
            System.out.println("A2 ghosts #" +  x + ": " +survivalRatesTwo[x]/100);
            System.out.println("A3 ghosts #" +  x + ": " +survivalRatesThree[x]/100);
//            System.out.println("A4 ghosts #" +  x + ": " +survivalRatesFour[x]/100);
//
//            System.out.println();
        }

    }

    public static void main(String args[]) throws InterruptedException {
        running(120);


    }





}
