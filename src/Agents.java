import java.util.*;
import java.util.prefs.PreferenceChangeEvent;

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
        int getF(){
            return f;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            var index = (index) o;
            return x == index.x &&
                    y == index.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

    }
    // used to automate find neighbors
    private final static int[] row = { -1, 0, 0, 1 };
    private final static int[] col = { 0, -1, 1, 0 };

    static boolean agentOne(int num, char [][] maze, HashMap<List<Integer>, Integer> connectedComponent, int d, int dd, index[] nulll){
        char [][] reference = new char[maze.length][maze.length];
        for(int i = 0; i < maze.length; i++)
            for(int j = 0; j < maze.length; j++)
                reference[i][j] = maze[i][j];
        reference[0][0] = ' ';
        reference[maze.length-1][maze.length-1] = ' ';

//      stores path
        int start = 0, end = 0;
        List<index> path = findPath(start, end, maze, connectedComponent, reference);
//      array of index objects that store ghost positions so i can both accurately move them and keep track in case there is overlap
        index[] ghosts = addGhosts(num, maze, connectedComponent);
        for (index curr: path){
//            if agent moves into ghost, its dead
            if(maze[curr.x][curr.y] == '*')
                return false;
            else
                maze[curr.x][curr.y] = 'A';

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
    static boolean agentTwo(int num, char [][] maze, HashMap<List<Integer>, Integer> connectedComponent, int s, int e, index[] ghost, char[][] ref){
        char[][] reference = new char[maze.length][maze.length];
        if (ref == null) {
            for(int i = 0; i < maze.length; i++)
                for(int j = 0; j < maze.length; j++)
                    reference[i][j] = maze[i][j];
            reference[0][0] = ' ';
            reference[maze.length-1][maze.length-1] = ' ';
        } else {
            reference = ref;
        }
//        will be 0,0 unless called by agent3
        int start = s, end = e;

//      array of index objects that store ghost positions so i can both accurately move them and keep track in case there is overlap
        index[] ghosts = null;
        if (ghost != null){
            ghosts = copyGhosts(ghost);
        }
        double[] distance = new double[num];
//      simulates until either agent is dead or reaches the end of the maze
        while(maze[maze.length-1][maze.length-1] != 'A') {

//          stores path
            List<index> path = findPath(start, end, maze, connectedComponent, reference);
//            only activates when called by agent3
            if (ghosts == null) {
                ghosts = addGhosts(num, maze, connectedComponent);
            }
//          no path
            if (path == null) {
                int ind = indMin(distance);
                index closestGhost = ghosts[ind];
//              if no valid move is found, then agent just remains in place
                int tempStart = start, tempEnd = end;
                index pos = moveAway(tempStart, tempEnd, start, end, closestGhost, maze, connectedComponent, reference);
                tempStart = pos.x;
                tempEnd = pos.y;
//              next four lines of code are to move the agent while also ensuring if the agent doesn't move the agent appears correctly
                maze[start][end] = reference[start][end];
                start = tempStart;
                end = tempEnd;
                maze[start][end] = 'A';

//                if(reference[start][end] == 'A') {
//                    System.out.println("s, e: " + start + ", " + end);
//                    System.out.println(reference[start][end]);
//
//                }
//                return false;
                moveGhosts(ghosts, reference, maze, distance, start, end);
                //          this means ghosts have moved into same cell and the dude is dead
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
                maze[curr.x][curr.y] = 'A';
                start = curr.x;
                end = curr.y;
//
                if (curr.prev != null) {
                    maze[curr.prev.x][curr.prev.y] = reference[curr.prev.x][curr.prev.y];
                }
                moveGhosts(ghosts, reference, maze, distance, start, end);
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
    static boolean agentThree(int num, char [][] maze, HashMap<List<Integer>, Integer> connectedComponent, int e, int ee, index[] nulll) {
        char[][] reference = new char[maze.length][maze.length];
        for(int i = 0; i < maze.length; i++)
            for(int j = 0; j < maze.length; j++) {

                reference[i][j] = maze[i][j];
            }
        reference[0][0] = ' ';
        reference[maze.length-1][maze.length-1] = ' ';
        int start = 0, end = 0;


//      array of index objects that store ghost positions so i can both accurately move them and keep track in case there is overlap
        index[] ghosts = addGhosts(num, maze, connectedComponent);
        double[] distance = new double[num];
        int[] row = {0, -1, 0, 0, 1};
        int[] col = {0, 0, -1, 1, 0};
//      simulates until either agent is dead or reaches the end of the maze
//      if all odds are equal, tie break goes to shortest cell
        while(maze[maze.length-1][maze.length-1] != 'A'){

//          new positions which are updated when max utility is found
            int newX = start, newY = end;
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
                    for(int count = 0; count < 8; count++){
//                      make duplicate so nothing changes
                        char[][] temp = new char[maze.length][];
                        for (int x = 0; x < maze.length; x++)
                            temp[x] = maze[x].clone();
                        index[] g = copyGhosts(ghosts);

//                      if successful, utility increases
                        if (agentTwo(num, temp, connectedComponent,currX,currY, g, reference)) {
                            currUtil += 1;
                        }
                    }
//                    System.out.println(bigUtil + "; " + currUtil);
//                  gets max utility
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
                            if (currX > newX || currY > newY){
                                newX = currX;
                                newY = currY;
                                bigUtil = currUtil;
                            }
                        }
                    }

//                    prevUtil = utility;


                }

            }
//          means no path
            if (bigUtil == 0){
//              gets index of closest ghost
                int ind = indMin(distance);
                index closestGhost = ghosts[ind];
                int tempStart = start, tempEnd = end;
//              moves away from closestGhost
                index pos = moveAway(tempStart, tempEnd, start, end, closestGhost, maze, connectedComponent, reference);
                tempStart = pos.x;
                tempEnd = pos.y;
//              next four lines of code are to move the agent while also ensuring if the agent doesn't move the agent appears correctly
                maze[start][end] = reference[start][end];
                start = tempStart;
                end = tempEnd;
                maze[start][end] = 'A';
                moveGhosts(ghosts, reference, maze, distance, start,end);
//                this means ghosts have moved into same cell and the dude is dead
                if (maze[start][end] != 'A') {
                    return false;
                }
            } else {
                maze[start][end] = reference[start][end];
                start = newX;
                end = newY;
                maze[start][end] = 'A';
                moveGhosts(ghosts, reference, maze, distance, start, end);
//              this means ghosts have moved into same cell and the dude is dead
                if (maze[start][end] != 'A') {
                    return false;
                }
            }

        }
//        System.out.println("alive");
        return true;
    }

    static boolean agentFour(int num, char [][] maze, HashMap<List<Integer>, Integer> connectedComponent, int e, int ee, index[] nulll){
        char [][] reference = new char[maze.length][];
        for(int i = 0; i < maze.length; i++)
            reference[i] = maze[i].clone();
        int start = 0, end = 0;
        maze[start][end] = 'A';
//        List<index> path = findPath(start, end, maze);
        index[] ghosts = addGhosts(num, maze, connectedComponent);
//      array of index objects that store ghost positions so i can both accurately move them and keep track in case there is overlap
        double[] distance = new double[num];
        moveGhosts(ghosts, reference, maze, distance, start, end);

        while(maze[maze.length-1][maze.length-1] != 'A')
        {
//            Maze.printMaze(maze);
            index closestGhost = num == 0 ? null : ghosts[indMin(distance)];

            if(closestGhost != null && closestGhost.distance < 3){
//              if no valid move is found, then agent just remains in place
                int tempStart = start, tempEnd = end;
                index pos = moveAway(tempStart, tempEnd, start, end, closestGhost, maze, connectedComponent, reference);
                tempStart = pos.x;
                tempEnd = pos.y;
//              next four lines of code are to move the agent while also ensuring if the agent doesn't move the agent appears correctly
                maze[start][end] = reference[start][end];
                start = tempStart;
                end = tempEnd;
                maze[start][end] = 'A';

                moveGhosts(ghosts, reference, maze, distance, start,end);

                //          this means ghosts have moved into same cell and the dude is dead
                if (maze[start][end] != 'A') {
//                    System.out.println("deadp");
                    return false;
                }
            } else {
                int currSmall = connectedComponent.get(List.of(start, end));
                int newX = 0, newY = 0;
//                looks for shortest cell
                for (int i = 0; i < row.length; i++) {
                    int currX = 0;
                    int currY = 0;
                    currX = start + row[i];
                    currY = end + col[i];
                    if (connectedComponent.containsKey(List.of(currX, currY)) && maze[currX][currY] != '*') {
                        int currValue = connectedComponent.get(List.of(currX, currY));
                        if (currValue < currSmall){
                            newX = currX;
                            newY = currY;
                            currSmall = currValue;
                        }
                    }
                }
                maze[start][end] = reference[start][end];
                start = newX;
                end = newY;
                maze[start][end] = 'A';
                moveGhosts(ghosts, reference, maze, distance, start, end);
//              this means ghosts have moved into same cell and the dude is dead
                if (maze[start][end] != 'A') {
//                    System.out.println("dead");
                    return false;
                }
            }

//            Maze.printMaze(maze);
        }


        return true;


    }



    private static List<index> findPath(int start, int end, char[][] maze, HashMap<List<Integer>, Integer> connectedComponent, char[][] ref){
//      fringe to store cells that need to be visited
        PriorityQueue<Agents.index> fringe = new PriorityQueue<>(comparingInt(index::getF));
        HashMap<List<Integer>, Integer> visited = new HashMap<>();
//      used to verify if end source has been achieved
        int endX = maze.length-1, endY = maze[0].length-1;
//      add beginning cell to fringe and visited; aka open and close
        fringe.add(new index(start,end, null));
        visited.put(List.of(start,end), getDistance(start,end, maze.length-1, maze.length-1));

//        System.out.println(connectedComponent.keySet());

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
                    int tempH = connectedComponent.get(List.of(currX, currY));
//                    int tempH = getDistance(currX, currY, maze.length-1, maze.length-1);
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
//        System.out.println("No path");
        return null;
    }


//  uses recursion to get to the oldest node and then proceeds to add the rest of the nodes to the path list.
    private static void getPath(index node, List<index> path){
        if (node != null){
            getPath(node.prev, path);
            path.add(node);

        }
    }

    static index moveAway(int tempStart, int tempEnd, int start, int end, index closestGhost, char[][] maze, HashMap<List<Integer>, Integer> connectedComponent, char[][] ref){
        for (int i = 0; i < row.length; i++) {
            int currX = start + row[i];
            int currY = end + col[i];

//                  checks if move is valid
            if (connectedComponent.containsKey(List.of(currX, currY)) && maze[currX][currY] != '*') {
                if(getDistance(closestGhost.x, closestGhost.y, currX, currY) > getDistance(closestGhost.x, closestGhost.y, tempStart, tempEnd)){
                    tempStart = currX;
                    tempEnd = currY;
                }

            }

        }
//        System.out.println(tempStart + ", " + tempEnd);
        return new index(tempStart, tempEnd);
    }

    //  adds param number of ghosts to the maze and at random locations
    static index[] addGhosts(int num, char[][] maze, HashMap<List<Integer>, Integer> connectedComponent){
        index[] list = new index[num];
//      gets random pair in maze
        while (num > 0) {
            int indx = (int) Math.floor(Math.random() * maze.length), indy = (int) Math.floor(Math.random() * maze[0].length);
//            if ((indx == 0 && indy == 0) || (indx == maze.length-1 && indy == maze[0].length-1)){
//                continue;
//            }
            if (connectedComponent.containsKey(List.of(indx, indy))) {
                maze[indx][indy] = '*';
                list[list.length - num] = new index(indx, indy);
                num -= 1;
            }
        }
        return list;
    }
    static index[] copyGhosts(index[] g){
        index[] arr = new index[g.length];
        for(int x = 0; x < g.length; x++){
            arr[x] = new index(g[x].x, g[x].y);
        }
        return arr;
    }
//  gets manhattan distance
    static int getDistance(int ghostX, int ghostY, int agentX, int agentY){
        return Math.abs(agentY- ghostY) + Math.abs(agentX- ghostX);
    }
//  gets index of smallest thing in ghost distances
    static int indMin(double[] distances){
        int ind = 0;
        double min = distances[0];

        for (int x = 1; x < distances.length; x++){
            if (distances[x] <= min){
                min = distances[x];
                ind = x;
            }
        }
        return ind;
    }

//  iterates through array of ghosts to move said ghosts
    static void moveGhosts(index[] ghosts, char[][] reference, char[][] maze, double[] distance, int agentX, int agentY){
        for (int x = 0; x < ghosts.length; x++){
            index ghost = ghosts[x];
//          randomly decide if current ghost is going up/down left/right
            int ind = (int) Math.floor(Math.random() * row.length);
//            System.out.println(ind);
            int newX = ghost.x + row[ind], newY = ghost.y + col[ind];
//          checks bounds
            if ((0 <= newX && newX < maze.length) && (0 <= newY && newY < maze[0].length)) {
//          checks against reference because that way walls are never missing; if reference is a wall then 50/50 chance to move into it or stay in original position
                if (reference[newX][newY] == '#') {
                    double chance = Math.random();
                    if (chance < .5) {
                        maze[newX][newY] = '*';
//                        if (distance != null) {
                            distance[x] = getDistance(newX, newY, agentX, agentY);
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
                        distance[x] = getDistance(newX, newY, agentX, agentY);
//                        System.out.println(distance[x]);
//                    }
                    ghost.x = newX;
                    ghost.y = newY;
                }
            }
        }

    }
//  used for agentTwo plus
    static void moveGhosts(index[] ghosts, char[][] reference, char[][] maze){
        for (int x = 0; x < ghosts.length; x++){
            index ghost = ghosts[x];
//          randomly decide if current ghost is going up/down left/right
            int ind = (int) Math.floor(Math.random() * row.length);
            int newX = ghost.x + row[ind], newY = ghost.y + col[ind];
//          checks bounds
//            System.out.println("a: " + ghost.x + ", " + ghost.y);
            if ((0 <= newX && newX < maze.length) && (0 <= newY && newY < maze[0].length)) {
//                System.out.println("b: " + newX + ", " + newY);
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

}
