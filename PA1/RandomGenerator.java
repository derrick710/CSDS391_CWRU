import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomGenerator {
    private static final String FILE_NAME = "random_input_state.txt";
    private static final int PUZZLE_SIZE = 3;

    public static void main(String[] args) {
        // Generate a random initial state
        List<Integer> numbers = generateRandomNumbers();
        String state = convertNumbersToString(numbers);
        System.out.println("Generated random state: " + state);

        // Save the state to a file
        saveStateToFile(state);
        System.out.println("Saved state to file: " + FILE_NAME);
    }

    /**
     * Generates a list of random numbers for the Eight Puzzle problem
     * @return A list of integers representing the state
     */
    private static List<Integer> generateRandomNumbers() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < PUZZLE_SIZE * PUZZLE_SIZE; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers, new Random());
        return numbers;
    }

    /**
     * Converts a list of integers to a string in the required format
     * @param numbers The list of integers to convert
     * @return A string representing the state
     */
    private static String convertNumbersToString(List<Integer> numbers) {
        StringBuilder sb = new StringBuilder("setState \"");
        for (int i = 0; i < PUZZLE_SIZE * PUZZLE_SIZE; i++) {
            int number = numbers.get(i);
            if (number == 0) {
                sb.append("b");
            } else {
                sb.append(number);
            }
            if(i % 3 ==2)
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\"");
        return sb.toString();
    }


    /**
     * Saves the given state to a text file
     * @param state The state to save
     */
    private static void saveStateToFile(String state) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME));
            writer.write(state);
            writer.write(state);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
