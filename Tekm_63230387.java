import java.util.*;


public class Tekm_63230387 implements Stroj {
    private int dolzinaBesed;
    private char[][] zacetneBesede;
    private char[][] filtriraneBesede;
    private char[] prejsnjaIzbira;
    private Set<String> ugotovljeneBesede;

    @Override
    public void inicializiraj(Set<String> besede) {
        this.dolzinaBesed = 6;
        this.zacetneBesede = pretvoriBesede(besede);
    }

    @Override
    public String poteza(List<Character> odzivSeznam) {
        if (odzivSeznam == null) {
            /*
            Prva poteza

            Tukaj re-inicializiramo vse spremenljivke (ker dobimo novo besedo) in
            vrnemo našo prvo besedo
            */

            this.filtriraneBesede = Arrays.copyOf(this.zacetneBesede, this.zacetneBesede.length);

            return new String(this.prejsnjaIzbira = vrniOptimalnoBesedo());
        }

        if (ugotovljeneBesede.size() == 10) {
            // bruteforce 100000 seeds
            int numSeeds = 50000;
            for (int i=-numSeeds; i<numSeeds; i++) {
                
            }
        }

        // pretvorimo tudi odziv v char[]
        char[] odziv = pretvoriOdziv(odzivSeznam);

        if (Arrays.equals(odziv, new char[]{'+', '+', '+', '+', '+', '+'})) {
            /*
            Ugotovili smo besedo!
            */
            return null;
        }

//        System.out.println(Arrays.deepToString(this.filtriraneBesede));

        filtrirajBesede(odziv);

//        System.out.println(Arrays.deepToString(this.filtriraneBesede));

        return null;
    }


    public char[][] pretvoriBesede(Set<String> besede) {
        /*
        Pretvorimo Set<String> v 2D tabelo tipa char
        Zakaj? se mi je zdelo, da je bolj hitro :>
         */
        int stBesed = besede.size();
        char[][] tabelaBesed = new char[stBesed][this.dolzinaBesed];

        int indeks = 0;
        for (String beseda: besede) {
            tabelaBesed[indeks] = beseda.toCharArray();
            indeks++;
        }

        return tabelaBesed;
    }

    public char[] pretvoriOdziv(List<Character> odzivSeznam) {
        char[] odziv = new char[this.dolzinaBesed];

        for (int i=0; i<this.dolzinaBesed; i++) {
            odziv[i] = odzivSeznam.get(i);
        }

        return odziv;
    }

    public char[] vrniOptimalnoBesedo() {
        return this.filtriraneBesede[0];
    }

    public void filtrirajBesede(char[] odziv) {
        /*
        Odstranimo vse besede, ki ne morejo biti pravilne
        */

        char[] pravilneCrke = new char[this.dolzinaBesed];
        char[] ugodneCrke = new char[this.dolzinaBesed];
        char[] napacneCrke = new char[this.dolzinaBesed];

        // napišemo v tabele pripadajoče črke
        for (int i=0; i<this.dolzinaBesed; i++) {
            char znakOdziva = odziv[i];

            switch (znakOdziva) {
                case '+':
                    pravilneCrke[i] = this.prejsnjaIzbira[i];
                    break;

                case 'o':
                    ugodneCrke[i] = this.prejsnjaIzbira[i];
                    break;

                case '-':
                    napacneCrke[i] = this.prejsnjaIzbira[i];
                    break;

                default:
                    throw new RuntimeException("Kako je do tega sploh prišlo??");
            }
        }

        System.out.println(Arrays.toString(pravilneCrke));
        System.out.println(Arrays.toString(ugodneCrke));
        System.out.println(Arrays.toString(napacneCrke));

        // FIXME nadaljuj debugging tu

        // nastavimo vse napačne besede = null
        for (int i=0; i<this.filtriraneBesede.length; i++) {
            char[] beseda = this.filtriraneBesede[i];

            if (beseda == null) {
                continue;
            }

            // odstanimo vse besede brez pravilnih črk
            for (int j=0; j<this.dolzinaBesed; j++) {
                if (pravilneCrke[j] != 0 && pravilneCrke[j] != beseda[j]) {
                    this.filtriraneBesede[i] = null;
                    break;
                }
            }

            // odstanimo vse besede brez ugodnih črk
            for (int j=0; j<this.dolzinaBesed; j++) {
                if (ugodneCrke[j] != 0 && kolikoVTabeli(beseda, this.prejsnjaIzbira[j]) < 1) {
                    this.filtriraneBesede[i] = null;
                    break;
                }
            }


            // odstanimo vse besede z napačnimi črkami
            for (int j=0; j<this.dolzinaBesed; j++) {
                if (napacneCrke[j] != 0 && kolikoVTabeli(beseda, this.prejsnjaIzbira[j]) > 0) {
                    this.filtriraneBesede[i] = null;
                    break;
                }
            }
        }


        // odstranimo vse null iz tabele
        int stNull = kolikoVTabeli(this.filtriraneBesede, null);

        System.out.println(stNull);
        int novaDolzina = this.filtriraneBesede.length - stNull;

        if (novaDolzina < 1) {
            throw new RuntimeException(":')");
        }

        char[][] novaTabela = new char[novaDolzina][this.dolzinaBesed];

        int counterNove = 0;
        for (char[] beseda : this.filtriraneBesede) {
            if (this.filtriraneBesede == null) {
                continue;
            }

            novaTabela[counterNove] = beseda;
            counterNove++;
        }

        this.filtriraneBesede = novaTabela;
    }

    public int kolikoVTabeli(char[][] tabela, char[] iskani) {
        int stevilo = 0;

        for (char[] element: tabela) {
            if (Arrays.equals(element, iskani)) {
                stevilo++;
            }
        }

        return stevilo;
    }

    public int kolikoVTabeli(char[] tabela, char iskani) {
        int stevilo = 0;

        for (char element: tabela) {
            if (element == iskani) {
                stevilo++;
            }
        }

        return stevilo;
    }
}
