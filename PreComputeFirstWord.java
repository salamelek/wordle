import java.util.*;

public class PreComputeFirstWord {
    public static void main(String[] args) {
        char[] possibleLetters = "abcdefghijklmnoprstuvz".toCharArray();
        char[][] mozniOdzivi = vrniMozneOdzive();
        char[][] slovar = pretvoriBesede(TestSkupno.preberiSlovar("slovar.txt"));

//        for (int l1=0; l1<possibleLetters.length; l1++) {
//            char letter1 = possibleLetters[l1];
//
//            for (int l2=0; l2<possibleLetters.length; l2++) {
//                char letter2 = possibleLetters[l2];
//                if (l1 == l2) continue;
//
//                for (int l3=0; l3<possibleLetters.length; l3++) {
//                    char letter3 = possibleLetters[l3];
//                    if (l1 == l3 || l2 == l3) continue;
//
//                    for (int l4=0; l4<possibleLetters.length; l4++) {
//                        char letter4 = possibleLetters[l4];
//                        if (l1 == l4 || l2 == l4 || l3 == l4) continue;
//
//                        for (int l5=0; l5<possibleLetters.length; l5++) {
//                            char letter5 = possibleLetters[l5];
//                            if (l1 == l5 || l2 == l5 || l3 == l5 || l4 == l5) continue;
//
//                            for (int l6=0; l6<possibleLetters.length; l6++) {
//                                char letter6 = possibleLetters[l6];
//                                if (l1 == l6 || l2 == l6 || l3 == l6 || l4 == l6 || l5 == l6) continue;
//
//                                char[] word1 = new char[]{letter1, letter2, letter3, letter4, letter5, letter6};
//
//                                printScore(slovar, word1);
//                            }
//                        }
//                    }
//                }
//            }
//        }

        for (char[] word1: slovar) {
            printScore(slovar, word1);
        }
    }

    public static void printScore(char[][] slovar, char[] word1) {
        System.out.print(new String(word1));

        int score = 0;
        for (char[] word2: slovar) {
            char[] odziv = izracunajOdziv(word2, word1);

            for (char o: odziv) {
                switch (o) {
                    case 'o' -> score += 1;
                    case '+' -> score += 2;
                    default -> score += 0;
                }
            }
        }
        System.out.println(" -> " + score);
    }

    public static short index(char[] beseda, char crka) {
        for (short i=0; i<beseda.length; i++) {
            if (beseda[i] == crka) {
                return i;
            }
        }

        return -1;
    }

    public static char[][] vrniMozneOdzive() {
        // Moj Bog, žal mi je, da sem grešil in žalil tebe, ki si moj najboljši Oče. Trdno sklenem, da se bom poboljšal, pomagaj mi s svojo milostjo. Amen.
        return new char[][]{{'-','-','-','-','-','-'},{'-','-','-','-','-','o'},{'-','-','-','-','-','+'},{'-','-','-','-','o','-'},{'-','-','-','-','o','o'},{'-','-','-','-','o','+'},{'-','-','-','-','+','-'},{'-','-','-','-','+','o'},{'-','-','-','-','+','+'},{'-','-','-','o','-','-'},{'-','-','-','o','-','o'},{'-','-','-','o','-','+'},{'-','-','-','o','o','-'},{'-','-','-','o','o','o'},{'-','-','-','o','o','+'},{'-','-','-','o','+','-'},{'-','-','-','o','+','o'},{'-','-','-','o','+','+'},{'-','-','-','+','-','-'},{'-','-','-','+','-','o'},{'-','-','-','+','-','+'},{'-','-','-','+','o','-'},{'-','-','-','+','o','o'},{'-','-','-','+','o','+'},{'-','-','-','+','+','-'},{'-','-','-','+','+','o'},{'-','-','-','+','+','+'},{'-','-','o','-','-','-'},{'-','-','o','-','-','o'},{'-','-','o','-','-','+'},{'-','-','o','-','o','-'},{'-','-','o','-','o','o'},{'-','-','o','-','o','+'},{'-','-','o','-','+','-'},{'-','-','o','-','+','o'},{'-','-','o','-','+','+'},{'-','-','o','o','-','-'},{'-','-','o','o','-','o'},{'-','-','o','o','-','+'},{'-','-','o','o','o','-'},{'-','-','o','o','o','o'},{'-','-','o','o','o','+'},{'-','-','o','o','+','-'},{'-','-','o','o','+','o'},{'-','-','o','o','+','+'},{'-','-','o','+','-','-'},{'-','-','o','+','-','o'},{'-','-','o','+','-','+'},{'-','-','o','+','o','-'},{'-','-','o','+','o','o'},{'-','-','o','+','o','+'},{'-','-','o','+','+','-'},{'-','-','o','+','+','o'},{'-','-','o','+','+','+'},{'-','-','+','-','-','-'},{'-','-','+','-','-','o'},{'-','-','+','-','-','+'},{'-','-','+','-','o','-'},{'-','-','+','-','o','o'},{'-','-','+','-','o','+'},{'-','-','+','-','+','-'},{'-','-','+','-','+','o'},{'-','-','+','-','+','+'},{'-','-','+','o','-','-'},{'-','-','+','o','-','o'},{'-','-','+','o','-','+'},{'-','-','+','o','o','-'},{'-','-','+','o','o','o'},{'-','-','+','o','o','+'},{'-','-','+','o','+','-'},{'-','-','+','o','+','o'},{'-','-','+','o','+','+'},{'-','-','+','+','-','-'},{'-','-','+','+','-','o'},{'-','-','+','+','-','+'},{'-','-','+','+','o','-'},{'-','-','+','+','o','o'},{'-','-','+','+','o','+'},{'-','-','+','+','+','-'},{'-','-','+','+','+','o'},{'-','-','+','+','+','+'},{'-','o','-','-','-','-'},{'-','o','-','-','-','o'},{'-','o','-','-','-','+'},{'-','o','-','-','o','-'},{'-','o','-','-','o','o'},{'-','o','-','-','o','+'},{'-','o','-','-','+','-'},{'-','o','-','-','+','o'},{'-','o','-','-','+','+'},{'-','o','-','o','-','-'},{'-','o','-','o','-','o'},{'-','o','-','o','-','+'},{'-','o','-','o','o','-'},{'-','o','-','o','o','o'},{'-','o','-','o','o','+'},{'-','o','-','o','+','-'},{'-','o','-','o','+','o'},{'-','o','-','o','+','+'},{'-','o','-','+','-','-'},{'-','o','-','+','-','o'},{'-','o','-','+','-','+'},{'-','o','-','+','o','-'},{'-','o','-','+','o','o'},{'-','o','-','+','o','+'},{'-','o','-','+','+','-'},{'-','o','-','+','+','o'},{'-','o','-','+','+','+'},{'-','o','o','-','-','-'},{'-','o','o','-','-','o'},{'-','o','o','-','-','+'},{'-','o','o','-','o','-'},{'-','o','o','-','o','o'},{'-','o','o','-','o','+'},{'-','o','o','-','+','-'},{'-','o','o','-','+','o'},{'-','o','o','-','+','+'},{'-','o','o','o','-','-'},{'-','o','o','o','-','o'},{'-','o','o','o','-','+'},{'-','o','o','o','o','-'},{'-','o','o','o','o','o'},{'-','o','o','o','o','+'},{'-','o','o','o','+','-'},{'-','o','o','o','+','o'},{'-','o','o','o','+','+'},{'-','o','o','+','-','-'},{'-','o','o','+','-','o'},{'-','o','o','+','-','+'},{'-','o','o','+','o','-'},{'-','o','o','+','o','o'},{'-','o','o','+','o','+'},{'-','o','o','+','+','-'},{'-','o','o','+','+','o'},{'-','o','o','+','+','+'},{'-','o','+','-','-','-'},{'-','o','+','-','-','o'},{'-','o','+','-','-','+'},{'-','o','+','-','o','-'},{'-','o','+','-','o','o'},{'-','o','+','-','o','+'},{'-','o','+','-','+','-'},{'-','o','+','-','+','o'},{'-','o','+','-','+','+'},{'-','o','+','o','-','-'},{'-','o','+','o','-','o'},{'-','o','+','o','-','+'},{'-','o','+','o','o','-'},{'-','o','+','o','o','o'},{'-','o','+','o','o','+'},{'-','o','+','o','+','-'},{'-','o','+','o','+','o'},{'-','o','+','o','+','+'},{'-','o','+','+','-','-'},{'-','o','+','+','-','o'},{'-','o','+','+','-','+'},{'-','o','+','+','o','-'},{'-','o','+','+','o','o'},{'-','o','+','+','o','+'},{'-','o','+','+','+','-'},{'-','o','+','+','+','o'},{'-','o','+','+','+','+'},{'-','+','-','-','-','-'},{'-','+','-','-','-','o'},{'-','+','-','-','-','+'},{'-','+','-','-','o','-'},{'-','+','-','-','o','o'},{'-','+','-','-','o','+'},{'-','+','-','-','+','-'},{'-','+','-','-','+','o'},{'-','+','-','-','+','+'},{'-','+','-','o','-','-'},{'-','+','-','o','-','o'},{'-','+','-','o','-','+'},{'-','+','-','o','o','-'},{'-','+','-','o','o','o'},{'-','+','-','o','o','+'},{'-','+','-','o','+','-'},{'-','+','-','o','+','o'},{'-','+','-','o','+','+'},{'-','+','-','+','-','-'},{'-','+','-','+','-','o'},{'-','+','-','+','-','+'},{'-','+','-','+','o','-'},{'-','+','-','+','o','o'},{'-','+','-','+','o','+'},{'-','+','-','+','+','-'},{'-','+','-','+','+','o'},{'-','+','-','+','+','+'},{'-','+','o','-','-','-'},{'-','+','o','-','-','o'},{'-','+','o','-','-','+'},{'-','+','o','-','o','-'},{'-','+','o','-','o','o'},{'-','+','o','-','o','+'},{'-','+','o','-','+','-'},{'-','+','o','-','+','o'},{'-','+','o','-','+','+'},{'-','+','o','o','-','-'},{'-','+','o','o','-','o'},{'-','+','o','o','-','+'},{'-','+','o','o','o','-'},{'-','+','o','o','o','o'},{'-','+','o','o','o','+'},{'-','+','o','o','+','-'},{'-','+','o','o','+','o'},{'-','+','o','o','+','+'},{'-','+','o','+','-','-'},{'-','+','o','+','-','o'},{'-','+','o','+','-','+'},{'-','+','o','+','o','-'},{'-','+','o','+','o','o'},{'-','+','o','+','o','+'},{'-','+','o','+','+','-'},{'-','+','o','+','+','o'},{'-','+','o','+','+','+'},{'-','+','+','-','-','-'},{'-','+','+','-','-','o'},{'-','+','+','-','-','+'},{'-','+','+','-','o','-'},{'-','+','+','-','o','o'},{'-','+','+','-','o','+'},{'-','+','+','-','+','-'},{'-','+','+','-','+','o'},{'-','+','+','-','+','+'},{'-','+','+','o','-','-'},{'-','+','+','o','-','o'},{'-','+','+','o','-','+'},{'-','+','+','o','o','-'},{'-','+','+','o','o','o'},{'-','+','+','o','o','+'},{'-','+','+','o','+','-'},{'-','+','+','o','+','o'},{'-','+','+','o','+','+'},{'-','+','+','+','-','-'},{'-','+','+','+','-','o'},{'-','+','+','+','-','+'},{'-','+','+','+','o','-'},{'-','+','+','+','o','o'},{'-','+','+','+','o','+'},{'-','+','+','+','+','-'},{'-','+','+','+','+','o'},{'-','+','+','+','+','+'},{'o','-','-','-','-','-'},{'o','-','-','-','-','o'},{'o','-','-','-','-','+'},{'o','-','-','-','o','-'},{'o','-','-','-','o','o'},{'o','-','-','-','o','+'},{'o','-','-','-','+','-'},{'o','-','-','-','+','o'},{'o','-','-','-','+','+'},{'o','-','-','o','-','-'},{'o','-','-','o','-','o'},{'o','-','-','o','-','+'},{'o','-','-','o','o','-'},{'o','-','-','o','o','o'},{'o','-','-','o','o','+'},{'o','-','-','o','+','-'},{'o','-','-','o','+','o'},{'o','-','-','o','+','+'},{'o','-','-','+','-','-'},{'o','-','-','+','-','o'},{'o','-','-','+','-','+'},{'o','-','-','+','o','-'},{'o','-','-','+','o','o'},{'o','-','-','+','o','+'},{'o','-','-','+','+','-'},{'o','-','-','+','+','o'},{'o','-','-','+','+','+'},{'o','-','o','-','-','-'},{'o','-','o','-','-','o'},{'o','-','o','-','-','+'},{'o','-','o','-','o','-'},{'o','-','o','-','o','o'},{'o','-','o','-','o','+'},{'o','-','o','-','+','-'},{'o','-','o','-','+','o'},{'o','-','o','-','+','+'},{'o','-','o','o','-','-'},{'o','-','o','o','-','o'},{'o','-','o','o','-','+'},{'o','-','o','o','o','-'},{'o','-','o','o','o','o'},{'o','-','o','o','o','+'},{'o','-','o','o','+','-'},{'o','-','o','o','+','o'},{'o','-','o','o','+','+'},{'o','-','o','+','-','-'},{'o','-','o','+','-','o'},{'o','-','o','+','-','+'},{'o','-','o','+','o','-'},{'o','-','o','+','o','o'},{'o','-','o','+','o','+'},{'o','-','o','+','+','-'},{'o','-','o','+','+','o'},{'o','-','o','+','+','+'},{'o','-','+','-','-','-'},{'o','-','+','-','-','o'},{'o','-','+','-','-','+'},{'o','-','+','-','o','-'},{'o','-','+','-','o','o'},{'o','-','+','-','o','+'},{'o','-','+','-','+','-'},{'o','-','+','-','+','o'},{'o','-','+','-','+','+'},{'o','-','+','o','-','-'},{'o','-','+','o','-','o'},{'o','-','+','o','-','+'},{'o','-','+','o','o','-'},{'o','-','+','o','o','o'},{'o','-','+','o','o','+'},{'o','-','+','o','+','-'},{'o','-','+','o','+','o'},{'o','-','+','o','+','+'},{'o','-','+','+','-','-'},{'o','-','+','+','-','o'},{'o','-','+','+','-','+'},{'o','-','+','+','o','-'},{'o','-','+','+','o','o'},{'o','-','+','+','o','+'},{'o','-','+','+','+','-'},{'o','-','+','+','+','o'},{'o','-','+','+','+','+'},{'o','o','-','-','-','-'},{'o','o','-','-','-','o'},{'o','o','-','-','-','+'},{'o','o','-','-','o','-'},{'o','o','-','-','o','o'},{'o','o','-','-','o','+'},{'o','o','-','-','+','-'},{'o','o','-','-','+','o'},{'o','o','-','-','+','+'},{'o','o','-','o','-','-'},{'o','o','-','o','-','o'},{'o','o','-','o','-','+'},{'o','o','-','o','o','-'},{'o','o','-','o','o','o'},{'o','o','-','o','o','+'},{'o','o','-','o','+','-'},{'o','o','-','o','+','o'},{'o','o','-','o','+','+'},{'o','o','-','+','-','-'},{'o','o','-','+','-','o'},{'o','o','-','+','-','+'},{'o','o','-','+','o','-'},{'o','o','-','+','o','o'},{'o','o','-','+','o','+'},{'o','o','-','+','+','-'},{'o','o','-','+','+','o'},{'o','o','-','+','+','+'},{'o','o','o','-','-','-'},{'o','o','o','-','-','o'},{'o','o','o','-','-','+'},{'o','o','o','-','o','-'},{'o','o','o','-','o','o'},{'o','o','o','-','o','+'},{'o','o','o','-','+','-'},{'o','o','o','-','+','o'},{'o','o','o','-','+','+'},{'o','o','o','o','-','-'},{'o','o','o','o','-','o'},{'o','o','o','o','-','+'},{'o','o','o','o','o','-'},{'o','o','o','o','o','o'},{'o','o','o','o','o','+'},{'o','o','o','o','+','-'},{'o','o','o','o','+','o'},{'o','o','o','o','+','+'},{'o','o','o','+','-','-'},{'o','o','o','+','-','o'},{'o','o','o','+','-','+'},{'o','o','o','+','o','-'},{'o','o','o','+','o','o'},{'o','o','o','+','o','+'},{'o','o','o','+','+','-'},{'o','o','o','+','+','o'},{'o','o','o','+','+','+'},{'o','o','+','-','-','-'},{'o','o','+','-','-','o'},{'o','o','+','-','-','+'},{'o','o','+','-','o','-'},{'o','o','+','-','o','o'},{'o','o','+','-','o','+'},{'o','o','+','-','+','-'},{'o','o','+','-','+','o'},{'o','o','+','-','+','+'},{'o','o','+','o','-','-'},{'o','o','+','o','-','o'},{'o','o','+','o','-','+'},{'o','o','+','o','o','-'},{'o','o','+','o','o','o'},{'o','o','+','o','o','+'},{'o','o','+','o','+','-'},{'o','o','+','o','+','o'},{'o','o','+','o','+','+'},{'o','o','+','+','-','-'},{'o','o','+','+','-','o'},{'o','o','+','+','-','+'},{'o','o','+','+','o','-'},{'o','o','+','+','o','o'},{'o','o','+','+','o','+'},{'o','o','+','+','+','-'},{'o','o','+','+','+','o'},{'o','o','+','+','+','+'},{'o','+','-','-','-','-'},{'o','+','-','-','-','o'},{'o','+','-','-','-','+'},{'o','+','-','-','o','-'},{'o','+','-','-','o','o'},{'o','+','-','-','o','+'},{'o','+','-','-','+','-'},{'o','+','-','-','+','o'},{'o','+','-','-','+','+'},{'o','+','-','o','-','-'},{'o','+','-','o','-','o'},{'o','+','-','o','-','+'},{'o','+','-','o','o','-'},{'o','+','-','o','o','o'},{'o','+','-','o','o','+'},{'o','+','-','o','+','-'},{'o','+','-','o','+','o'},{'o','+','-','o','+','+'},{'o','+','-','+','-','-'},{'o','+','-','+','-','o'},{'o','+','-','+','-','+'},{'o','+','-','+','o','-'},{'o','+','-','+','o','o'},{'o','+','-','+','o','+'},{'o','+','-','+','+','-'},{'o','+','-','+','+','o'},{'o','+','-','+','+','+'},{'o','+','o','-','-','-'},{'o','+','o','-','-','o'},{'o','+','o','-','-','+'},{'o','+','o','-','o','-'},{'o','+','o','-','o','o'},{'o','+','o','-','o','+'},{'o','+','o','-','+','-'},{'o','+','o','-','+','o'},{'o','+','o','-','+','+'},{'o','+','o','o','-','-'},{'o','+','o','o','-','o'},{'o','+','o','o','-','+'},{'o','+','o','o','o','-'},{'o','+','o','o','o','o'},{'o','+','o','o','o','+'},{'o','+','o','o','+','-'},{'o','+','o','o','+','o'},{'o','+','o','o','+','+'},{'o','+','o','+','-','-'},{'o','+','o','+','-','o'},{'o','+','o','+','-','+'},{'o','+','o','+','o','-'},{'o','+','o','+','o','o'},{'o','+','o','+','o','+'},{'o','+','o','+','+','-'},{'o','+','o','+','+','o'},{'o','+','o','+','+','+'},{'o','+','+','-','-','-'},{'o','+','+','-','-','o'},{'o','+','+','-','-','+'},{'o','+','+','-','o','-'},{'o','+','+','-','o','o'},{'o','+','+','-','o','+'},{'o','+','+','-','+','-'},{'o','+','+','-','+','o'},{'o','+','+','-','+','+'},{'o','+','+','o','-','-'},{'o','+','+','o','-','o'},{'o','+','+','o','-','+'},{'o','+','+','o','o','-'},{'o','+','+','o','o','o'},{'o','+','+','o','o','+'},{'o','+','+','o','+','-'},{'o','+','+','o','+','o'},{'o','+','+','o','+','+'},{'o','+','+','+','-','-'},{'o','+','+','+','-','o'},{'o','+','+','+','-','+'},{'o','+','+','+','o','-'},{'o','+','+','+','o','o'},{'o','+','+','+','o','+'},{'o','+','+','+','+','-'},{'o','+','+','+','+','o'},{'o','+','+','+','+','+'},{'+','-','-','-','-','-'},{'+','-','-','-','-','o'},{'+','-','-','-','-','+'},{'+','-','-','-','o','-'},{'+','-','-','-','o','o'},{'+','-','-','-','o','+'},{'+','-','-','-','+','-'},{'+','-','-','-','+','o'},{'+','-','-','-','+','+'},{'+','-','-','o','-','-'},{'+','-','-','o','-','o'},{'+','-','-','o','-','+'},{'+','-','-','o','o','-'},{'+','-','-','o','o','o'},{'+','-','-','o','o','+'},{'+','-','-','o','+','-'},{'+','-','-','o','+','o'},{'+','-','-','o','+','+'},{'+','-','-','+','-','-'},{'+','-','-','+','-','o'},{'+','-','-','+','-','+'},{'+','-','-','+','o','-'},{'+','-','-','+','o','o'},{'+','-','-','+','o','+'},{'+','-','-','+','+','-'},{'+','-','-','+','+','o'},{'+','-','-','+','+','+'},{'+','-','o','-','-','-'},{'+','-','o','-','-','o'},{'+','-','o','-','-','+'},{'+','-','o','-','o','-'},{'+','-','o','-','o','o'},{'+','-','o','-','o','+'},{'+','-','o','-','+','-'},{'+','-','o','-','+','o'},{'+','-','o','-','+','+'},{'+','-','o','o','-','-'},{'+','-','o','o','-','o'},{'+','-','o','o','-','+'},{'+','-','o','o','o','-'},{'+','-','o','o','o','o'},{'+','-','o','o','o','+'},{'+','-','o','o','+','-'},{'+','-','o','o','+','o'},{'+','-','o','o','+','+'},{'+','-','o','+','-','-'},{'+','-','o','+','-','o'},{'+','-','o','+','-','+'},{'+','-','o','+','o','-'},{'+','-','o','+','o','o'},{'+','-','o','+','o','+'},{'+','-','o','+','+','-'},{'+','-','o','+','+','o'},{'+','-','o','+','+','+'},{'+','-','+','-','-','-'},{'+','-','+','-','-','o'},{'+','-','+','-','-','+'},{'+','-','+','-','o','-'},{'+','-','+','-','o','o'},{'+','-','+','-','o','+'},{'+','-','+','-','+','-'},{'+','-','+','-','+','o'},{'+','-','+','-','+','+'},{'+','-','+','o','-','-'},{'+','-','+','o','-','o'},{'+','-','+','o','-','+'},{'+','-','+','o','o','-'},{'+','-','+','o','o','o'},{'+','-','+','o','o','+'},{'+','-','+','o','+','-'},{'+','-','+','o','+','o'},{'+','-','+','o','+','+'},{'+','-','+','+','-','-'},{'+','-','+','+','-','o'},{'+','-','+','+','-','+'},{'+','-','+','+','o','-'},{'+','-','+','+','o','o'},{'+','-','+','+','o','+'},{'+','-','+','+','+','-'},{'+','-','+','+','+','o'},{'+','-','+','+','+','+'},{'+','o','-','-','-','-'},{'+','o','-','-','-','o'},{'+','o','-','-','-','+'},{'+','o','-','-','o','-'},{'+','o','-','-','o','o'},{'+','o','-','-','o','+'},{'+','o','-','-','+','-'},{'+','o','-','-','+','o'},{'+','o','-','-','+','+'},{'+','o','-','o','-','-'},{'+','o','-','o','-','o'},{'+','o','-','o','-','+'},{'+','o','-','o','o','-'},{'+','o','-','o','o','o'},{'+','o','-','o','o','+'},{'+','o','-','o','+','-'},{'+','o','-','o','+','o'},{'+','o','-','o','+','+'},{'+','o','-','+','-','-'},{'+','o','-','+','-','o'},{'+','o','-','+','-','+'},{'+','o','-','+','o','-'},{'+','o','-','+','o','o'},{'+','o','-','+','o','+'},{'+','o','-','+','+','-'},{'+','o','-','+','+','o'},{'+','o','-','+','+','+'},{'+','o','o','-','-','-'},{'+','o','o','-','-','o'},{'+','o','o','-','-','+'},{'+','o','o','-','o','-'},{'+','o','o','-','o','o'},{'+','o','o','-','o','+'},{'+','o','o','-','+','-'},{'+','o','o','-','+','o'},{'+','o','o','-','+','+'},{'+','o','o','o','-','-'},{'+','o','o','o','-','o'},{'+','o','o','o','-','+'},{'+','o','o','o','o','-'},{'+','o','o','o','o','o'},{'+','o','o','o','o','+'},{'+','o','o','o','+','-'},{'+','o','o','o','+','o'},{'+','o','o','o','+','+'},{'+','o','o','+','-','-'},{'+','o','o','+','-','o'},{'+','o','o','+','-','+'},{'+','o','o','+','o','-'},{'+','o','o','+','o','o'},{'+','o','o','+','o','+'},{'+','o','o','+','+','-'},{'+','o','o','+','+','o'},{'+','o','o','+','+','+'},{'+','o','+','-','-','-'},{'+','o','+','-','-','o'},{'+','o','+','-','-','+'},{'+','o','+','-','o','-'},{'+','o','+','-','o','o'},{'+','o','+','-','o','+'},{'+','o','+','-','+','-'},{'+','o','+','-','+','o'},{'+','o','+','-','+','+'},{'+','o','+','o','-','-'},{'+','o','+','o','-','o'},{'+','o','+','o','-','+'},{'+','o','+','o','o','-'},{'+','o','+','o','o','o'},{'+','o','+','o','o','+'},{'+','o','+','o','+','-'},{'+','o','+','o','+','o'},{'+','o','+','o','+','+'},{'+','o','+','+','-','-'},{'+','o','+','+','-','o'},{'+','o','+','+','-','+'},{'+','o','+','+','o','-'},{'+','o','+','+','o','o'},{'+','o','+','+','o','+'},{'+','o','+','+','+','-'},{'+','o','+','+','+','o'},{'+','o','+','+','+','+'},{'+','+','-','-','-','-'},{'+','+','-','-','-','o'},{'+','+','-','-','-','+'},{'+','+','-','-','o','-'},{'+','+','-','-','o','o'},{'+','+','-','-','o','+'},{'+','+','-','-','+','-'},{'+','+','-','-','+','o'},{'+','+','-','-','+','+'},{'+','+','-','o','-','-'},{'+','+','-','o','-','o'},{'+','+','-','o','-','+'},{'+','+','-','o','o','-'},{'+','+','-','o','o','o'},{'+','+','-','o','o','+'},{'+','+','-','o','+','-'},{'+','+','-','o','+','o'},{'+','+','-','o','+','+'},{'+','+','-','+','-','-'},{'+','+','-','+','-','o'},{'+','+','-','+','-','+'},{'+','+','-','+','o','-'},{'+','+','-','+','o','o'},{'+','+','-','+','o','+'},{'+','+','-','+','+','-'},{'+','+','-','+','+','o'},{'+','+','-','+','+','+'},{'+','+','o','-','-','-'},{'+','+','o','-','-','o'},{'+','+','o','-','-','+'},{'+','+','o','-','o','-'},{'+','+','o','-','o','o'},{'+','+','o','-','o','+'},{'+','+','o','-','+','-'},{'+','+','o','-','+','o'},{'+','+','o','-','+','+'},{'+','+','o','o','-','-'},{'+','+','o','o','-','o'},{'+','+','o','o','-','+'},{'+','+','o','o','o','-'},{'+','+','o','o','o','o'},{'+','+','o','o','o','+'},{'+','+','o','o','+','-'},{'+','+','o','o','+','o'},{'+','+','o','o','+','+'},{'+','+','o','+','-','-'},{'+','+','o','+','-','o'},{'+','+','o','+','-','+'},{'+','+','o','+','o','-'},{'+','+','o','+','o','o'},{'+','+','o','+','o','+'},{'+','+','o','+','+','-'},{'+','+','o','+','+','o'},{'+','+','o','+','+','+'},{'+','+','+','-','-','-'},{'+','+','+','-','-','o'},{'+','+','+','-','-','+'},{'+','+','+','-','o','-'},{'+','+','+','-','o','o'},{'+','+','+','-','o','+'},{'+','+','+','-','+','-'},{'+','+','+','-','+','o'},{'+','+','+','-','+','+'},{'+','+','+','o','-','-'},{'+','+','+','o','-','o'},{'+','+','+','o','-','+'},{'+','+','+','o','o','-'},{'+','+','+','o','o','o'},{'+','+','+','o','o','+'},{'+','+','+','o','+','-'},{'+','+','+','o','+','o'},{'+','+','+','o','+','+'},{'+','+','+','+','-','-'},{'+','+','+','+','-','o'},{'+','+','+','+','-','+'},{'+','+','+','+','o','-'},{'+','+','+','+','o','o'},{'+','+','+','+','o','+'},{'+','+','+','+','+','-'},{'+','+','+','+','+','o'},{'+','+','+','+','+','+'}};
    }

    public static short izracunajOdzivIndex(char[] pravaPar, char[] izbranaPar) {
        char[] prava = Arrays.copyOf(pravaPar, pravaPar.length);
        char[] izbrana = Arrays.copyOf(izbranaPar, izbranaPar.length);

        // Preverimo <null>, dolžino in sestavo (<izbrana> lahko vsebuje samo
        // male črke slovenske abecede).

        if (6 != izbrana.length) {
            throw new TestSkupno.WordleIzjema(String.format("Metoda <poteza> je vrnila besedo napačne dolžine (%d).", izbrana.length));
        }

        for (int i = 0; i < 6; i++) {
            char znak = izbrana[i];
            if (TestSkupno.ABECEDA.indexOf(znak) < 0) {
                throw new TestSkupno.WordleIzjema(String.format("Metoda <poteza> je vrnila besedo z neveljavnim znakom (%c).", znak));
            }
        }

        short odzivIndeks = 0;

        for (int i = 0; i < 6; i++) {
            if (prava[i] == izbrana[i]) {
                odzivIndeks += (short) (2 * Math.pow(3, 6 - 1 - i));
                prava[i] = '#';
                izbrana[i] = '_';
            }
        }

        // Poiščemo pravilne črke na napačnih mestih.

        for (int ixIzbrana = 0; ixIzbrana < 6; ixIzbrana++) {
            char crka = izbrana[ixIzbrana];
            if (crka != '_') {
                int ixPrava = index(prava, crka);
                if (ixPrava >= 0) {
                    odzivIndeks += (short) (1 * Math.pow(3, 6 - 1 - ixIzbrana));
                    prava[ixPrava] = '#';
                    izbrana[ixIzbrana] = '_';
                }
            }
        }

        return odzivIndeks;
    }

    public static char[] izracunajOdziv(char[] pravaPar, char[] izbranaPar) {
        char[] prava = Arrays.copyOf(pravaPar, pravaPar.length);
        char[] izbrana = Arrays.copyOf(izbranaPar, izbranaPar.length);

        if (6 != izbrana.length) {
            throw new TestSkupno.WordleIzjema(String.format("Metoda <poteza> je vrnila besedo napačne dolžine (%d).", izbrana.length));
        }

        for (int i = 0; i < 6; i++) {
            char znak = izbrana[i];
            if (TestSkupno.ABECEDA.indexOf(znak) < 0) {
                throw new TestSkupno.WordleIzjema(String.format("Metoda <poteza> je vrnila besedo z neveljavnim znakom (%c).", znak));
            }
        }

        char[] odziv = "------".toCharArray();

        for (int i = 0; i < 6; i++) {
            if (prava[i] == izbrana[i]) {
                odziv[i] = '+';
                prava[i] = '#';
                izbrana[i] = '_';
            }
        }

        for (int ixIzbrana = 0; ixIzbrana < 6; ixIzbrana++) {
            char crka = izbrana[ixIzbrana];
            if (crka != '_') {
                int ixPrava = index(prava, crka);
                if (ixPrava >= 0) {
                    odziv[ixIzbrana] = 'o';
                    prava[ixPrava] = '#';
                    izbrana[ixIzbrana] = '_';
                }
            }
        }

        return odziv;
    }

    public static char[][] pretvoriBesede(Set<String> besede) {
        /*
        pretvorimo Set<String> v 2D char tabelo zaradi hitrosti
        */

        int stBesed = besede.size();
        char[][] tabelaBesed = new char[stBesed][6];

        short indeks = 0;
        for (String beseda: besede) {
            tabelaBesed[indeks] = beseda.toCharArray();
            indeks++;
        }

        return tabelaBesed;
    }
}
