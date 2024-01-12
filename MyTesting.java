import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MyTesting {
    public static void main(String[] args) {
        char[][] besede = pretvoriBesede(TestSkupno.preberiSlovar(TestSkupno.SLOVAR));

        for (int i=0; i<=100; i++) {
            Random random = new Random(i);
            System.out.println(i + ": " + random.nextInt(besede.length));





//            char[][] besede2 = Arrays.copyOf(besede, besede.length);
//            shuffleArray(besede2, random);
//
//            for (int j=0; j<10; j++) {
//                System.out.print(new String(besede2[j]) + ", ");
//            }
//
//            System.out.println();
        }
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
