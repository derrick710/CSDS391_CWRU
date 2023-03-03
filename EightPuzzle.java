import java.io.*;
import java.util.*;

public class EightPuzzle {
    private State state; // the current state of the puzzle

    private String[] commands; // an array of commands to read input from a file
    private int blank_row; // the row index of the blank tile in the puzzle
    private int blank_col; // the column index of the blank tile in the puzzle
    private int maxNode = 100000; // the maximum number of nodes to explore during search
    private int k;
    private String move_command;
    private String heuristic; // the heuristic function to use for A* search

    /**
     * Constructor for EightPuzzle class that reads input from a file and initializes the puzzle state.
     * @param filename the name of the file containing the puzzle configuration and commands
     */
    public EightPuzzle(String filename) {
        try {
            FileInputStream inputStream = new FileInputStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            List<String> commandList = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                commandList.add(line.trim());
            }

            reader.close();
            inputStream.close();

            commands = commandList.toArray(new String[0]);

            for (int i = 0; i < commands.length; i++) {
                String[] tokens = commands[i].split("\\s+");
                if (tokens[0].equals("setState")) {
                    String stateStr = tokens[1];
                    stateStr = stateStr.concat(tokens[2]);
                    stateStr = stateStr.concat(tokens[3]);
                    setState(new State(stateStr));
                    if (!this.state.isSolvable(state)) {
                        System.out.print("Unsolvable");
                        break;
                    }
                } else if (tokens[0].equals("printState")) {
                    System.out.println("Current State is");
                    printState();
                } else if (tokens[0].equals("solve")) {
                    if(tokens[1].equals("Beam")){
                        this.k = Integer.parseInt(tokens[2]);
                        beam_search(state, k);
                        System.out.println("Beam Search");
                    }
                    else {
                        this.heuristic = tokens[1];
                        solve_A_star(state, heuristic);
                        System.out.print("A* search with "+ this.heuristic);
                    }
                } else if (tokens[0].equals("maxNodes")) {
                    this.maxNode(Integer.parseInt(tokens[1]));
                } else if (tokens[0].equals("move")) {
                    move_command = tokens[1];
                    setState(new State(state.move(move_command).toStringState()));
                } else if (tokens[0].equals("randomizeState")) {
                    setState(EightPuzzle.randomizeState(Integer.parseInt(tokens[1])));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Sets the current state of the puzzle.
     * @param state the new State object representing the puzzle state
     */
    public void setState(State state){
        this.state = state;
    }
    /**
     * Prints the current state of the puzzle to the console.
     */
    public void printState() {
        this.state.printState();
    }

    /**
     * Sets the maximum number of nodes to explore during search.
     * @param n the new maximum number of nodes
     */
    public void maxNode(int n){
        this.maxNode = n;
    }

    /**
     * Generates a randomized puzzle state by making n random moves from the goal state.
     * @param n the number of random moves to make
     * @return a new State object representing the randomized puzzle state
     */
   public static State randomizeState(int n){
        State goal = new State("b12 345 678");
        State temp = goal;
        Random rand = new Random();
        String[] direction;
        rand.setSeed(0);
        for(int i = 0; i < n;i++){
                int index = rand.nextInt(temp.action_Avai().size());
                String direct = temp.action_Avai().get(index);
                temp = temp.move(direct);
        }
        return temp;
    }
    /**
     * Implements the A* search algorithm to find a solution to the puzzle.
     * @param initial_state the initial State object representing the puzzle state
     * @param heuristic the heuristic function to use for A* search
     */
    public void solve_A_star(State initial_state, String heuristic){
        int node_num = 0;
        int move_count = 0;
        PriorityQueue<State> frontier = new PriorityQueue<>(new Comparator<State>() {
            @Override
            public int compare(State o1, State o2) {
                return o1.getF() - o2.getF();
            }
        });
        HashMap<State, Integer> reached = new HashMap<>();
        System.out.print("Directions: ");
        initial_state.setG(0);
        frontier.add(initial_state);
        reached.put(initial_state, 0);

        while(frontier.isEmpty() == false){
            if(node_num > this.maxNode){
                throw new RuntimeException("MaxNode exceeded");
            }
            State curr = frontier.poll();
            this.state = curr;
            if(curr.getParent() != null) {
                if (curr.getCurrent_blank_col() > curr.getParent().getCurrent_blank_col()) {
                    String move = "right ";
                    System.out.print(move);
                    move_count++;
                }
                if (curr.getCurrent_blank_col() < curr.getParent().getCurrent_blank_col()) {
                    String move = "left ";
                    System.out.print(move);
                    move_count++;

                }
                if (curr.getCurrent_blank_row() > curr.getParent().getCurrent_blank_row()) {
                    String move = "down ";
                    System.out.print(move);
                    move_count++;
                }
                if (curr.getCurrent_blank_row() < curr.getParent().getCurrent_blank_row()) {
                    String move = "up ";
                    System.out.print(move);
                    move_count++;
                }
            }
            if(curr.isGoal()){
                System.out.println("\n" + "Number of tiles moved " + move_count);
                System.out.println("Number of states visited " + node_num);
                System.out.println("Found goal state");
                return;
            }
            else {
                for (State neighbor : curr.neighbors()) {
                    if(neighbor != null) {
                        if (!reached.containsKey(neighbor.toStringState()) || (neighbor.getG() < reached.get(neighbor.toStringState()))) {
                            neighbor.setG(curr.getG() + 1);
                            if (heuristic.equals("h1")) {
                                neighbor.setH(neighbor.getH1());
                            } else {
                                neighbor.setH(neighbor.getH2());
                            }
                            neighbor.setParent(curr);
                            frontier.add(neighbor);
                            reached.put(neighbor, neighbor.getG());
                            node_num++;
                        }
                    }
                }
            }
        }
    }
    public State move(String direction){
        return new State(this.state.move(direction).toStringState());
    }
    /**
     * Implements the beam search algorithm to find a solution to the puzzle.
     * @param state the initial State object representing the puzzle state
     * @param k the beam width parameter for the search
     */
    public void beam_search(State state, int k){
        int move_count = 0;
        int node_num = 0;
        PriorityQueue<State> frontier = new PriorityQueue<>(new Comparator<State>() {
            @Override
            public int compare(State o1, State o2) {
                return o1.getF() - o2.getF();
            }
        });
        HashMap<State, Integer> reached = new HashMap<>();
        System.out.print("Directions: ");
        state.setG(0);
        state.setH(state.getH2() + state.getH1());
        frontier.add(state);
        reached.put(state,0);
        node_num++;
        move_count++;

        while(!frontier.isEmpty()){
            if(node_num > maxNode){
                throw new RuntimeException("MaxNode exceeded");
            }
            State curr = frontier.poll();
            this.state = curr;
            if(curr.getParent() != null) {
                if (curr.getCurrent_blank_col() > curr.getParent().getCurrent_blank_col()) {
                    String move = "right ";
                    System.out.print(move);
                    move_count++;
                }
                if (curr.getCurrent_blank_col() < curr.getParent().getCurrent_blank_col()) {
                    String move = "left ";
                    System.out.print(move);
                    move_count++;

                }
                if (curr.getCurrent_blank_row() > curr.getParent().getCurrent_blank_row()) {
                    String move = "down ";
                    System.out.print(move);
                    move_count++;
                }
                if (curr.getCurrent_blank_row() < curr.getParent().getCurrent_blank_row()) {
                    String move = "up ";
                    System.out.print(move);
                    move_count++;
                }
            }
            if(curr.isGoal()){
                System.out.print("\n"+"Found goal state");
                System.out.println("\n" + "Number of tiles moved " + move_count);
                System.out.println("Number of states visited " + node_num);
                return;
            }
            else {
                List<State> neighbors = curr.neighbors();
                ArrayList<State> candidates = new ArrayList<State>();
                for (State neighbor : neighbors) {
                    if (neighbor != null && !reached.containsKey(neighbor)) {
                        neighbor.setG(curr.getG() + 1);
                        neighbor.setH(neighbor.getH1() + neighbor.getH2());
                        neighbor.setParent(curr);
                        candidates.add(neighbor);
                        reached.put(neighbor, neighbor.getG());
                        node_num++;
                    }
                }
                if (candidates.size() > k) {
                    Collections.sort(candidates, new Comparator<State>() {
                        @Override
                        public int compare(State o1, State o2) {
                            return o1.getF() - o2.getF();
                        }
                    });
                    frontier.clear();
                    for (int i = 0; i < k; i++) {
                        frontier.add(candidates.get(i));
                        node_num++;
                    }
                } else {
                    for (State candidate : candidates) {
                        frontier.add(candidate);
                        node_num++;
                    }
                }
            }
        }
        System.out.println("Failed to find with beam width");
    }
    public static void main(String[] args) {
        EightPuzzle puzzle = new EightPuzzle("inputh1");
     /*   for(String val: puzzle.commands){
            System.out.println(val);
        }*/
    }
}
