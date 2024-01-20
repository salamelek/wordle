import java.util.*;


public class Tekm_63230387 implements Stroj {
    private short pravilneBesedeCounter;    // šteje koliko besed smo ugotovili doslej
    private char[][] ugotovljeneBesede;     // tabela ugotovljenih besed
    private char[][] filtriraneBesede;      // seznam možnih besed (se filtrira za vsak odziv)
    private boolean seedJeBilZgresen;       // failsafe, če zgrešimo seed
    private boolean semUgotovilSeed;        // očitno
    private char[][] pravilneBesede;        // vse besede v pravilnem vrstnem redu (samo če smo ugotovili seed)
    private char[][] zacetneBesede;         // seznam začetnih besed (odstranimo ugotovljeno besedo)
    private char[] prejsnjaIzbira;          // očitno
    private short stBesedZaSeed;            // koliko besed hočemo nabrati, preden iščemo seed
    private char[][] slovar;                // slovar vseh možnih besed
    private long zacetek;                   // Čas začetka iskanja besed


    @Override
    public void inicializiraj(Set<String> besede) {
        /*
        Inicializiramo raznorazne vrednosti za pravilno delovanje stroja
        */

        this.stBesedZaSeed = 30;

        this.semUgotovilSeed = false;
        this.seedJeBilZgresen = false;
        this.pravilneBesedeCounter = 0;
        this.slovar = pretvoriBesede(besede);
        this.zacetneBesede = pretvoriBesede(besede);
        this.ugotovljeneBesede = new char[0][6];

        this.zacetek = System.currentTimeMillis();
    }

    @Override
    public String poteza(List<Character> odzivSeznam) {
        /*
        Pri vsaki potezi vrnemo besedo
        */

        // seed cracking
        if (this.ugotovljeneBesede.length == this.stBesedZaSeed) {
            if (!this.semUgotovilSeed && !this.seedJeBilZgresen) {
                long konec = System.currentTimeMillis();
                crackSeed(konec - this.zacetek);
            }
        }

        // Prva poteza
        if (odzivSeznam == null) {
            /*
            Tukaj re-inicializiramo vse spremenljivke (ker dobimo novo besedo) in
            vrnemo našo prvo besedo
            */

            this.filtriraneBesede = Arrays.copyOf(this.zacetneBesede, this.zacetneBesede.length);

            char[] beseda;
            if (this.semUgotovilSeed) {
                beseda = this.pravilneBesede[this.pravilneBesedeCounter];
            } else {
                // Če dam več kot 500 postane dokaj počasno
                if (this.zacetneBesede.length < 500) {
                    beseda = vrniBesedo_tockujOdziv();
                } else {
                    beseda = "kornea".toCharArray();
                }
            }

            return new String(this.prejsnjaIzbira = beseda);
        }

        // pretvorimo tudi odziv v char[]
        char[] odziv = pretvoriOdziv(odzivSeznam);

        // Smo ugotovili
        if (Arrays.equals(odziv, "++++++".toCharArray())) {
            /*
            Ugotovili smo besedo!
            */

            this.pravilneBesedeCounter++;

            // dodamo besedo v seznam ugotovljenih besed
            dodajUgotovljenoBesedo();

            // odstranimo besedo iz this.zacetneBesede
            odstaniUgotovljenoBesedo();

            return null;
        }

        // preveri ali smo ugotovili napačen seed
        if (this.semUgotovilSeed) {
            if (!(Arrays.equals(odziv, "++++++".toCharArray()))) {
                System.out.println("AAAAAAAAAAAAH SEED JE BIL NAPAČEN KSDJFHNKSDJFNSKDHJFNBKJAHSDAISDJF");
                this.semUgotovilSeed = false;
                this.seedJeBilZgresen = true;
            }
        }

        // =============================
        // VV - Normalni potek igre - VV
        // =============================


        // odstranimo besede, ki niso več relevantne
        this.filtriraneBesede = filtrirajBesede(this.filtriraneBesede, this.prejsnjaIzbira, odziv);

        // preverimo, če se splača iskati črke
        boolean[] razlike = seSplacaIskatCrke();
        if (razlike != null) {
            return new String(this.prejsnjaIzbira = vrniBesedo_iskaneCrke(razlike));
        }

        // vrnemo "optimalno" besedo
        return new String(this.prejsnjaIzbira = vrniBesedo_tockujOdziv());
    }


    public void crackSeed(long razlikaCasa) {
        /*
        Ko dosežemo želeno število ugotovljenih besed (this.stBesedZaSeed) začnemo s seed crackingom
        Dokler imamo še kaj časa, preverimo vsa možna števila od 0 do koliko nam pač uspe.
        Za vsak kandidat semena preverimo, če bi bilo prvih n členov enakih z novo premešanim slovarjem
        */

        long casNaBesedo = razlikaCasa / this.stBesedZaSeed;
        double estimatedTime = casNaBesedo * (this.slovar.length - this.stBesedZaSeed) / 1000.;

        estimatedTime *= 2./3; // pomnožimo z 2/3, da malo pospešimo
        int ostaliCas = 600 - (int) estimatedTime;
        ostaliCas = Math.min(ostaliCas, 510);

        System.out.printf("%n%dms%n", razlikaCasa);
        System.out.println("na besedo: " + casNaBesedo + "ms");
        System.out.println("estimated za vse besede: " + (int) estimatedTime + "s (worst case seveda)");
        System.out.printf("%n'Investiram' %d sekund za iskanje semena...%n", ostaliCas);

        long start = System.currentTimeMillis();
        long end = start + ostaliCas * 1000L;

        int seedCounter = -1;

        outer:
        while (System.currentTimeMillis() < end) {
            seedCounter++;
            Random random = new Random(seedCounter);
            char[][] besede = Arrays.copyOf(this.slovar, this.slovar.length);


            // shuffle array
            for (int i=besede.length - 1; i>0; i--) {
                int randInt = random.nextInt(i + 1);

                char[] tmp = besede[i];
                besede[i] = besede[randInt];
                besede[randInt] = tmp;

                // če vzamemo eno od začetnih besed, prekinemo
                // (~3X faster), (stBesedZaSeed / slovar.length) loss ~~> 0.000625 seed loss
                if (i > this.stBesedZaSeed && kolikoVTabeli(this.ugotovljeneBesede, besede[i]) > 0) {
                    continue outer;
                }
            }

            // preglej prvih 10 besed
            for (short j=0; j<this.stBesedZaSeed; j++) {
                if (Arrays.equals(besede[j], this.ugotovljeneBesede[j])) {
                    continue;
                }

                continue outer;
            }

            this.semUgotovilSeed = true;
            this.pravilneBesede = besede;

            System.out.printf("YESSSSS SEM GA UGOTOVIL: %d%n%n", seedCounter);

            break;
        }

        if (!semUgotovilSeed) {
            System.out.printf("Na žalost ga nisem dobil :(%nPoiskal sem le semena do %d%n%n", seedCounter);
            this.seedJeBilZgresen = true;
        }
    }

    public char[] vrniBesedo_iskaneCrke(boolean[] razlike) {
        /*
        Ustvarimo tabelo, ki vsebuje črke, ki nam bodo povedale katera je manjkakoča črka
        To rabimo v primerih odziva "-+++++", saj ugotavljati vsako besedo posebej bi bilo zamudno
        */

        // ne indeksu crke shranimo kolikokrat se pojavi (kot ena mapa)
        // lahko bi rabil dolzino [('z' + 1) - 'a'], ampak se mi nece
        short[] stCrk = new short['z' + 1];

        // zapiši koliko vsakih črk je
        for (short i=0; i<razlike.length; i++) {
            boolean jeRazlika = razlike[i];

            if (!jeRazlika) {
                continue;
            }

            for (char[] beseda: this.filtriraneBesede) {
                stCrk[beseda[i]]++;
            }
        }

        char[] besedaZaIskanje = "aaaaaa".toCharArray();
        for (short i=0; i<6; i++) {
            char najCrka = vrniNajCrko(stCrk);

            if (stCrk[najCrka] == 0) {
                break;
            }

            besedaZaIskanje[i] = najCrka;
            stCrk[najCrka] = 0;
        }


        return besedaZaIskanje;
    }

    public char[] vrniBesedo_tockujOdziv() {
        char[] najBeseda = "aaaaaa".toCharArray();
        int najTocke = -1;

        for (char[] beseda: this.filtriraneBesede) {
            int tocke = vrniTocke(this.filtriraneBesede, beseda);
            if (tocke > najTocke) {
                najTocke = tocke;
                najBeseda = beseda;
            }
        }

        return najBeseda;
    }

    public boolean[] seSplacaIskatCrke() {
        /*
        Pregleda za koliko bi se znižala dolžina seznama
        */

        boolean[] razlike = vrniTabeloRazlik();
        short stRazlik = kolikoVTabeli(razlike, true);

        if (stRazlik < 1) {
            return null;
        }

        // Za zdaj se zdi, da stRazlik je najboljše <= 4 in količina besed > (2 stRazlik)
        // Ne vem pa še, če je to to... morda lahko še kaj optimiziram
        if (stRazlik <= 4 && this.filtriraneBesede.length > (2 * stRazlik)) {
            return razlike;
        }

        return null;
    }

    public boolean[] vrniTabeloRazlik() {
        /*
        Vrne tabelo boolean[6], kjer true pomeni, da se po indeksu besede razlikujejo
        Raje tako pregledamo namesto rabiti odziva, ker je možno da s tem si prišparamo potezo
        */

        boolean[] tabelaIndeksov = new boolean[6];
        char[] prvaBeseda = this.filtriraneBesede[0];

        outer:
        for (short i=0; i<6; i++) {
            for (short j=1; j<this.filtriraneBesede.length; j++) {
                char[] beseda = this.filtriraneBesede[j];

                if (prvaBeseda[i] != beseda[i]) {
                    tabelaIndeksov[i] = true;
                    continue outer;
                }
            }
        }

        return tabelaIndeksov;
    }

    public static char vrniNajCrko(short[] stCrk) {
        /*
        Vrne črko, ki se največ pojavlja glede na "mapo" stCrk
        */

        char najCrka = 0;
        short najPojav = -1;

        for (char crka='a'; crka<='z'; crka++) {
            if (stCrk[crka] > najPojav) {
                najCrka = crka;
                najPojav = stCrk[crka];
            }
        }

        return najCrka;
    }

    public void dodajUgotovljenoBesedo() {
        /*
        Dodamo v seznam ugotovljenih besed prvakar ugotovljeno besedo
        Rabili bomo ta seznam za seed cracking
        */

        // this.ugotovljeneBesede.add(new String(this.prejsnjaIzbira));
        int dolzinaPrejsnje = this.ugotovljeneBesede.length;
        char[][] novSeznam = new char[dolzinaPrejsnje + 1][6];

        for (short i=0; i<dolzinaPrejsnje; i++) {
            novSeznam[i] = this.ugotovljeneBesede[i];
        }

        novSeznam[dolzinaPrejsnje] = this.prejsnjaIzbira;

        this.ugotovljeneBesede = novSeznam;
    }

    public void odstaniUgotovljenoBesedo() {
        /*
        Odstrani ugotovljeno besedo iz slovarja
        Na tak način, za vsako novo besedo lahko imamo besedo manj za iskanje
        */

        for (short i=0; i<this.zacetneBesede.length; i++) {
            if (Arrays.equals(this.zacetneBesede[i], this.prejsnjaIzbira)) {
                this.zacetneBesede[i] = null;
                break;
            }
        }

        this.zacetneBesede = odstraniVseNull(this.zacetneBesede);
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

    public static char[] pretvoriOdziv(List<Character> odzivSeznam) {
        /*
        Podobno kot slovar, tudi odziv pretvorimo v char arry za hitrejši dostop
        */

        char[] odziv = new char[6];

        for (short i=0; i<6; i++) {
            odziv[i] = odzivSeznam.get(i);
        }

        return odziv;
    }

    public static short kolikoVTabeli(char[][] tabela, char[] iskani) {
        /*
        Vrne število iskanega elementa v tabeli (besede v slovarju)
        */

        short stevilo = 0;

        for (char[] element: tabela) {
            if (Arrays.equals(element, iskani)) {
                stevilo++;
            }
        }

        return stevilo;
    }

    public static short kolikoVTabeli(boolean[] tabela, boolean iskani) {
        /*
        Vrne število iskanega elementa v tabeli (vrednosti true al false)
        */

        short stevilo = 0;

        for (boolean element: tabela) {
            if (element == iskani) {
                stevilo++;
            }
        }

        return stevilo;
    }

    public static char[][] odstraniVseNull(char[][] seznam) {
        /*
        Ker rabim tabele, namesto brisati elemente, jih nastavim na null
        S to metodo, zbrišem vse null besede, da ne zgubljam časa pri iskanju
        */

        short stNull = kolikoVTabeli(seznam, null);
        int novaDolzina = seznam.length - stNull;
        char[][] novaTabela = new char[novaDolzina][6];

        short counterNove = 0;
        for (char[] beseda : seznam) {
            if (beseda == null) {
                continue;
            }

            novaTabela[counterNove] = beseda;
            counterNove++;
        }

        return novaTabela;
    }

    public static char[][] filtrirajBesede(char[][] besede, char[] prejsnjaBeseda, char[] odziv) {
        for (int i=0; i<besede.length; i++) {
            char[] odziv2 = izracunajOdziv(besede[i], prejsnjaBeseda);
            if (Arrays.equals(odziv2, odziv)) {
                continue;
            }

            besede[i] = null;
        }

        return odstraniVseNull(besede);
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

    public static short index(char[] beseda, char crka) {
        for (short i=0; i<beseda.length; i++) {
            if (beseda[i] == crka) {
                return i;
            }
        }

        return -1;
    }

    public static int vrniTocke(char[][] besede, char[] beseda1) {
        int score = 0;
        for (char[] beseda2: besede) {
            char[] odziv = izracunajOdziv(beseda2, beseda1);

            for (char o: odziv) {
                switch (o) {
                    case 'o' -> score += 2;
                    case '+' -> score += 3;
                    default -> score += 0;
                }
            }
        }

        return score;
    }
}