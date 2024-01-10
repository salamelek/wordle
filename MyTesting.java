import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MyTesting {
    public static void main(String[] args) {
        for (int i=1; i<=19; i++) {
            System.out.print(i + ": ");
            System.out.println(((i - 1) / 6) + 1);
        }

//        long seed = 123;
//        char[][] in = new char[][]{
//                "banana".toCharArray(),
//                "penguin".toCharArray(),
//                "table".toCharArray(),
//                "car".toCharArray(),
//                "antenna".toCharArray(),
//                "shovel".toCharArray()
//        };
//
//        System.out.println("original: \t" + Arrays.deepToString(in));
//
//        shuffleArray(in, new Random(seed));
//        System.out.println("shuffled: \t" + Arrays.deepToString(in));
//
//        //Ordered list of integers from 0 to in.length
//        int[] mapping = new int[]{0,1,2,3,4,5};
//        shuffleArray(mapping, new Random(seed));//same seed -> same shuffle
//
//        char[][] out = new char[in.length][];
//        for(int i = 0; i < out.length; i++)
//            out[mapping[i]] = in[i];
//
//        System.out.println("out: \t\t" + Arrays.deepToString(out));
    }

    private static void shuffleArray(int[] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);

            // Swap array[i] and array[index]
            int temp = array[i];
            array[i] = array[index];
            array[index] = temp;
        }
    }

    private static void shuffleArray(char[][] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);

            // Swap array[i] and array[index]
            char[] temp = array[i];
            array[i] = array[index];
            array[index] = temp;
        }
    }
}
