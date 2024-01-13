import java.util.*;

public class UstvariTabelo {
    public static void main(String[] args) {
        Set<String> slovarCringe = TestSkupno.preberiSlovar("slovar.txt");
        char[][] slovar = pretvoriBesede(slovarCringe);

        char[][] mozniOdzivi = new char[729][6];
        short[][] tabelaOdzivov = new short[slovar.length][slovar.length];

        // ustvari tabelo moznih odzivov
        char[] znakiOdziva = new char[]{'-', 'o', '+'};
        short counter = 0;

        for (char i1=0; i1<3; i1++) {
            char znak1 = znakiOdziva[i1];

            for (char i2=0; i2<3; i2++) {
                char znak2 = znakiOdziva[i2];

                for (char i3=0; i3<3; i3++) {
                    char znak3 = znakiOdziva[i3];

                    for (char i4=0; i4<3; i4++) {
                        char znak4 = znakiOdziva[i4];

                        for (char i5=0; i5<3; i5++) {
                            char znak5 = znakiOdziva[i5];

                            for (char i6=0; i6<3; i6++) {
                                char znak6 = znakiOdziva[i6];

                                mozniOdzivi[counter] = new char[]{znak1, znak2, znak3, znak4, znak5, znak6};
                                counter++;
                            }
                        }
                    }
                }
            }
        }

        // dodeli indeks odziva paru besed (tudi te v indeksu
        for (short i=0; i<slovar.length; i++) {
            for (short j=0; j<slovar.length; j++) {
                char[] odziv = pretvoriOdziv(TestSkupno.izracunajOdziv(new String(slovar[i]), new String(slovar[j])));
                tabelaOdzivov[i][j] = dobiIndex(mozniOdzivi, odziv);
            }
        }

        printajTabelo(tabelaOdzivov);
    }

    public static char[][] pretvoriBesede(Set<String> besede) {
        int stBesed = besede.size();
        char[][] tabelaBesed = new char[stBesed][6];

        int indeks = 0;
        for (String beseda: besede) {
            tabelaBesed[indeks] = beseda.toCharArray();
            indeks++;
        }

        return tabelaBesed;
    }

    public static char[] pretvoriOdziv(List<Character> odzivSeznam) {
        char[] odziv = new char[6];

        for (int i=0; i<6; i++) {
            odziv[i] = odzivSeznam.get(i);
        }

        return odziv;
    }

    public static short dobiIndex(char[][] tabela, char[] odziv) {

        for (short i=0; i<tabela.length; i++) {
            if (Arrays.equals(tabela[i], odziv)) {
                return i;
            }
        }

        throw new RuntimeException(":(");
    }

    public static void printajTabelo(short[][] tabela) {
        StringBuilder sb = new StringBuilder();

        sb.append("{");

        for (short i = 0; i < tabela.length; i++) {
            sb.append("{");

            for (short j = 0; j < tabela[i].length; j++) {
                sb.append(shortToHex(tabela[i][j]));

                if (j != tabela[i].length - 1) {
                    sb.append(",");
                }
            }

            sb.append("}");

            if (i != tabela.length - 1) {
                sb.append(",");
            }
        }

        sb.append("}");
        System.out.print(sb);
    }

    public static String shortToHex(short value) {
        return Integer.toHexString(value & 0xFFFF);
    }

    // Convert hex back to short
    public static short hexToShort(String hexString) {
        return Short.parseShort(hexString, 16);
    }
}
