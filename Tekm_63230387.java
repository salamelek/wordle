import java.util.*;


public class Tekm_63230387 implements Stroj {
    private int dolzinaBesed;
    private char[][] zacetneBesede;
    private char[][] filtriraneBesede;
    private char[] prejsnjaIzbira;
    private char[][] slovar;
    private char[][] ugotovljeneBesede;
    private boolean semUgotovilSeed;
    private char[][] pravilneBesede;
    private int pravilneBesedeCounter;
    private int stBesedZaSeed;
    private boolean seedJeBilZgresen;

    @Override
    public void inicializiraj(Set<String> besede) {
        this.dolzinaBesed = 6;
        this.semUgotovilSeed = false;
        this.seedJeBilZgresen = false;
        this.stBesedZaSeed = 10;
        this.pravilneBesedeCounter = this.stBesedZaSeed;
        this.zacetneBesede = pretvoriBesede(besede);
        this.slovar = this.zacetneBesede;
        this.ugotovljeneBesede = new char[0][this.dolzinaBesed];
    }

    @Override
    public String poteza(List<Character> odzivSeznam) {
        // SEED CRACKING
        // TODO preveri 230547 krat da se ne pokvari ce ugotovi zgresen seed
        if (this.ugotovljeneBesede.length == this.stBesedZaSeed && !this.semUgotovilSeed && !this.seedJeBilZgresen) {
            int seconds = 1 * 60; // bo pogledalo bodisi v - kot v +

            crackSeed(seconds);
        }


        // Prva poteza
        if (odzivSeznam == null) {
            /*
            Tukaj re-inicializiramo vse spremenljivke (ker dobimo novo besedo) in
            vrnemo našo prvo besedo
            */

            this.filtriraneBesede = Arrays.copyOf(this.zacetneBesede, this.zacetneBesede.length);

            if (this.semUgotovilSeed) {
                return new String (vrniZnanoBesedo());
            }

            return new String(this.prejsnjaIzbira = vrniOptimalnoBesedo());
        }

        // pretvorimo tudi odziv v char[]
        char[] odziv = pretvoriOdziv(odzivSeznam);

        // Smo ugotovili
        if (kolikoVTabeli(odziv, '+') == this.dolzinaBesed) {
            /*
            Ugotovili smo besedo!
            */
            dodajUgotovljenoBesedo();

            // odstranimo že ugotovljeno besedo
            odstaniUgotovljenoBesedo();

            return null;
        }

        // preveri ali smo ugotovili napačen seed
        if (this.semUgotovilSeed && kolikoVTabeli(odziv, '+') != this.dolzinaBesed) {
            System.out.println("AAAAAAAAAAAAH SEED JE BIL NAPAČEN KSDJFHNKSDJFNSKDHJFNBKJAHSDAISDJF");
            System.out.println(odziv);
            System.out.println(this.prejsnjaIzbira);
            this.semUgotovilSeed = false;
            this.seedJeBilZgresen = true;
        }

        // =============================
        // VV - Normalni potek igre - VV
        if (this.semUgotovilSeed) {
            return new String (vrniZnanoBesedo());
        }

        // odstranimo besede, ki niso več relevantne
        filtrirajBesede(odziv);

//        System.out.println(Arrays.deepToString(this.filtriraneBesede));

        boolean[] razlike = vrniTabeloRazlik();
        int stRazlik = kolikoVTabeli(razlike, true);
        // TODO za zdaj se zdi, da stRazlik je najboljše <= 4 in količina besed > (2 * stRazlik)
        // TODO ne vem pa še če je to to... morda lahko še kaj optimiziram
        if (stRazlik <= 4 && stRazlik >= 1 && this.filtriraneBesede.length > (2 * stRazlik)) {
//            System.out.printf("%niscem crke (st: %d)%n", stRazlik);
            return new String(this.prejsnjaIzbira = vrniBesedoZaIskanjeCrk(razlike));
        }


//        System.out.println("\niscem optimalno");
        return new String(this.prejsnjaIzbira = vrniOptimalnoBesedo());
    }


    public void dodajUgotovljenoBesedo() {
        /*
        doda ugotovljeno besedo v seznam
        */

        // this.ugotovljeneBesede.add(new String(this.prejsnjaIzbira));
        int dolzinaPrejsnje = this.ugotovljeneBesede.length;
        char[][] novSeznam = new char[dolzinaPrejsnje + 1][this.dolzinaBesed];

        for (int i=0; i<dolzinaPrejsnje; i++) {
            novSeznam[i] = this.ugotovljeneBesede[i];
        }

        novSeznam[dolzinaPrejsnje] = this.prejsnjaIzbira;

        this.ugotovljeneBesede = novSeznam;
    }

    public char[] vrniZnanoBesedo() {
        this.pravilneBesedeCounter++;
        return this.pravilneBesede[this.pravilneBesedeCounter - 1];
    }

    public void crackSeed(int seconds) {
        /*
        Išče semena od 0 v obe smeri (+-)
        dokler ne preseže časa podanega v parametru seconds
        */

        System.out.printf("%n'Investiram' %d sekund za iskanje semena...%n", seconds);

        long start = System.currentTimeMillis();
        long end = start + seconds * 1000;

        int i = 0;

        outer:
        while (System.currentTimeMillis() < end) {
            sign:
            for (int sign=-1; sign<=1; sign+=2) {
                char[][] besede = Arrays.copyOf(this.slovar, this.slovar.length);
                besede = shuffleArray(besede, i * sign);

                // FIXME ZAKAJ POGOSTO KO UGOTOVI SEED JE PRVA BESEDA NULL??????

                char[][] first10Elements = Arrays.copyOfRange(besede, 0, Math.min(10, besede.length));
                System.out.printf("%d:%n", i * sign);
                System.out.println(Arrays.deepToString(first10Elements));
                System.out.println(Arrays.deepToString(this.ugotovljeneBesede));
                System.out.println("==================");

                // preglej prvih 10 besed
                for (int j=0; j<this.stBesedZaSeed; j++) {
                    if (Arrays.equals(besede[j], this.ugotovljeneBesede[j])) {
                        continue;
                    }

                    continue sign;
                }

                this.semUgotovilSeed = true;
                this.pravilneBesede = besede;

                System.out.printf("YESSSSS SEM GA UGOTOVIL: %d%n%n", i * sign);
                break outer;
            }

            i++;
        }

        if (!semUgotovilSeed) {
            System.out.printf("Na žalost ga nisem dobil :(%nPoiskal sem le semena od -%d do %d%n%n", i, i);
            this.seedJeBilZgresen = true;
        }
    }

    private static char[][] shuffleArray(char[][] besede2, long seed) {
        /*
        Najlepša hvala stricu internetu
        */

        Random random = new Random(seed);

        char[][] besede = Arrays.copyOf(besede2, besede2.length);

        for (int i=besede.length - 1; i>0; i--) {
            int index = random.nextInt(i + 1);

            char[] temp = besede[i];
            besede[i] = besede[index];
            besede[index] = temp;
        }

        return besede;
    }

    public boolean[] vrniTabeloRazlik() {
        /*
        Vrne tabelo boolean[6], kjer true pomeni, da se po indeksu razlikuje
        ne rabimo odziva, ker z odzivom je možno, da zgubimo potezo
        */

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

    public char vrniNajCrko(int[] stCrk) {
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

    public void odstaniUgotovljenoBesedo() {
        for (int i=0; i<this.zacetneBesede.length; i++) {
            if (Arrays.equals(this.zacetneBesede[i], this.prejsnjaIzbira)) {
                this.zacetneBesede[i] = null;
                break;
            }
        }

        this.zacetneBesede = odstraniVseNull(this.zacetneBesede);
    }

    public char[] pretvoriOdziv(List<Character> odzivSeznam) {
        char[] odziv = new char[this.dolzinaBesed];

        for (int i=0; i<this.dolzinaBesed; i++) {
            odziv[i] = odzivSeznam.get(i);
        }

        return odziv;
    }

    public char[] vrniOptimalnoBesedo() {
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
        /*
        moramo preveriti, če vse besede imajo vsaj eno ponavljajočo črko
        Če imajo, potem vrnemo false, čene true
        */
        for (char[] beseda: this.filtriraneBesede) {
            if (!imaPonavljajoceCrke(beseda)) {
                return false;
            }
        }

        return true;
    }

    public boolean imaPonavljajoceCrke(char[] beseda) {
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
        /*
        Vrne mapo števila črk
        Ta mapa predstavlja kolikokrat se je vsaka črka pojavila na vsakem mestu
        Ker pa nočem rabiti počaaaaaaaaaaaaasnihhhh map, bom rabil 2D int tabelo
        */

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

    public void filtrirajBesede(char[] odziv) {
        /*
        Odstranimo vse besede, ki ne morejo biti pravilne
        */

        // totalno moje delo
        for (int i=0; i<this.filtriraneBesede.length; i++) {
            char[] beseda = this.filtriraneBesede[i];

            if (!jeZdruzljiva(beseda, this.prejsnjaIzbira, odziv)) {
                this.filtriraneBesede[i] = null;
            }
        }

        // odstranimo vse null iz tabele
        this.filtriraneBesede = odstraniVseNull(this.filtriraneBesede);
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

    public int kolikoVTabeli(boolean[] tabela, boolean iskani) {
        int stevilo = 0;

        for (boolean element: tabela) {
            if (element == iskani) {
                stevilo++;
            }
        }

        return stevilo;
    }

    public char[][] odstraniVseNull(char[][] seznam) {
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

    public int dobiIndex(char[] beseda, char crka) {
        for (int i=0; i<beseda.length; i++) {
            if (beseda[i] == crka) {
                return i;
            }
        }

        return -1;
    }

    private boolean jeZdruzljiva(char[] besedaPrava, char[] izbiraPrava, char[] odziv) {
        // delamo s kopijo, da ne zamešamo vseka
        char[] beseda = Arrays.copyOf(besedaPrava, besedaPrava.length);
        char[] izbira = Arrays.copyOf(izbiraPrava, izbiraPrava.length);

        for (int i=0; i<this.dolzinaBesed; i++) {
            if (odziv[i] == '+') {
                if (beseda[i] != izbira[i]) {
                    return false;
                }

                beseda[i] = '#';
                izbira[i] = '_';
            }
        }

        // TODO lahko bi celo štel koliko črk je lahko
        for (int i=0; i<this.dolzinaBesed; i++) {
            if (odziv[i] == 'o') {
                char crka = izbira[i];
                int ixBeseda = dobiIndex(beseda, crka);

                if (ixBeseda < 0 || beseda[i] == crka) {
                    return false;
                }

                // Označimo, da smo pripadajoča položaja že pregledali.
                beseda[ixBeseda] = '#';
                izbira[i]= '_';
            }
        }

        for (int i=0; i<this.dolzinaBesed; i++) {
            if (odziv[i] == '-' && kolikoVTabeli(beseda, izbira[i]) > 0) {
                return false;
            }
        }

        return true;
    }
}
