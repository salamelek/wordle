import java.util.*;

public class Tekm_63230387_3b1b implements Stroj {
    private char[][] filtriraneBesede;
    private char[][] zacetneBesede;
    private char[][] ugotovljeneBesede;
    private char[] prejsnjaIzbira;
    private int dolzinaBesed;
    private int kolikoBesedPrej;


    @Override
    public void inicializiraj(Set<String> besede) {
        this.dolzinaBesed = 6;
        this.zacetneBesede = pretvoriBesede(besede);
        this.kolikoBesedPrej = this.zacetneBesede.length;

//        this.zacetneBesede = new char[][] {
//                "tutati".toCharArray(),
//                "utatut".toCharArray(),
//                "brtoal".toCharArray(),
//                "sklqjd".toCharArray(),
//                "askdpj".toCharArray(),
//                "batoal".toCharArray(),
//        };
    }

    @Override
    public String poteza(List<Character> odzivSeznam) {
        // Prva poteza
        if (odzivSeznam == null) {
            /*
            Tukaj re-inicializiramo vse spremenljivke (ker dobimo novo besedo) in
            vrnemo našo prvo besedo
            */

            this.filtriraneBesede = Arrays.copyOf(this.zacetneBesede, this.zacetneBesede.length);

            return new String(this.prejsnjaIzbira = vrniOptimalnoBesedo());
        }

        // pretvorimo tudi odziv v char[]
        char[] odziv = pretvoriOdziv(odzivSeznam);

        // Smo ugotovili besedo
        if (kolikoVTabeli(odziv, '+') == this.dolzinaBesed) {
            dodajUgotovljenoBesedo();
            odstaniUgotovljenoBesedo();

            return null;
        }

        filtrirajBesede(odziv);

//        System.out.println(Arrays.deepToString(this.filtriraneBesede));

        return new String(this.prejsnjaIzbira = vrniOptimalnoBesedo());
    }

    public char[] vrniOptimalnoBesedo() {
        /**
         * Koncepti in ideje od "3Blue1Brown"
         * https://www.youtube.com/watch?v=v68zYyaEmEA
         * https://www.youtube.com/watch?v=fRed0Xmc2Wg
         *
         * Izračunamo koliko informacije ti da beseda in koliko je to dejansko možno
         * To lahko tudi rekurzivno implementiramo, da pogleda za n naslednjih korakov
         *
         * Prvo ali prvi 2 optimalni besedi bi si lahko pre-computali,
         * a ne bodo več optimalne, ko odstranimo že ugotovljene besede s slovarja
         * (bo treba optimizirat čas vs natančnost)
         *
         * lahko bi celo computali optimalno besedo vsakih n-poskusov
         * */

        int kolikoBesedZdaj = this.filtriraneBesede.length;

        int p = this.kolikoBesedPrej / kolikoBesedZdaj;
        double info = log2(1./p);

        this.kolikoBesedPrej = kolikoBesedZdaj;
        return "abadef".toCharArray();
    }

    public static int log2(double N) {
        return (int)(Math.log(N) / Math.log(2));
    }

    public char[][] pretvoriBesede(Set<String> besede) {
        /**
         * Pretvorimo Set<String> v 2D char tabelo zaradi hitrosti
         * */

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
        /**
         * Podobno kot slovar, tudi odziv pretvorimo v char arry za hitrejši dostop
         * */

        char[] odziv = new char[this.dolzinaBesed];

        for (int i=0; i<this.dolzinaBesed; i++) {
            odziv[i] = odzivSeznam.get(i);
        }

        return odziv;
    }

    public int kolikoVTabeli(char[][] tabela, char[] iskani) {
        /**
         * Vrne število iskanega elementa v tabeli (besede v slovarju)
         * */

        int stevilo = 0;

        for (char[] element: tabela) {
            if (Arrays.equals(element, iskani)) {
                stevilo++;
            }
        }

        return stevilo;
    }

    public int kolikoVTabeli(char[] tabela, char iskani) {
        /**
         * Vrne število iskanega elementa v tabeli (črke v besedi)
         * */

        int stevilo = 0;

        for (char element: tabela) {
            if (element == iskani) {
                stevilo++;
            }
        }

        return stevilo;
    }

    public void dodajUgotovljenoBesedo() {
        /**
         * Dodamo v seznam ugotovljenih besed prvakar ugotovljeno besedo
         * Rabili bomo ta seznam za seed cracking
         * */

        // this.ugotovljeneBesede.add(new String(this.prejsnjaIzbira));
        int dolzinaPrejsnje = this.ugotovljeneBesede.length;
        char[][] novSeznam = new char[dolzinaPrejsnje + 1][this.dolzinaBesed];

        System.arraycopy(this.ugotovljeneBesede, 0, novSeznam, 0, dolzinaPrejsnje);

        novSeznam[dolzinaPrejsnje] = this.prejsnjaIzbira;

        this.ugotovljeneBesede = novSeznam;
    }

    public void odstaniUgotovljenoBesedo() {
        /**
         * Odstrani ugotovljeno besedo iz slovarja
         * Na tak način, za vsako novo besedo lahko imamo besedo manj za iskanje
         * */

        for (int i=0; i<this.zacetneBesede.length; i++) {
            if (Arrays.equals(this.zacetneBesede[i], this.prejsnjaIzbira)) {
                this.zacetneBesede[i] = null;
                break;
            }
        }

        this.zacetneBesede = odstraniVseNull(this.zacetneBesede);
    }

    public char[][] odstraniVseNull(char[][] seznam) {
        /**
         * Ker rabim tabele, namesto brisati elemente, jih nastavim na null
         * S to metodo, zbrišem vse null besede, da ne zgubljam časa pri iskanju
         * */

        int stNull = kolikoVTabeli(seznam, null);
        int novaDolzina = seznam.length - stNull;
        char[][] novaTabela = new char[novaDolzina][this.dolzinaBesed];

        int counterNove = 0;
        for (char[] beseda : seznam) {
            if (beseda == null) {
                continue;
            }

            novaTabela[counterNove] = beseda;
            counterNove++;
        }

        return novaTabela;
    }

    public void filtrirajBesede(char[] odziv) {
        /**
         * 1) odstrani vse besede, ki nimajo pravilnih črk
         * 2) odstrani vse besede, ki:
         *      > nimajo ugodne črke (pazi na pravilne črke ;-;)
         *      > imajo ugodno črko na istem mestu kot odziv
         *      > imajo manj ugodnih črk kot odziv (seveda pa je treba šteti tudi pravilne črke ;-;)
         *      >
         *
         * NEVEMMMM MORDA SE DA TIPO ŠTET AL NEVEM KAJ
         * */

        char[] pravilneCrke = new char[this.dolzinaBesed];
        char[] ugodneCrke = new char[this.dolzinaBesed];
        char[] napacneCrke = new char[this.dolzinaBesed];

        for (int i=0; i<odziv.length; i++) {
            char znak = odziv[i];

            switch (znak) {
                case '+':
                    pravilneCrke[i] = this.prejsnjaIzbira[i];
                    break;
                case 'o':
                    ugodneCrke[i] = this.prejsnjaIzbira[i];
                    break;
                case '-':
                    napacneCrke[i] = this.prejsnjaIzbira[i];
                    break;
            }
        }

        outer:
        for (int i=0; i<this.filtriraneBesede.length; i++) {
            char[] beseda = this.filtriraneBesede[i];

            // pravilne črke
            for (int j = 0; j < pravilneCrke.length; j++) {
                char pravilnaCrka = pravilneCrke[j];

                if (pravilnaCrka == 0) {
                    continue;
                }

                if (pravilnaCrka != beseda[j]) {
                    this.filtriraneBesede[i] = null;
                    continue outer;
                }
            }

            // ugodne črke
            for (int j = 0; j < ugodneCrke.length; j++) {
                char ugodnaCrka = ugodneCrke[j];

                if (ugodnaCrka == 0) {
                    continue;
                }

                // odstrani besedo, če ima ugodno črko na istem indeksu
                if (ugodnaCrka == beseda[j]) {
                    this.filtriraneBesede[i] = null;
                    continue outer;
                }

                // odstrani besedo, če ima manj ugodnih črk kakor jih je v odzivu
                if (kolikoVTabeli(beseda, ugodnaCrka) < kolikoVTabeli(ugodneCrke, ugodnaCrka)) {
                    this.filtriraneBesede[i] = null;
                    continue outer;
                }
            }

            // napačne črke
            for (char napacnaCrka : napacneCrke) {
                if (napacnaCrka == 0) {
                    continue;
                }

                // odstrani besedo, če ima napačno črko (ki ni pravilna ali ugodna)
                if (kolikoVTabeli(pravilneCrke, napacnaCrka) + kolikoVTabeli(ugodneCrke, napacnaCrka) < kolikoVTabeli(beseda, napacnaCrka)) {
                    this.filtriraneBesede[i] = null;
                    continue outer;
                }
            }
        }

        this.filtriraneBesede = odstraniVseNull(this.filtriraneBesede);
    }
}
