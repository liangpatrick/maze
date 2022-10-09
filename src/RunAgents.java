import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RunAgents extends Thread{
    int ghosts;
    char [][] maze;
    HashMap<List<Integer>, Integer> visited;
    int start;
    int end;
    Agents.index[] agent2;
    static double[] survivalRatesOne = new double[101];
    static double[] survivalRatesTwo = new double[101];
    static double[] survivalRatesThree = new double[101];

    public RunAgents(int ghosts, int start, int end, Agents.index[] agent2){
        this.ghosts = ghosts;
        this.start = start;
        this.end = end;
        this.agent2 = agent2;
    }
    @Override
    public void run(){
        Maze maze = new Maze();
        long startTime = System.nanoTime();
        long endTime;
        long duration;
        for(int iter = 1; iter <= 100; iter++ ) {
            char[][] m = maze.generateMaze();
            if(Agents.agentOne(ghosts, m, maze.visited, start, end, agent2)){
                survivalRatesOne[ghosts] += 1;
            }
            m = maze.generateMaze();
            if(Agents.agentTwo(ghosts, m, maze.visited, start, end, agent2)){
                survivalRatesTwo[ghosts] += 1;
            }
//            m = maze.generateMaze();
//            if(Agents.agentThree(ghosts, m, maze.visited, start, end, agent2)){
//                survivalRatesThree[ghosts] += 1;
//            }

        }
        endTime = System.nanoTime();
        duration = (endTime - startTime)/(long)Math.pow(10,9);
        System.out.println("Ghosts #" + ghosts + "Agents Time: " + duration);
//        System.out.println("a1 ghosts #" +  ghosts + ": " +survivalRatesOne[ghosts]/100);
//        System.out.println("a2 ghosts #" +  ghosts + ": " +survivalRatesTwo[ghosts]/100);
//        System.out.println("a3 ghosts #" +  ghosts + ": " +survivalRatesThree[ghosts]/100);





    }

    public static void running(int ghosts) throws InterruptedException {
        long startTime = System.nanoTime();
        long endTime;
        long duration;


//      how many ghosts spawn, up to 100 ghosts
        List<Thread> threadList = new ArrayList<Thread>();
        Thread t = null;
        for (int num = 0; num <= ghosts; num++) {


            t = new RunAgents(num,0, 0, null);
            t.start();
            threadList.add(t);


        }
        for(Thread tt : threadList) {
            // waits for this thread to die
            tt.join();
        }
        endTime = System.nanoTime();
        duration = (endTime - startTime)/(long)Math.pow(10,9);
        System.out.println("Finished Time: " + duration);
        for( int x = 0; x < 100; x++) {
            System.out.println("A1 ghosts #" +  x + ": " +survivalRatesOne[x]/100);
            System.out.println("A2 ghosts #" +  x + ": " +survivalRatesTwo[x]/100);
            System.out.println("A3 ghosts #" +  ghosts + ": " +survivalRatesThree[ghosts]/100);
            System.out.println();
        }

    }

    public static void main(String args[]) throws InterruptedException {
        running(100);


    }





}
