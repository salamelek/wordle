import java.util.*;

// TODO (bruteforce)
/*
- make lots of lists (or maps, idk) that say "these words contain a, these contain b, etc etc
- when checking how many words does it remove, cut it early if the average is not promising
- first narrow down the list of all possible combinations (idk how yet)
*/


public class Tekm_63230387 implements Stroj {
    private short dolzinaBesed;
    private char[][] zacetneBesede;
    private char[][] filtriraneBesede;
    private char[] prejsnjaIzbira;
    private char[][] slovar;
    private char[][] ugotovljeneBesede;
    private boolean semUgotovilSeed;
    private char[][] pravilneBesede;
    private short pravilneBesedeCounter;
    private short stBesedZaSeed;
    private boolean seedJeBilZgresen;
    private int crackingSeconds;
    private long zacetek;

    @Override
    public void inicializiraj(Set<String> besede) {
        /*
        Inicializiramo raznorazne vrednosti za pravilno delovanje stroja
        */

        this.dolzinaBesed = 6;
        this.stBesedZaSeed = 30;
        this.crackingSeconds = 10;

        this.zacetek = System.currentTimeMillis();

        this.semUgotovilSeed = false;
        this.seedJeBilZgresen = false;
        this.pravilneBesedeCounter = this.stBesedZaSeed;
        this.zacetneBesede = pretvoriBesede(besede);
        this.slovar = pretvoriBesede(besede);
        this.ugotovljeneBesede = new char[0][this.dolzinaBesed];
    }

    @Override
    public String poteza(List<Character> odzivSeznam) {
        /*
        Pri vsaki potezi vrnemo besedo
        */

        // SEED CRACKING
        if (this.ugotovljeneBesede.length == this.stBesedZaSeed && !this.semUgotovilSeed && !this.seedJeBilZgresen) {
            long konec = System.currentTimeMillis();
            crackSeed(konec - this.zacetek);
        }

        // Prva poteza
        if (odzivSeznam == null) {
            /*
            Tukaj re-inicializiramo vse spremenljivke (ker dobimo novo besedo) in
            vrnemo našo prvo besedo
            */

            this.filtriraneBesede = Arrays.copyOf(this.zacetneBesede, this.zacetneBesede.length);

            if (this.semUgotovilSeed) {
                return new String(this.prejsnjaIzbira = vrniZnanoBesedo());
            }

            // če dam več kot 750 postane dokaj pocasno
            if (this.zacetneBesede.length < 750) {
                return new String(this.prejsnjaIzbira = vrniBesedo_tockujOdziv());
            } else {
                char[] beseda = "kornea".toCharArray();
                return new String(this.prejsnjaIzbira = beseda);
            }
        }

        // pretvorimo tudi odziv v char[]
        char[] odziv = pretvoriOdziv(odzivSeznam);

        // Smo ugotovili
        if (kolikoVTabeli(odziv, '+') == this.dolzinaBesed) {
            /*
            Ugotovili smo besedo!
            */
            dodajUgotovljenoBesedo();

            // odstranimo že ugotovljeno besedo iz slovarja
            odstaniUgotovljenoBesedo();

            return null;
        }

        // preveri ali smo ugotovili napačen seed
        if (this.semUgotovilSeed && kolikoVTabeli(odziv, '+') != this.dolzinaBesed) {
            System.out.println("AAAAAAAAAAAAH SEED JE BIL NAPAČEN KSDJFHNKSDJFNSKDHJFNBKJAHSDAISDJF");
            this.semUgotovilSeed = false;
            this.seedJeBilZgresen = true;
        }

        // =============================
        // VV - Normalni potek igre - VV
        if (this.semUgotovilSeed) {
            return new String (vrniZnanoBesedo());
        }

        // odstranimo besede, ki niso več relevantne
        this.filtriraneBesede = filtrirajBesede(this.filtriraneBesede,this.prejsnjaIzbira , odziv);

        if (this.filtriraneBesede.length == 0) {
            System.out.println(odziv);
            System.out.println(this.prejsnjaIzbira);
            throw new RuntimeException("SEZNAM FILTRIRANIH BESED JE PRAZENNNN!!!!!!!!!!!!!!!!!");
        }

//        System.out.println(Arrays.deepToString(this.filtriraneBesede));

        boolean[] razlike = seSplacaIskatCrke();
        if (razlike != null) {
            return new String(this.prejsnjaIzbira = vrniBesedo_iskaneCrke(razlike));
        }

        return new String(this.prejsnjaIzbira = vrniBesedo_tockujOdziv());
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

        // testiraj katera možnost je najboljsa

        // TODO za zdaj se zdi, da stRazlik je najboljše <= 4 in količina besed > (2 stRazlik)
        // TODO ne vem pa še če je to to... morda lahko še kaj optimiziram
        if (stRazlik <= 4 && this.filtriraneBesede.length > (2 * stRazlik)) {
            return razlike;
        }

        return null;
    }

    public void dodajUgotovljenoBesedo() {
        /*
        Dodamo v seznam ugotovljenih besed prvakar ugotovljeno besedo
        Rabili bomo ta seznam za seed cracking
        */

        // this.ugotovljeneBesede.add(new String(this.prejsnjaIzbira));
        int dolzinaPrejsnje = this.ugotovljeneBesede.length;
        char[][] novSeznam = new char[dolzinaPrejsnje + 1][this.dolzinaBesed];

        for (short i=0; i<dolzinaPrejsnje; i++) {
            novSeznam[i] = this.ugotovljeneBesede[i];
        }

        novSeznam[dolzinaPrejsnje] = this.prejsnjaIzbira;

        this.ugotovljeneBesede = novSeznam;
    }

    public char[] vrniZnanoBesedo() {
        /*
        Ko (in če) ugotovimo seme, kličemo to metodo za vračanje vseh pravilnih besed
        */

        this.pravilneBesedeCounter++;
        return this.pravilneBesede[this.pravilneBesedeCounter - 1];
    }

    public void crackSeed(long razlikaCasa) {
        /*
        Ko dosežemo želeno število ugotovljenih besed (this.stBesedZaSeed) začnemo s seed crackingom
        Dokler imamo še kaj časa, preverimo vsa možna števila od 0 do kolikor pač nam uspe.
        Za vsak kandidat semena preverimo, če bi bilo prvih n členov enakih s novo premešanim slovarjem
        */

        System.out.println(razlikaCasa + "ms");
        long casNaBesedo = razlikaCasa / this.stBesedZaSeed;
        System.out.println("na besedo: " + casNaBesedo + "ms");
        // to bo reklo precej več časa, ker potem se pospeši
        System.out.println("estimated za vse besede: " + (casNaBesedo * (this.slovar.length - this.stBesedZaSeed) / 1000.) + "s");

        // TODO settaj čas bruteforce glede na prvih n besed
        System.out.printf("%n'Investiram' %d sekund za iskanje semena...%n", this.crackingSeconds);

        long start = System.currentTimeMillis();
        long end = start + this.crackingSeconds * 1000L;

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

            break outer;
        }

        if (!semUgotovilSeed) {
            System.out.printf("Na žalost ga nisem dobil :(%nPoiskal sem le semena do %d%n%n", seedCounter);
            this.seedJeBilZgresen = true;
        }
    }

    public boolean[] vrniTabeloRazlik() {
        /*
        Vrne tabelo boolean[this.dolzinaBesed], kjer true pomeni, da se po indeksu besede razlikujejo
        Raje tako pregledamo namesto rabiti odziva, ker je možno da s tem si prišparamo potezo
        */

        boolean[] tabelaIndeksov = new boolean[6];
        char[] prvaBeseda = this.filtriraneBesede[0];

        outer:
        for (short i=0; i<this.dolzinaBesed; i++) {
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

    public char vrniNajCrko(short[] stCrk) {
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

    public char[][] pretvoriBesede(Set<String> besede) {
        /*
        pretvorimo Set<String> v 2D char tabelo zaradi hitrosti
        */

        int stBesed = besede.size();
        char[][] tabelaBesed = new char[stBesed][this.dolzinaBesed];

        short indeks = 0;
        for (String beseda: besede) {
            tabelaBesed[indeks] = beseda.toCharArray();
            indeks++;
        }

        return tabelaBesed;
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

    public char[] pretvoriOdziv(List<Character> odzivSeznam) {
        /*
        Podobno kot slovar, tudi odziv pretvorimo v char arry za hitrejši dostop
        */

        char[] odziv = new char[this.dolzinaBesed];

        for (short i=0; i<this.dolzinaBesed; i++) {
            odziv[i] = odzivSeznam.get(i);
        }

        return odziv;
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

    public short kolikoVTabeli(char[][] tabela, char[] iskani) {
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

    public short kolikoVTabeli(char[] tabela, char iskani) {
        /*
        Vrne število iskanega elementa v tabeli (črke v besedi)
        */

        short stevilo = 0;

        for (char element: tabela) {
            if (element == iskani) {
                stevilo++;
            }
        }

        return stevilo;
    }

    public short kolikoVTabeli(boolean[] tabela, boolean iskani) {
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

    public char[][] odstraniVseNull(char[][] seznam) {
        /*
        Ker rabim tabele, namesto brisati elemente, jih nastavim na null
        S to metodo, zbrišem vse null besede, da ne zgubljam časa pri iskanju
        */

        short stNull = kolikoVTabeli(seznam, null);
        int novaDolzina = seznam.length - stNull;
        char[][] novaTabela = new char[novaDolzina][this.dolzinaBesed];

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

    public char[][] filtrirajBesede(char[][] besede, char[] prejsnjaBeseda, char[] odziv) {
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

            // 12: 3.3684
            // 23: 3.3657

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