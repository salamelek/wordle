import java.util.*;

public class Tekm_63230387_new implements Stroj {
    private char[][] filtriraneBesede;
    private char[][] zacetneBesede;
    private char[][] slovar;
    private char[][] ugotovljeneBesede;
    private char[] prejsnjaIzbira;
    private char[] mozneCrke;
    private int dolzinaBesed;


    @Override
    public void inicializiraj(Set<String> besede) {
        this.dolzinaBesed = 6;
        this.mozneCrke = "abcdefghijklmnoprstuvz".toCharArray();
        this.slovar = pretvoriBesede(besede);
        this.zacetneBesede = pretvoriBesede(besede);
        this.ugotovljeneBesede = new char[0][this.dolzinaBesed];
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

        this.filtriraneBesede = filtrirajBesede(this.filtriraneBesede,this.prejsnjaIzbira , odziv);

//        System.out.println(Arrays.deepToString(this.filtriraneBesede));

        boolean[] razlike = seSplacaIskatCrke();
        if (razlike != null) {
            return new String(this.prejsnjaIzbira = vrniBesedoZaIskanjeCrk(razlike));
        }

        if (this.filtriraneBesede.length < 6) {
            return new String(this.prejsnjaIzbira = vrniOptimalnoBesedo2());
        }

        return new String(this.prejsnjaIzbira = vrniOptimalnoBesedo());
    }

    public boolean[] seSplacaIskatCrke() {
        /**
         * Pregleda za koliko bi se znižala dolžina seznama
         * */

        boolean[] razlike = vrniTabeloRazlik();
        int stRazlik = kolikoVTabeli(razlike, true);

        // testiraj katera možnost je najboljsa

        // TODO za zdaj se zdi, da stRazlik je najboljše <= 4 in količina besed > (2 * stRazlik)
        // TODO ne vem pa še če je to to... morda lahko še kaj optimiziram
        if (stRazlik <= 4 && stRazlik >= 1 && this.filtriraneBesede.length > (2 * stRazlik)) {
            return razlike;
        }

        return null;
    }

    public boolean[] vrniTabeloRazlik() {
        /**
         * Vrne tabelo boolean[this.dolzinaBesed], kjer true pomeni, da se po indeksu besede razlikujejo
         * Raje tako pregledamo namesto rabiti odziva, ker je možno da s tem si prišparamo potezo
         * */

        boolean[] tabelaIndeksov = new boolean[6];
        char[] prvaBeseda = this.filtriraneBesede[0];

        outer:
        for (int i=0; i<this.dolzinaBesed; i++) {
            for (int j=1; j<this.filtriraneBesede.length; j++) {
                char[] beseda = this.filtriraneBesede[j];

                if (prvaBeseda[i] != beseda[i]) {
                    tabelaIndeksov[i] = true;
                    continue outer;
                }
            }
        }

        return tabelaIndeksov;
    }

    public char[] vrniBesedoZaIskanjeCrk(boolean[] razlike) {
        /**
         * Ustvarimo tabelo, ki vsebuje črke, ki nam bodo povedale katera je manjkakoča črka
         * To rabimo v primerih odziva "-+++++", saj ugotavljati vsako besedo posebej bi bilo zamudno
         * */

        // ne indeksu crke shranimo kolikokrat se pojavi (kot ena mapa)
        // lahko bi rabil dolzino [('z' + 1) - 'a'], ampak se mi nece
        int[] stCrk = new int['z' + 1];

        // zapiši koliko vsakih črk je
        for (int i=0; i<razlike.length; i++) {
            boolean jeRazlika = razlike[i];

            if (!jeRazlika) {
                continue;
            }

            for (char[] beseda: this.filtriraneBesede) {
                stCrk[beseda[i]]++;
            }
        }

        char[] besedaZaIskanje = "aaaaaa".toCharArray();
        for (int i=0; i<6; i++) {
            char najCrka = vrniNajCrko(stCrk);

            if (stCrk[najCrka] == 0) {
                break;
            }

            besedaZaIskanje[i] = najCrka;
            stCrk[najCrka] = 0;
        }


        return besedaZaIskanje;
    }

    public char[] vrniOptimalnoBesedo2() {
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
         *
         * namesto računat informacije, bi računal samo katera beseda da najbolj raven graf
         * */

//        System.out.println("rabim 2!");

        // vsi guess bojo samo obstoječe besede (ker vseh možnih kombinacij črk je 22^6 :')
        double[] povprecnoBesedManj = new double[this.filtriraneBesede.length];

        for (int i=0; i<this.filtriraneBesede.length; i++) {
            char[] beseda = this.filtriraneBesede[i];
            char[][] mozniOdzivi = vrniMozneOdzive(beseda);
            int[] stStevilBesedManj = new int[mozniOdzivi.length];

            for (int j = 0; j < mozniOdzivi.length; j++) {
                char[] mozenOdziv = mozniOdzivi[j];
                int stBesedManj = kolikoBesedOdstrani(beseda, mozenOdziv);

                stStevilBesedManj[j] = stBesedManj;
            }

            double povprecje = povprecje(stStevilBesedManj);
            povprecnoBesedManj[i] = povprecje;
        }

        double najvecBesedManj = max(povprecnoBesedManj);
        int indeksNajBesede = dobiIndex(povprecnoBesedManj, najvecBesedManj);

        return this.filtriraneBesede[indeksNajBesede];
    }

    public char[] vrniOptimalnoBesedo() {
        /**
         * Izračuna katera je najbolj popularna črka na določenem indeksu
         * Če je to možno, ne ponavlja črk
         *
         * Dejansko s to funkcijo poskusim dobiti čim več pravilnih črk v odzivu
         * */

        // indeks števila predstavlja ascii code char-a
        int[][] mapaStCrk = vrniMapoStCrk();
        char[] najCrke = new char[this.dolzinaBesed];

        int najCrkeCounter = 0;
        for (int i=0; i<this.dolzinaBesed; i++) {
            char najCrka = 0;
            int najPojav = -1;

            for (char crka='a'; crka<='z'; crka++) {
                if (kolikoVTabeli(najCrke, crka) > 0 && !moramRabimIstoCrko()) {
                    continue;
                }

                int[] stCrk = mapaStCrk[crka];

                if (stCrk[i] > najPojav) {
                    najPojav = stCrk[i];
                    najCrka = crka;
                }
            }

            najCrke[najCrkeCounter] = najCrka;
            najCrkeCounter++;
        }

        return najCrke;
    }

    public boolean moramRabimIstoCrko() {
        /**
         * Metoda, ki preveri kdaj se mora rabiti iste črke pri optimalni besedi.
         * Če ne bi tega pregledali, ne bi nikoli ugotovili besed, ki imajo ponavljajoče črke
         * */

        for (char[] beseda: this.filtriraneBesede) {
            if (!imaPonavljajoceCrke(beseda)) {
                return false;
            }
        }

        return true;
    }

    public boolean imaPonavljajoceCrke(char[] beseda) {
        /**
         * Pregleda, če ima dana beseda ponavljajoče se črke
         * */

        for (int i=0; i<beseda.length; i++) {
            for (int j=0; j<beseda.length; j++) {
                if (i == j) {
                    continue;
                }

                if (beseda[i] == beseda[j]) {
                    return true;
                }
            }
        }

        return false;
    }

    public int[][] vrniMapoStCrk() {
        /**
         * Vrne "mapo" v obili int[][].
         * Ključ je char črka (ki jo lahko interpretiramo kot int)
         * Vrednost pa je int[], ki šteje kolikokrat se je črka pojavila na katerem indeksu
         * npr. če hočemo indekse črke r, poiščemo tabelo['r']
         * Vem, da se to dela z mapami, ampak vem tudi da so bolj počasne kot tabele
         * */

        char[] crke = "abcdefghijklmnoprstuvz".toCharArray();
        int[][] mapaStCrk = new int['z' + 1][this.dolzinaBesed];

        for (char crka: crke) {
            for (char[] beseda: this.filtriraneBesede) {
                int[]indeksiCrk = vrniVseIndekse(beseda, crka);

                if (indeksiCrk == null) {
                    continue;
                }

                for (int indeks: indeksiCrk) {
                    mapaStCrk[crka][indeks]++;
                }
            }
        }

        return mapaStCrk;
    }

    public int[] vrniVseIndekse(char[] beseda, char crka) {
        /**
         * Vrne tabelo int[] vseh indeksov iskane črke v besedi
         * npr. beseda: banana | iskana: a => [1, 3, 5]
         * */

        int stCrk = kolikoVTabeli(beseda, crka);

        if (stCrk == 0) {
            return null;
        }

        int[] vsiIndeksi = new int[stCrk];

        int indeksiCounter = 0;
        for (int i=0; i<beseda.length; i++) {
            char crkaVBesedi = beseda[i];

            if (crkaVBesedi == crka) {
                vsiIndeksi[indeksiCounter] = i;
                indeksiCounter++;
            }
        }

        return vsiIndeksi;
    }

    public double povprecje(int[] tabela) {
        double vsota = 0;

        for (int st: tabela) {
            vsota += st;
        }

        return vsota / tabela.length;
    }

    public double max(double[] tabela) {
        // vem, da ni optimalno, ampak mi ne rabi več od tega
        double naj = 0;

        for (double st: tabela) {
            if (st > naj) {
                naj = st;
                continue;
            }

//            if (st <= 0) {
//                throw new RuntimeException("max() metoda dela le s pozitivnimi števili!");
//            }
        }

        return naj;
    }

    public char[][] vrniMozneOdzive(char[] zacetnaBeseda) {
        /**
         * vrne seznam vseh možnih odzivov in njihovo pogostost glede na začetno besedo
         * */

        // seveda rabi this.filtriraneBesede
        char[][] mozniOdzivi = new char[0][this.dolzinaBesed];
        for (char[] beseda: this.filtriraneBesede) {
            char[] odziv = pretvoriOdziv(TestSkupno.izracunajOdziv(new String(zacetnaBeseda), new String(beseda)));
            mozniOdzivi = dodajVTabeloCeNiZe(mozniOdzivi, odziv);
        }

        return mozniOdzivi;
    }

    public char[][] dodajVTabeloCeNiZe(char[][] mozniOdzivi, char[] odziv) {
        for (char[] odziv2: mozniOdzivi) {
            if (odziv2 == odziv) {
                return mozniOdzivi;
            }
        }

        char[][] mozniOdzivi2 = new char[mozniOdzivi.length + 1][this.dolzinaBesed];
        mozniOdzivi2[mozniOdzivi.length] = odziv;

        return mozniOdzivi2;
    }

    public int kolikoBesedOdstrani(char[] beseda, char[] odziv) {
        char[][] tempBesede = Arrays.copyOf(this.filtriraneBesede, this.filtriraneBesede.length);

        char[][] filtriraneTemp = filtrirajBesede(tempBesede, beseda, odziv);

        return tempBesede.length - filtriraneTemp.length;
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

    public char[][] filtrirajBesede(char[][] besede, char[] prejsnjaBeseda, char[] odziv) {
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
                    pravilneCrke[i] = prejsnjaBeseda[i];
                    break;
                case 'o':
                    ugodneCrke[i] = prejsnjaBeseda[i];
                    break;
                case '-':
                    napacneCrke[i] = prejsnjaBeseda[i];
                    break;
            }
        }

        outer:
        for (int i=0; i<besede.length; i++) {
            char[] beseda = besede[i];

            // pravilne črke
            for (int j = 0; j < pravilneCrke.length; j++) {
                char pravilnaCrka = pravilneCrke[j];

                if (pravilnaCrka == 0) {
                    continue;
                }

                if (pravilnaCrka != beseda[j]) {
                    besede[i] = null;
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
                    besede[i] = null;
                    continue outer;
                }

                // odstrani besedo, če ima manj ugodnih črk kakor jih je v odzivu
                if (kolikoVTabeli(beseda, ugodnaCrka) < kolikoVTabeli(ugodneCrke, ugodnaCrka)) {
                    besede[i] = null;
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
                    besede[i] = null;
                    continue outer;
                }
            }
        }

        return odstraniVseNull(besede);
    }

    public int dobiIndex(double[] tabela, double iskani) {
        /**
         * Vrne prvi indeks iskane črke v besedi (ali pa -1 če črke v besedi ni)
         * */

        for (int i=0; i<tabela.length; i++) {
            if (tabela[i] == iskani) {
                return i;
            }
        }

        return -1;
    }

    public int kolikoVTabeli(boolean[] tabela, boolean iskani) {
        /**
         * Vrne število iskanega elementa v tabeli (vrednosti true al false)
         * */

        int stevilo = 0;

        for (boolean element: tabela) {
            if (element == iskani) {
                stevilo++;
            }
        }

        return stevilo;
    }

    public char vrniNajCrko(int[] stCrk) {
        /**
         * Vrne črko, ki se največ pojavlja glede na "mapo" stCrk
         * */

        char najCrka = 0;
        int najPojav = -1;

        for (char crka='a'; crka<='z'; crka++) {
            if (stCrk[crka] > najPojav) {
                najCrka = crka;
                najPojav = stCrk[crka];
            }
        }

        return najCrka;
    }
}
