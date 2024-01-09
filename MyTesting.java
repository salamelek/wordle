import java.util.*;

public class MyTesting {
    public static void main(String[] args) {
        String[] words = new String[]{"banana", "car", "montage", "cottage", "table", "laptop"};

        System.out.println(Arrays.toString(words));

        words = shuffleArray(words, 1);

        System.out.println(Arrays.toString(words));

        words = shuffleArray(words, 1);

        System.out.println(Arrays.toString(words));
    }

    private static String[] shuffleArray(String[] array, long seed) {
        Random random = new Random(seed);

        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);

            // Swap array[i] and array[index]
            String temp = array[i];
            array[i] = array[index];
            array[index] = temp;
        }

        return array;
    }
}
