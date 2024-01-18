import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MyTesting {
    public static void main(String[] args) {
        char[][] besede = pretvoriBesede(TestSkupno.preberiSlovar(TestSkupno.SLOVAR));

        char[][] words = new char[][] {"banana".toCharArray(), "yourmom".toCharArray(), "horse".toCharArray()};
        char[][] words2 = new char[3][];
        words2[0] = words[0];
        words2[1] = words[1];
        words2[2] = words[2];

        System.out.println(Arrays.deepToString(words2));
        System.out.println(Arrays.toString(words));
//        System.out.println(Arrays.toString(words2));
        char[] a = new char[]{};
        words[1] = a;
        System.out.println(Arrays.deepToString(words2));
        System.out.println(Arrays.toString(words));
//        System.out.println(Arrays.toString(words2));

//        String[] words = new String[] {"banana", "yourmom", "horse"};
//        String[] words2 = new String[3];
//        words2[0] = words[0];
//        words2[1] = words[1];
//        words2[2] = words[2];
//
//        System.out.println(Arrays.toString(words2));
//        words[1] = null;
//        System.out.println(Arrays.toString(words2));
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

    public static char[][] pretvoriBesede(Set<String> besede) {
        /**
         * pretvorimo Set<String> v 2D char tabelo zaradi hitrosti
         * */

        int stBesed = besede.size();
        char[][] tabelaBesed = new char[stBesed][6];

        int indeks = 0;
        for (String beseda: besede) {
            tabelaBesed[indeks] = beseda.toCharArray();
            indeks++;
        }

        return tabelaBesed;
    }
}
