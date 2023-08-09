import java.util.ArrayList;
import java.util.List;

public class State implements Comparable<State> {
    private int[][] board;
    private String string_state;
    private State parent;
    private int current_blank_row ;
    private int current_blank_col ;
    private int g;
    private int h;
    /**
     * Constructs a new State object with the given string representation of the board. The string
     * must contain the characters 'b' for the blank tile and the numbers 1-8 representing the other tiles.
     * The string should have no spaces.
     * @param str_state the string representation of the board
     */
    public State(String str_state) {
        this.string_state = str_state;
        this.board = new int[3][3];
        str_state = str_state.replaceAll("\\s", "");
        int strindex = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (strindex < str_state.length()) {
                        char c = str_state.charAt(strindex);
                        if (c == 'b') {
                            board[i][j] = 0;
                            this.current_blank_row = i;
                            this.current_blank_col = j;
                        } else {
                            board[i][j] = Character.getNumericValue(c);
                        }
                        strindex++;
                    }
                }
            }
    }
    /**
     * Sets the parent state of this State object.
     * @param parent the parent state
     */
    public void setParent(State parent){
        this.parent = parent;
    }
    /**
     * Returns the parent state of this State object.
     * @return the parent state
     */
    public State getParent(){
        return this.parent;
    }
    /**
     * Computes the Manhattan distance heuristic h2 for this state, which is the sum of the distances of each tile
     * from its goal position.
     * @return the h2 heuristic value
     */
    public int getH2(){
        int res = 0;
        for(int i = 0; i <3;i++){
            for(int j = 0; j < 3;j++){
                int val = board[i][j];
                if (val != 0){
                    int r = i - (val /3);
                    int c = j - (val %3);
                    res += Math.abs(r) + Math.abs(c);
                }
            }
        }
        return res;
    }

    /**
     * Computes the number of misplaced tiles heuristic h1 for this state.
     * @return the h1 heuristic value
     */
    public int getH1(){
        int res = 0;
        String goal = "b12345678";
        for (int i = 0; i < 9; i++) {
            if (this.toStringState().charAt(i) != goal.charAt(i)) {
                res++;
            }
        }

        return res;
    }
    /**
     Compares this state with another state based on their f values.
     @param other the other state to compare with
     @return -1 if this state has a smaller f value than the other state, 1 if this state has a larger f value,
     markdown Copy code and 0 if they have the same f value
     */
    @Override
    public int compareTo(State other) {
        int f1 = this.getF();
        int f2 = other.getF();
        if (f1 < f2) {
            return -1;
        } else if (f1 > f2) {
            return 1;
        } else {
            return 0;
        }
    }
    /**
     Sets the cost of getting to this state from the initial state.
     @param g the new cost value
     */
    public void setG(int g) {
        this.g = g;
    }
    /**
     Sets the heuristic value of this state.
     @param h the new heuristic value
     */
    public void setH(int h) {
        this.h = h;
    }
    /**
     Returns the cost of getting to this state from the initial state.
     @return the cost of getting to this state from the initial state
     */
    public int getG() {
        return g;
    }
    /**
     Returns the heuristic value of this state.
     @return the heuristic value of this state
     */
    public int getH() {
        return h;
    }
    /**
     Returns the sum of the cost and heuristic value of this state.
     @return the sum of the cost and heuristic value of this state
     */
    public int getF() {
        return g + h;
    }
    /**
     Returns the string representation of this state.
     @return the string representation of this state
     */
    public String toStringState() {
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < 3;i++){
            for(int j = 0; j < 3; j++){
                if(board[i][j] ==0){
                    res.append('b');
                    continue;
                }
                res.append(board[i][j]);
            }
        }
        return res.toString();
    }

    public boolean isSolvable(State state){
        int[] arr = new int[9];
        int k = 0;
        int inversions = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                arr[k] = state.board[i][j];
                k++;
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = i + 1; j < 9; j++) {
                if (arr[j] != 0 && arr[i] != 0 && arr[i] > arr[j]) {
                    inversions++;
                }
            }
        }

        // Check if the puzzle is solvable
        if (inversions % 2 == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void printState() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.print("\n");
        }
    }
    public int getCurrent_blank_row(){
        return this.current_blank_row;
    }
    public int getCurrent_blank_col(){
        return this.current_blank_col;
    }
    public void setCurrent_blank_row(int row){
        this.current_blank_row = row;
    }
    public void setCurrent_blank_col(int col){
        this.current_blank_col = col;
    }
    public boolean isGoal(){
        if(this.toStringState().equals("b12345678")){
            return true;
        }
        return false;
    }
    public State up(){
        if(current_blank_row >0){
            State newState = new State(this.string_state);
            int temp = newState.board[current_blank_row][current_blank_col];
            newState.board[current_blank_row][current_blank_col] = newState.board[current_blank_row-1][current_blank_col];
            newState.board[current_blank_row-1][current_blank_col] = temp;
            newState.setCurrent_blank_row(this.current_blank_row - 1);
            return newState;
        }
        else{
            return null;
        }
    }
    public State down(){
        if(current_blank_row <2) {
            State newState = new State(this.string_state);
            int temp = newState.board[current_blank_row][current_blank_col];
            newState.board[current_blank_row][current_blank_col] = newState.board[current_blank_row + 1][current_blank_col];
            newState.board[current_blank_row + 1][current_blank_col] = temp;
            newState.setCurrent_blank_row(this.current_blank_row + 1);
            return newState;
        }
        else{
            return null;
        }
    }
    public State left(){
        if(current_blank_col >0){
            State newState = new State(this.string_state);
            int temp = newState.board[current_blank_row][current_blank_col];
            newState.board[current_blank_row][current_blank_col] = newState.board[current_blank_row][current_blank_col-1];
            newState.board[current_blank_row][current_blank_col-1] = temp;
            newState.setCurrent_blank_col(this.current_blank_col - 1);
            return newState;
        }
        else{
            return null;
        }
    }
    public State right(){
        if(current_blank_col <2){
            State newState = new State(this.string_state);
            int temp = newState.board[current_blank_row][current_blank_col];
            newState.board[current_blank_row][current_blank_col] = newState.board[current_blank_row][current_blank_col+1];
            newState.board[current_blank_row][current_blank_col+1] = temp;
            newState.setCurrent_blank_col(this.current_blank_col + 1);
            return newState;
        }
        else{
            return null;
        }
    }
    public List<String> action_Avai(){
        List<String> res = new ArrayList<>();
        if(this.getCurrent_blank_row() == 0){
            res.add("down");
        } else if (this.getCurrent_blank_row() == 1) {
            res.add("up");
            res.add("down");
        } else if (this.getCurrent_blank_row() == 2) {
            res.add("up");
        }

        if(this.getCurrent_blank_col() == 0){
            res.add("right");
        } else if (this.getCurrent_blank_col() == 1) {
            res.add("left");
            res.add("right");
        } else if (this.getCurrent_blank_col() == 2) {
            res.add("left");
        }
        return res;
    }
    public List<State> neighbors() {
        List<State> neighbors_list = new ArrayList<State>();

        // Get the row and column of the blank tile
        int blank_row = this.getCurrent_blank_row();
        int blank_col = this.getCurrent_blank_col();

        // Generate neighbors by moving the blank tile in different directions
        if (blank_row > 0) {
            // Move blank tile up
            State upNeighbor = new State(this.toStringState());
            upNeighbor = upNeighbor.up();
            neighbors_list.add(upNeighbor);
        }
        if (blank_col > 0) {
            // Move blank tile left
            State leftNeighbor = new State(this.toStringState());
            leftNeighbor = leftNeighbor.left();
            neighbors_list.add(leftNeighbor);
        }
        if (blank_row < 2) {
            // Move blank tile down
            State downNeighbor = new State(this.toStringState());
            downNeighbor = downNeighbor.down();
            neighbors_list.add(downNeighbor);
        }

        if (blank_col < 2) {
            // Move blank tile right
            State rightNeighbor = new State(this.toStringState());
            rightNeighbor = rightNeighbor.right();
            neighbors_list.add(rightNeighbor);
        }

        return neighbors_list;
    }
    public State move(String direction){
        if(direction == "left"){
            State leftState = new State(this.toStringState());
            leftState = leftState.left();
            return leftState;
        }
        if(direction == "right"){
            State rightState = new State(this.toStringState());
            rightState = rightState.right();
            return rightState;
        }
        if(direction == "up"){
            State upState = new State(this.toStringState());
            upState = upState.up();
            return upState;
        }
        if(direction == "down"){
            State downState = new State(this.toStringState());
            downState = downState.down();
            return downState;
        }
        return null;
    }

    public static void main(String[] args){
        State state = new State("b12 345 678");
        state.printState();
        state.move("right");
        state.printState();
        state.right();
        state.printState();
    }
}
