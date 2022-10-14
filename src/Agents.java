import java.util.*;
import static java.util.Comparator.comparingInt;

public class Agents {
//  class is replacing a tuples functionality
    static class index{
        index prev;
        int x;
        int y;
        int f;
        int g;
        int h;
        int distance;
//      this one is to create a normal index object
        public index(int x, int y){
            this.x = x;
            this.y = y;
            this.g = 0;
            this.h = 0;
            this.f = 0;
        }
//      used when creating connected component and for comparisons
        public index(int x, int y, int distance){
            this.x = x;
            this.y = y;
            this.distance = distance;
        }
//      used for fringe
        public index(int x, int y, index prev){
            this.x = x;
            this.y = y;
            this.prev = prev;
            this.g = 0;
            this.h = 0;
            this.f = 0;
        }
//        used for priority queue
        int getF(){
            return f;
        }
//        used for ghost priority queue
        int getDistance(){
            return distance;
        }


    }
    // used to automate find neighbors
    private final static int[] row = { -1, 0, 0, 1 };
    private final static int[] col = { 0, -1, 1, 0 };

    static boolean agentOne(int num, char [][] maze, HashMap<List<Integer>, Integer> connectedComponent){
//        don't want reference to be linked in any way to maze
        char [][] reference = new char[maze.length][maze.length];
        for(int i = 0; i < maze.length; i++)
            for(int j = 0; j < maze.length; j++)
                reference[i][j] = maze[i][j];

//      stores path
        int start = 0, end = 0;
        List<index> path = findPath(start, end, maze, connectedComponent);
//      array of index objects that store ghost positions so i can both accurately move them and keep track in case there is overlap
        PriorityQueue<index> ghosts = addGhosts(num, maze, connectedComponent);
        for (index curr: path){
//            if agent moves into ghost, its dead
            if(maze[curr.x][curr.y] == '*')
                return false;
            else
                maze[curr.x][curr.y] = 'A';
//            if its the first move, then the beginning doesn't matter
            if (curr.prev != null){
                maze[curr.prev.x][curr.prev.y] = reference[curr.prev.x][curr.prev.y];
            }
            moveGhosts(ghosts, reference, maze);
//          this means ghosts have moved into same cell and the dude is dead
            if (maze[curr.x][curr.y] != 'A'){
                return false;

            }
        }

        return true;


    }
//    agentTwo needs more parameters because agentThree calls it
    static boolean agentTwo(int num, char [][] maze, HashMap<List<Integer>, Integer> connectedComponent, int s, int e, PriorityQueue<index> ghost, char[][] ref){
//        don't want reference to be linked in any way to maze
        char[][] reference = new char[maze.length][maze.length];
        if (ref == null) {
            for(int i = 0; i < maze.length; i++)
                for(int j = 0; j < maze.length; j++)
                    reference[i][j] = maze[i][j];
        } else {
            reference = ref;
        }
//        will be 0,0 unless called by agent3
        int start = s, end = e;

//      priority queue of index objects that store ghost positions so i can both accurately move them and keep track in case there is overlap; stored in priority queue to speed up look up
        PriorityQueue<index> ghosts = null;
        if (ghost != null){
//            copyGhosts so there is no link between ghosts and ghost(param)
            ghosts = copyGhosts(ghost);
        }

//      searches until either agent is dead or reaches the end of the maze
        while(maze[maze.length-1][maze.length-1] != 'A') {
//          stores path
            List<index> path = findPath(start, end, maze, connectedComponent);
//            only activates when called by agent3
            if (ghosts == null) {
                ghosts = addGhosts(num, maze, connectedComponent);
//                System.out.println("here");
            }

//          no path
            if (path == null) {
                index closestGhost = ghosts.peek();
//              if no valid move is found, then agent just remains in place
                int tempStart = start, tempEnd = end;
                index pos = moveAway(tempStart, tempEnd, start, end, closestGhost, maze, connectedComponent);
                tempStart = pos.x;
                tempEnd = pos.y;
                //            if agent moves into ghost, its dead
                if(maze[tempStart][tempEnd] == '*')
                    return false;
                else {
                    maze[start][end] = reference[start][end];
                    start = tempStart;
                    end = tempEnd;
                    maze[start][end] = 'A';
                }
//              next four lines of code are to move the agent while also ensuring if the agent doesn't move the agent appears correctly


                moveGhosts(ghosts, reference, maze, start, end);
//              this means ghosts have moved into same cell and the dude is dead
                if (maze[start][end] != 'A') {
                    return false;
                }
            } else {
//              get first index instead of zeroeth since zeroeth index is just the starting point
                index curr;
                if (path.size() > 1) {
                    curr = path.get(1);
                } else {
                    curr = path.get(0);
                }
                if(maze[curr.x][curr.y] == '*')
                    return false;
                else {
                    maze[curr.x][curr.y] = 'A';
                    start = curr.x;
                    end = curr.y;
                }

//
                if (curr.prev != null) {
                    maze[curr.prev.x][curr.prev.y] = reference[curr.prev.x][curr.prev.y];
                }
                moveGhosts(ghosts, reference, maze, start, end);
//              this means ghosts have moved into same cell and the dude is dead
                if (maze[curr.x][curr.y] != 'A') {
                    return false;
                }
            }
        }
        return true;

    }
    static boolean agentThree(int num, char [][] maze, HashMap<List<Integer>, Integer> connectedComponent) {
        char[][] reference = new char[maze.length][maze.length];
        for(int i = 0; i < maze.length; i++)
            for(int j = 0; j < maze.length; j++) {

                reference[i][j] = maze[i][j];
            }

        int start = 0, end = 0;
//      array of index objects that store ghost positions so i can both accurately move them and keep track in case there is overlap
        PriorityQueue<index> ghosts = addGhosts(num, maze, connectedComponent);
        int[] row = {0, -1, 0, 0, 1};
        int[] col = {0, 0, -1, 1, 0};
//      simulates until either agent is dead or reaches the end of the maze
//      if all odds are equal, tie break goes to shortest cell
        while(maze[maze.length-1][maze.length-1] != 'A'){
//          new positions which are updated when max utility is found
            int newX = start, newY = end;
//            System.out.println(newX + ", " + newY + "; " + connectedComponent.get(List.of(newX, newY)));
//          used to store max utility; has to be negative so the 0 first move can always be added
            int bigUtil = -1;
//          goes through each possible move and then simulates
            for (int i = 0; i < row.length; i++) {
                int currX = 0;
                int currY = 0;
                currX = start + row[i];
                currY = end + col[i];
//              checks if move is valid
                if (connectedComponent.containsKey(List.of(currX, currY)) && maze[currX][currY] != '*') {
                    int currUtil = 0;
//                  simulates agentTwo
//                    System.out.println("simul");
                    for(int count = 0; count < 8; count++){
//                      make duplicate so nothing changes
                        char[][] temp = new char[maze.length][];
                        for (int x = 0; x < maze.length; x++)
                            temp[x] = maze[x].clone();
                        PriorityQueue<index> g = copyGhosts(ghosts);
//                      if successful, utility increases
                        if (agentTwo(num, temp, connectedComponent,currX,currY, g, reference)) {
                            currUtil += 1;
                        }
                    }
//                  gets max utility
//                    System.out.println(bigUtil + " u " + currUtil);
                    if (bigUtil < currUtil){
                        newX = currX;
                        newY = currY;
                        bigUtil = currUtil;
                    }
                    else if (bigUtil == currUtil){
//                        calculate a tie breaker based on shortest path; use hashmap set to decide!!!!!
                        if(connectedComponent.get(List.of(currX, currY)) < connectedComponent.get(List.of(newX, newY))){

                            newX = currX;
                            newY = currY;
                            bigUtil = currUtil;

                        }
                        else if (connectedComponent.get(List.of(currX, currY)) == connectedComponent.get(List.of(newX, newY))){
//                            biased towards down/right
                            if (currX > newX || currY > newY){
                                newX = currX;
                                newY = currY;
                                bigUtil = currUtil;
                            }
                        }
                    }
//                    System.out.println(bigUtil + " X " + currUtil);
                }

            }
//          means no path
//            agentTwo stuff
            if (bigUtil == 0){
//              gets index of closest ghost
                index closestGhost = ghosts.peek();
                int tempStart = start, tempEnd = end;
//              moves away from closestGhost
//                System.out.println("move");
                index pos = moveAway(tempStart, tempEnd, start, end, closestGhost, maze, connectedComponent);
                tempStart = pos.x;
                tempEnd = pos.y;
//              next four lines of code are to move the agent while also ensuring if the agent doesn't move the agent appears correctly
                maze[start][end] = reference[start][end];
                start = tempStart;
                end = tempEnd;
                maze[start][end] = 'A';
                moveGhosts(ghosts, reference, maze, start,end);
//                this means ghosts have moved into same cell and the dude is dead
                if (maze[start][end] != 'A') {
//System.out.println("deadb" + start + ", " + end);
//Maze.printMaze(maze);
 return false;
                }
            } else {
                maze[start][end] = reference[start][end];
                start = newX;
                end = newY;
                maze[start][end] = 'A';
                moveGhosts(ghosts, reference, maze, start, end);
//              this means ghosts have moved into same cell and the dude is dead
                if (maze[start][end] != 'A') {
//System.out.println("dead" + start + ", " + end);
//Maze.printMaze(maze);

                    return false;
                }
            }

        }
//        System.out.println("alive");
        return true;
    }

    static boolean agentFour(int num, char [][] maze, HashMap<List<Integer>, Integer> connectedComponent){
        char [][] reference = new char[maze.length][maze.length];
        for(int i = 0; i < maze.length; i++)
            for(int j = 0; j < maze.length; j++)
                reference[i][j] = maze[i][j];
//        will be 0,0 unless called by agent3
        int start = 0, end = 0;
//      array of index objects that store ghost positions so i can both accurately move them and keep track in case there is overlap
        PriorityQueue<index> ghosts = null;
//        List<index> path = findPath4(start, end, maze, connectedComponent, ghosts);
//      simulates until either agent is dead or reaches the end of the maze
        while(maze[maze.length-1][maze.length-1] != 'A') {
//          stores path
            List<index> path = findPath4(start, end, maze, connectedComponent, ghosts);
            if (ghosts == null) {
                ghosts = addGhosts(num, maze, connectedComponent);
            }
//          no path
            if (path == null) {
                index closestGhost = ghosts.peek();
//              if no valid move is found, then agent just remains in place
                int tempStart = start, tempEnd = end;
                index pos = moveAway(tempStart, tempEnd, start, end, closestGhost, maze, connectedComponent);
                tempStart = pos.x;
                tempEnd = pos.y;
//              next four lines of code are to move the agent while also ensuring if the agent doesn't move the agent appears correctly
                maze[start][end] = reference[start][end];
                start = tempStart;
                end = tempEnd;
                maze[start][end] = 'A';


                moveGhosts(ghosts, reference, maze, start, end);
                //          this means ghosts have moved into same cell and the dude is dead
                if (maze[start][end] != 'A') {
                    return false;
                }
            } else {
//              get first index instead of zeroeth since zeroeth index is just the starting point
                index curr;
                if (path.size() == 1) {
                    curr = path.get(0);
                    path.remove(0);
                } else {
                    curr = path.get(1);
                    path.remove(0);
                }
                if(maze[curr.x][curr.y] == '*')
                    return false;
                else {
                    maze[curr.x][curr.y] = 'A';
                    start = curr.x;
                    end = curr.y;
                }

                if (curr.prev != null) {
                    maze[curr.prev.x][curr.prev.y] = reference[curr.prev.x][curr.prev.y];
                }
                moveGhosts(ghosts, reference, maze, start, end);
//              this means ghosts have moved into same cell and the dude is dead
                if (maze[curr.x][curr.y] != 'A') {
                    return false;
                }
            }
//
//
        }
//
//
        return true;


    }

    static boolean agentFive(int num, char [][] maze, HashMap<List<Integer>, Integer> connectedComponent){
        char [][] reference = new char[maze.length][maze.length];
        for(int i = 0; i < maze.length; i++)
            for(int j = 0; j < maze.length; j++)
                reference[i][j] = maze[i][j];
        int start = 0, end = 0;
//      array of index objects that store ghost positions so i can both accurately move them and keep track in case there is overlap
        index[] ghosts = null;
        index[] lastSeen = null;

//      simulates until either agent is dead or reaches the end of the maze
        while(maze[maze.length-1][maze.length-1] != 'A')
        {
//            Maze.printMaze(maze);


//          stores path
            List<index> path = findPath5(start, end, maze, connectedComponent, lastSeen);
            if (ghosts == null) {
                ghosts = addGhosts5(num, maze, connectedComponent);
                lastSeen = copyGhosts(ghosts);
            }
//            for (int x = 0; x < ghosts.length; x ++){
//                System.out.println(ghosts[x].x + ", " + ghosts[x].y + "; " + lastSeen[x].x + ", " + lastSeen[x].y );
//            }
//            System.out.println(ghosts.toString());
//            System.out.println(lastSeen.toString());
//          no path
            if (path == null){
                int ind = indMin(ghosts);
                index closestGhost = lastSeen[ind];
//              if no valid move is found, then agent just remains in place
//                Maze.printMaze(maze);
//
                int tempStart = start, tempEnd = end;
//                System.out.println(tempStart + ", " + tempEnd);
                index pos = moveAway(tempStart, tempEnd, start, end, closestGhost, maze, connectedComponent);
                tempStart = pos.x;
                tempEnd = pos.y;
//              next four lines of code are to move the agent while also ensuring if the agent doesn't move the agent appears correctly
                maze[start][end] = reference[start][end];
                start = tempStart;
                end = tempEnd;
                maze[start][end] = 'A';

//                Maze.printMaze(maze);
//                if (true) {
//                    System.out.println("hello");
//                    return false;
//                }
                moveGhosts(lastSeen, ghosts, reference, maze, start, end);

                //          this means ghosts have moved into same cell and the dude is dead
                if (maze[start][end] != 'A') {
                    return false;
                }
            } else {
//              get first index instead of zeroeth since zeroeth index is just the starting point
                index curr;
                if (path.size() == 1) {
                    curr = path.get(0);
                    path.remove(0);
                } else {
                    curr = path.get(1);
                    path.remove(0);
                }
                if(maze[curr.x][curr.y] == '*')
                    return false;
                else {
                    maze[curr.x][curr.y] = 'A';
                    start = curr.x;
                    end = curr.y;
                }

                if (curr.prev != null) {
                    maze[curr.prev.x][curr.prev.y] = reference[curr.prev.x][curr.prev.y];
                }
                moveGhosts(lastSeen, ghosts, reference, maze, start, end);
//              this means ghosts have moved into same cell and the dude is dead
                if (maze[curr.x][curr.y] != 'A') {
                    return false;
                }
            }


        }


        return true;




    }






    private static List<index> findPath(int start, int end, char[][] maze, HashMap<List<Integer>, Integer> connectedComponent){
//      fringe to store cells that need to be visited
        PriorityQueue<index> fringe = new PriorityQueue<>(comparingInt(index::getF));
        HashMap<List<Integer>, Integer> visited = new HashMap<>();
//      used to verify if end source has been achieved
        int endX = maze.length-1, endY = maze[0].length-1;
//      add beginning cell to fringe and visited; aka open and close
        fringe.add(new index(start,end, null));
        visited.put(List.of(start,end), getDistance(start,end, maze.length-1, maze.length-1));

        while(!fringe.isEmpty()) {
//          use poll instead of remove so no errors are thrown
            index curr = fringe.poll();

//          indX and indY hold current positions, didn't want to keep using curr.x or curr.y for laziness sakes
            int indX = curr.x, indY = curr.y;
//          if arrived at destination
            if (indX == endX && indY == endY) {
                List<index> path = new ArrayList<>();;
                getPath(curr, path);
                return path;
            }

//          generates neighbors
            for (int i = 0; i < row.length; i++) {
                int currX = indX + row[i];
                int currY = indY + col[i];

//                checks if move is Valid
                if (connectedComponent.containsKey(List.of(currX, currY)) && maze[currX][currY] != '*') {

                    int tempG = curr.g + 1;
//                    the heuristic is simply the distance from current cell to the end cell
                    int tempH = connectedComponent.get(List.of(currX, currY));
                    int tempF = tempH + tempG;

                    index temp = new index(currX, currY, curr);
//                    calculates heuristic values
                    temp.g = tempG;
                    temp.h = tempH;
                    temp.f = tempF;

//                    if the closed list contains the current node, check its value against the previous f value associated with the cell
                    if(visited.containsKey(List.of(currX, currY)) ){
//                        if the previous iteration of the thing has a larger f, get rid of it
                       if(tempF < visited.get(List.of(currX,currY))){
                           fringe.remove(temp);
                       }
                       else
                            continue;

                    }
                    visited.put(List.of(currX, currY),temp.f);
                    fringe.add(temp);
                }
            }
            visited.put(List.of(indX, indY), curr.f);

        }
        return null;
    }

//   used in findPath4 so that its based on distance away from nearest ghost
    static int calcH(int currX, int currY, PriorityQueue<index> g){
        if (g == null){
            return 0;
        }
        int closestDistance = Integer.MAX_VALUE;
        for(index ghost: g){
            closestDistance = Math.min(getDistance(currX, currY, ghost.x, ghost.y), closestDistance);
        }
//        returns a negative value so that when g(cell value from start to curr) + h, the bigger positive h is the smaller f will become which means it will be selected first
        return -closestDistance;
    }
//    only difference is the heuristic
    private static List<index> findPath4(int start, int end, char[][] maze, HashMap<List<Integer>, Integer> connectedComponent, PriorityQueue<index> g){
//      fringe to store cells that need to be visited
        PriorityQueue<Agents.index> fringe = new PriorityQueue<>(comparingInt(index::getF));
        HashMap<List<Integer>, Integer> visited = new HashMap<>();
//      used to verify if end source has been achieved
        int endX = maze.length-1, endY = maze[0].length-1;
//      add beginning cell to fringe and visited; aka open and close
        fringe.add(new index(start,end, null));
        visited.put(List.of(start,end), getDistance(start,end, maze.length-1, maze.length-1));

        while(!fringe.isEmpty()) {
//          use poll instead of remove so no errors are thrown
            index curr = fringe.poll();

//          indX and indY hold current positions, didn't want to keep using curr.x or curr.y for laziness sakes
            int indX = curr.x, indY = curr.y;
//          if arrived at destination
            if (indX == endX && indY == endY) {
                List<index> path = new ArrayList<>();;
                getPath(curr, path);
                return path;
            }

//          generates neighbors
            for (int i = 0; i < row.length; i++) {
                int currX = indX + row[i];
                int currY = indY + col[i];
//                bounds check

                if (connectedComponent.containsKey(List.of(currX, currY)) && maze[currX][currY] != '*') {

                    int tempG = curr.g + 1;
                    int tempH = calcH(currX, currY, g);
                    int tempF = tempH + tempG;

                    index temp = new index(currX, currY, curr);
                    temp.g = tempG;
                    temp.h = tempH;
                    temp.f = tempF;
//                    if(fringe.contains(new index ))
                    if(visited.containsKey(List.of(currX, currY)) ){
//                        if the previous iteration of the thing has a larger f, get rid of it
                        if(tempF < visited.get(List.of(currX,currY))){
                            fringe.remove(temp);
                        }
                        else
                            continue;

                    }
                    visited.put(List.of(currX, currY),temp.f);
                    fringe.add(temp);
                }
            }
            visited.put(List.of(indX, indY), curr.f);

        }
        return null;
    }

//    static int calcH(int currX, int currY, index[] g, char[][]reference){
//        if (g == null){
//            return 0;
//        }
//        int closestDistance = Integer.MAX_VALUE;
////        ignores if ghost is in a wall, instead stays away from walls
//        for(index ghost: g){
//            if(reference[ghost.x][ghost.y] != '#')
//                closestDistance = Math.min(getDistance(currX, currY, ghost.x, ghost.y), closestDistance);
//
//        }
////        returns a negative value so that when g(cell value from start to curr) + h, the bigger positive h is the smaller f will become which means it will be selected first
//        return -closestDistance;
//    }

    static int calcH(int currX, int currY, index[] g){
        if (g == null){
            return 0;
        }
        int closestDistance = Integer.MAX_VALUE;
        for(index ghost: g){
            closestDistance = Math.min(getDistance(currX, currY, ghost.x, ghost.y), closestDistance);
        }
//        returns a negative value so that when g(cell value from start to curr) + h, the bigger positive h is the smaller f will become which means it will be selected first
        return -closestDistance;
    }

    private static List<index> findPath5(int start, int end, char[][] maze, HashMap<List<Integer>, Integer> connectedComponent, index[] g){
//      fringe to store cells that need to be visited
        PriorityQueue<Agents.index> fringe = new PriorityQueue<>(comparingInt(index::getF));
        HashMap<List<Integer>, Integer> visited = new HashMap<>();
//      used to verify if end source has been achieved
        int endX = maze.length-1, endY = maze[0].length-1;
//      add beginning cell to fringe and visited; aka open and close
        fringe.add(new index(start,end, null));
        visited.put(List.of(start,end), getDistance(start,end, maze.length-1, maze.length-1));

        while(!fringe.isEmpty()) {
//          use poll instead of remove so no errors are thrown
            index curr = fringe.poll();

//          indX and indY hold current positions, didn't want to keep using curr.x or curr.y for laziness sakes
            int indX = curr.x, indY = curr.y;
//          if arrived at destination
            if (indX == endX && indY == endY) {
                List<index> path = new ArrayList<>();;
                getPath(curr, path);
                return path;
            }

//          generates neighbors
            for (int i = 0; i < row.length; i++) {
                int currX = indX + row[i];
                int currY = indY + col[i];
//                bounds check

                if (connectedComponent.containsKey(List.of(currX, currY)) && maze[currX][currY] != '*') {

                    int tempG = curr.g + 1;
                    int tempH = calcH(currX, currY, g);
                    int tempF = tempH + tempG;

                    index temp = new index(currX, currY, curr);
                    temp.g = tempG;
                    temp.h = tempH;
                    temp.f = tempF;
//                    if(fringe.contains(new index ))
                    if(visited.containsKey(List.of(currX, currY)) ){
//                        if the previous iteration of the thing has a larger f, get rid of it
                        if(tempF < visited.get(List.of(currX,currY))){
                            fringe.remove(temp);
                        }
                        else
                            continue;

                    }
                    visited.put(List.of(currX, currY),temp.f);
                    fringe.add(temp);
                }
            }
            visited.put(List.of(indX, indY), curr.f);

        }
        return null;
    }





//  uses recursion to get to the oldest node and then proceeds to add the rest of the nodes to the path list.
    private static void getPath(index node, List<index> path){
        if (node != null){
            getPath(node.prev, path);
            path.add(node);

        }
    }

    static index moveAway(int tempStart, int tempEnd, int start, int end, index closestGhost, char[][] maze, HashMap<List<Integer>, Integer> connectedComponent){
        for (int i = 0; i < row.length; i++) {
            int currX = start + row[i];
            int currY = end + col[i];

//          checks if move is valid
            if (connectedComponent.containsKey(List.of(currX, currY)) && maze[currX][currY] != '*') {
//                if current cell is farther away, update new position
                if(getDistance(closestGhost.x, closestGhost.y, currX, currY) > getDistance(closestGhost.x, closestGhost.y, tempStart, tempEnd)){
                    tempStart = currX;
                    tempEnd = currY;
                }

            }

        }
        return new index(tempStart, tempEnd);
    }

    //  adds param number of ghosts to the maze and at random locations
    static PriorityQueue<index> addGhosts(int num, char[][] maze, HashMap<List<Integer>, Integer> connectedComponent){
        PriorityQueue<index> list = new PriorityQueue<>(comparingInt(index::getDistance));
//      gets random pair in maze
        while (num > 0) {
//            math random
            int indx = (int) Math.floor(Math.random() * maze.length), indy = (int) Math.floor(Math.random() * maze[0].length);
//          checks if its valid
            if (connectedComponent.containsKey(List.of(indx, indy))) {
                maze[indx][indy] = '*';
                list.add(new index(indx, indy, 0));
                num -= 1;
            }
        }
        return list;
    }
//    makes a new ghosts pqueue
    static PriorityQueue<index> copyGhosts(PriorityQueue<index> ghost){
        PriorityQueue<index> arr = new PriorityQueue<>(comparingInt(index::getDistance));
        for(index g: ghost){
            arr.add(new index(g.x, g.y, g.distance));
        }
        return arr;
    }
//  gets manhattan distance
    static int getDistance(int ghostX, int ghostY, int agentX, int agentY){
        return Math.abs(agentY- ghostY) + Math.abs(agentX- ghostX);
    }

//  iterates through array of ghosts to move said ghosts
    static void moveGhosts(PriorityQueue<index> g, char[][] reference, char[][] maze, int agentX, int agentY){
        for (index ghost: g){
//          randomly decide if current ghost is going up/down left/right
            int ind = (int) Math.floor(Math.random() * row.length);
            int newX = ghost.x + row[ind], newY = ghost.y + col[ind];
//          checks bounds
            if ((0 <= newX && newX < maze.length) && (0 <= newY && newY < maze[0].length)) {
//          checks against reference because that way walls are never missing; if reference is a wall then 50/50 chance to move into it or stay in original position
                if (reference[newX][newY] == '#') {
                    double chance = Math.random();
                    if (chance < .5) {
                        maze[newX][newY] = '*';
//                        if (distance != null) {
                            ghost.distance = getDistance(newX, newY, agentX, agentY);
//                            System.out.println(distance[x]);
//                        }
//                      basically reverts previous ghost cell to whatever it should be; doesn't matter if multiple ghosts in same cell because ghosts are stored in ghosts array.
                        maze[ghost.x][ghost.y] = reference[ghost.x][ghost.y];
                        ghost.x = newX;
                        ghost.y = newY;
                    } else {
//                  have to do this in case there are multiple ghosts in the same cell
                        maze[ghost.x][ghost.y] = '*';
                    }

                } else {
                    maze[newX][newY] = '*';
                    maze[ghost.x][ghost.y] = reference[ghost.x][ghost.y];
//                    if (distance != null){
                    ghost.distance = getDistance(newX, newY, agentX, agentY);
//                        System.out.println(distance[x]);
//                    }
                    ghost.x = newX;
                    ghost.y = newY;
                }
            }
        }

    }
    static void moveGhosts(PriorityQueue<index> g, char[][] reference, char[][] maze){
        for (index ghost: g){
//          randomly decide if current ghost is going up/down left/right
            int ind = (int) Math.floor(Math.random() * row.length);
            int newX = ghost.x + row[ind], newY = ghost.y + col[ind];
//          checks bounds
            if ((0 <= newX && newX < maze.length) && (0 <= newY && newY < maze[0].length)) {
//          checks against reference because that way walls are never missing; if reference is a wall then 50/50 chance to move into it or stay in original position
                if (reference[newX][newY] == '#') {
                    double chance = Math.random();
                    if (chance < .5) {
                        maze[newX][newY] = '*';

//                      basically reverts previous ghost cell to whatever it should be; doesn't matter if multiple ghosts in same cell because ghosts are stored in ghosts array.
                        maze[ghost.x][ghost.y] = reference[ghost.x][ghost.y];
                        ghost.x = newX;
                        ghost.y = newY;
                    } else {
//                  have to do this in case there are multiple ghosts in the same cell
                        maze[ghost.x][ghost.y] = '*';
                    }

                } else {
                    maze[newX][newY] = '*';
                    maze[ghost.x][ghost.y] = reference[ghost.x][ghost.y];

                    ghost.x = newX;
                    ghost.y = newY;
                }
            }
        }

    }


    static index[] addGhosts5(int num, char[][] maze, HashMap<List<Integer>, Integer> connectedComponent){
        index[] list = new index[num];
//      gets random pair in maze
        while (num > 0) {
            int indx = (int) Math.floor(Math.random() * maze.length), indy = (int) Math.floor(Math.random() * maze[0].length);
            if (connectedComponent.containsKey(List.of(indx, indy))) {
                maze[indx][indy] = '*';
                list[list.length - num] = new index(indx, indy);
                num -= 1;
            }
        }
        return list;
    }






    static void moveGhosts(index[] lastSeen, index[] g, char[][] reference, char[][] maze, int agentX, int agentY){
        for (int x = 0; x < g.length; x ++){
            index ghost = g[x];
//          randomly decide if current ghost is going up/down left/right
            int ind = (int) Math.floor(Math.random() * row.length);
            int newX = ghost.x + row[ind], newY = ghost.y + col[ind];
//          checks bounds
            if ((0 <= newX && newX < maze.length) && (0 <= newY && newY < maze[0].length)) {
//          checks against reference because that way walls are never missing; if reference is a wall then 50/50 chance to move into it or stay in original position
                if (reference[newX][newY] == '#') {
                    double chance = Math.random();
                    if (chance < .5) {
                        maze[newX][newY] = '*';
//                        if (distance != null) {
                        ghost.distance = getDistance(newX, newY, agentX, agentY);
//                            System.out.println(distance[x]);
//                        }
//                      basically reverts previous ghost cell to whatever it should be; doesn't matter if multiple ghosts in same cell because ghosts are stored in ghosts array.
                        maze[ghost.x][ghost.y] = reference[ghost.x][ghost.y];
                        ghost.x = newX;
                        ghost.y = newY;
                    } else {
//                  have to do this in case there are multiple ghosts in the same cell
//                        maze[ghost.x][ghost.y] = '*';
                    }

                } else {
                    lastSeen[x].x = newX;
                    lastSeen[x].y = newY;
                    maze[newX][newY] = '*';
                    maze[ghost.x][ghost.y] = reference[ghost.x][ghost.y];
//                    if (distance != null){
                    ghost.distance = getDistance(newX, newY, agentX, agentY);
//                        System.out.println(distance[x]);
//                    }
                    ghost.x = newX;
                    ghost.y = newY;
                }
            }
        }

    }

    //  gets index of smallest thing in ghost distances
    static int indMin(index[] distances){
        int ind = 0;
        int min = distances[0].getDistance();

        for (int x = 1; x < distances.length; x++){
            if (distances[x].getDistance() <= min){
                min = distances[x].getDistance();
                ind = x;
            }
        }
        return ind;
    }
    static index[] copyGhosts(index[] g){
        index[] arr = new index[g.length];
        for(int x = 0; x < g.length; x++){
            arr[x] = new index(g[x].x, g[x].y);
        }
        return arr;
    }

}
