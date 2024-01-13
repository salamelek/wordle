import java.util.*;


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

    @Override
    public void inicializiraj(Set<String> besede) {
        /**
         * Inicializiramo raznorazne vrednosti za pravilno delovanje stroja
         * */

        this.dolzinaBesed = 6;
        this.semUgotovilSeed = false;
        this.seedJeBilZgresen = false;
        this.stBesedZaSeed = 10;
        this.pravilneBesedeCounter = this.stBesedZaSeed;
        this.zacetneBesede = pretvoriBesede(besede);
        this.slovar = pretvoriBesede(besede);
        this.ugotovljeneBesede = new char[0][this.dolzinaBesed];
    }

    @Override
    public String poteza(List<Character> odzivSeznam) {
        /**
         * Pri vsaki potezi vrnemo besedo
         * */

        // SEED CRACKING
        if (this.ugotovljeneBesede.length == this.stBesedZaSeed && !this.semUgotovilSeed && !this.seedJeBilZgresen) {
            short seconds = 10; // bo pogledalo bodisi v - kot v +

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
                return new String(this.prejsnjaIzbira = vrniZnanoBesedo());
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

        filtrirajBesede(odziv);

        if (this.filtriraneBesede.length == 0) {
            System.out.println(odziv);
            System.out.println(this.prejsnjaIzbira);
            throw new RuntimeException("SEZNAM FILTRIRANIH BESED JE PRAZENNNN!!!!!!!!!!!!!!!!!");
        }

//        System.out.println(Arrays.deepToString(this.filtriraneBesede));

        boolean[] razlike = seSplacaIskatCrke();
        if (razlike != null) {
            return new String(this.prejsnjaIzbira = vrniBesedoZaIskanjeCrk(razlike));
        }

//        System.out.println("\niscem optimalno");
        return new String(this.prejsnjaIzbira = vrniOptimalnoBesedo());
    }


    public boolean[] seSplacaIskatCrke() {
        /**
         * Pregleda za koliko bi se znižala dolžina seznama
         * */

        boolean[] razlike = vrniTabeloRazlik();
        short stRazlik = kolikoVTabeli(razlike, true);

        // testiraj katera možnost je najboljsa

        // TODO za zdaj se zdi, da stRazlik je najboljše <= 4 in količina besed > (2 * stRazlik)
        // TODO ne vem pa še če je to to... morda lahko še kaj optimiziram
        if (stRazlik <= 4 && stRazlik >= 1 && this.filtriraneBesede.length > (2 * stRazlik)) {
            return razlike;
        }

        return null;
    }

    public void dodajUgotovljenoBesedo() {
        /**
         * Dodamo v seznam ugotovljenih besed prvakar ugotovljeno besedo
         * Rabili bomo ta seznam za seed cracking
         * */

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
        /**
         * Ko (in če) ugotovimo seme, kličemo to metodo za vračanje vseh pravilnih besed
         * */

        this.pravilneBesedeCounter++;
        return this.pravilneBesede[this.pravilneBesedeCounter - 1];
    }

    public void crackSeed(short seconds) {
        /**
         * Ko dosežemo želeno število ugotovljenih besed (this.stBesedZaSeed) začnemo s seed crackingom
         * Dokler imamo še kaj časa, preverimo vsa možna števila od 0 do kolikor pač nam uspe. (bodisi v plus kot v minus)
         * Za vsak kandidat semena preverimo, če bi bilo prvih n členov enakih s novo premešanim slovarjem
         * */

        System.out.printf("%n'Investiram' %d sekund za iskanje semena...%n", seconds);

        long start = System.currentTimeMillis();
        long end = start + seconds * 1000;

        short seedCounter = 0;

        outer:
        while (System.currentTimeMillis() < end) {
            sign:
            for (short sign=-1; sign<=1; sign+=2) {
                Random random = new Random(seedCounter * sign);
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
                        continue sign;
                    }
                }

                // preglej prvih 10 besed
                for (short j=0; j<this.stBesedZaSeed; j++) {
                    if (Arrays.equals(besede[j], this.ugotovljeneBesede[j])) {
                        continue;
                    }

                    continue sign;
                }

                this.semUgotovilSeed = true;
                this.pravilneBesede = besede;

                System.out.printf("YESSSSS SEM GA UGOTOVIL: %d%n%n", seedCounter * sign);
                break outer;
            }

            seedCounter++;
        }

        if (!semUgotovilSeed) {
            System.out.printf("Na žalost ga nisem dobil :(%nPoiskal sem le semena od -%d do %d%n%n", seedCounter, seedCounter);
            this.seedJeBilZgresen = true;
        }
    }

    public boolean[] vrniTabeloRazlik() {
        /**
         * Vrne tabelo boolean[this.dolzinaBesed], kjer true pomeni, da se po indeksu besede razlikujejo
         * Raje tako pregledamo namesto rabiti odziva, ker je možno da s tem si prišparamo potezo
         * */

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

    public char[] vrniBesedoZaIskanjeCrk(boolean[] razlike) {
        /**
         * Ustvarimo tabelo, ki vsebuje črke, ki nam bodo povedale katera je manjkakoča črka
         * To rabimo v primerih odziva "-+++++", saj ugotavljati vsako besedo posebej bi bilo zamudno
         * */

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

    public char vrniNajCrko(short[] stCrk) {
        /**
         * Vrne črko, ki se največ pojavlja glede na "mapo" stCrk
         * */

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
        /**
         * pretvorimo Set<String> v 2D char tabelo zaradi hitrosti
         * */

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
        /**
         * Odstrani ugotovljeno besedo iz slovarja
         * Na tak način, za vsako novo besedo lahko imamo besedo manj za iskanje
         * */

        for (short i=0; i<this.zacetneBesede.length; i++) {
            if (Arrays.equals(this.zacetneBesede[i], this.prejsnjaIzbira)) {
                this.zacetneBesede[i] = null;
                break;
            }
        }

        this.zacetneBesede = odstraniVseNull(this.zacetneBesede);
    }

    public char[] pretvoriOdziv(List<Character> odzivSeznam) {
        /**
         * Podobno kot slovar, tudi odziv pretvorimo v char arry za hitrejši dostop
         * */

        char[] odziv = new char[this.dolzinaBesed];

        for (short i=0; i<this.dolzinaBesed; i++) {
            odziv[i] = odzivSeznam.get(i);
        }

        return odziv;
    }

    public char[] vrniOptimalnoBesedo() {
        /**
         * Izračuna katera je najbolj popularna črka na določenem indeksu
         * Če je to možno, ne ponavlja črk
         *
         * Dejansko s to funkcijo poskusim dobiti čim več pravilnih črk v odzivu
         * */

        // indeks števila predstavlja ascii code char-a
        short[][] mapaStCrk = vrniMapoStCrk();
        char[] najCrke = new char[this.dolzinaBesed];

        short najCrkeCounter = 0;
        for (short i=0; i<this.dolzinaBesed; i++) {
            char najCrka = 0;
            short najPojav = -1;

            for (char crka='a'; crka<='z'; crka++) {
                if (kolikoVTabeli(najCrke, crka) > 0 && !moramRabimIstoCrko()) {
                    continue;
                }

                short[] stCrk = mapaStCrk[crka];

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

        for (short i=0; i<beseda.length; i++) {
            for (short j=0; j<beseda.length; j++) {
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

    public short[][] vrniMapoStCrk() {
        /**
         * Vrne "mapo" v obili short[][].
         * Ključ je char črka (ki jo lahko interpretiramo kot int)
         * Vrednost pa je short[], ki šteje kolikokrat se je črka pojavila na katerem indeksu
         * npr. če hočemo indekse črke r, poiščemo tabelo['r']
         * Vem, da se to dela z mapami, ampak vem tudi da so bolj počasne kot tabele
         * */

        char[] crke = "abcdefghijklmnoprstuvz".toCharArray();
        short[][] mapaStCrk = new short['z' + 1][this.dolzinaBesed];

        for (char crka: crke) {
            for (char[] beseda: this.filtriraneBesede) {
                short[]indeksiCrk = vrniVseIndekse(beseda, crka);

                if (indeksiCrk == null) {
                    continue;
                }

                for (short indeks: indeksiCrk) {
                    mapaStCrk[crka][indeks]++;
                }
            }
        }

        return mapaStCrk;
    }

    public short[] vrniVseIndekse(char[] beseda, char crka) {
        /**
         * Vrne tabelo short[] vseh indeksov iskane črke v besedi
         * npr. beseda: banana | iskana: a => [1, 3, 5]
         * */

        short stCrk = kolikoVTabeli(beseda, crka);

        if (stCrk == 0) {
            return null;
        }

        short[] vsiIndeksi = new short[stCrk];

        short indeksiCounter = 0;
        for (short i=0; i<beseda.length; i++) {
            char crkaVBesedi = beseda[i];

            if (crkaVBesedi == crka) {
                vsiIndeksi[indeksiCounter] = i;
                indeksiCounter++;
            }
        }

        return vsiIndeksi;
    }

    public short kolikoVTabeli(char[][] tabela, char[] iskani) {
        /**
         * Vrne število iskanega elementa v tabeli (besede v slovarju)
         * */

        short stevilo = 0;

        for (char[] element: tabela) {
            if (Arrays.equals(element, iskani)) {
                stevilo++;
            }
        }

        return stevilo;
    }

    public short kolikoVTabeli(char[] tabela, char iskani) {
        /**
         * Vrne število iskanega elementa v tabeli (črke v besedi)
         * */

        short stevilo = 0;

        for (char element: tabela) {
            if (element == iskani) {
                stevilo++;
            }
        }

        return stevilo;
    }

    public short kolikoVTabeli(boolean[] tabela, boolean iskani) {
        /**
         * Vrne število iskanega elementa v tabeli (vrednosti true al false)
         * */

        short stevilo = 0;

        for (boolean element: tabela) {
            if (element == iskani) {
                stevilo++;
            }
        }

        return stevilo;
    }

    public char[][] odstraniVseNull(char[][] seznam) {
        /**
         * Ker rabim tabele, namesto brisati elemente, jih nastavim na null
         * S to metodo, zbrišem vse null besede, da ne zgubljam časa pri iskanju
         * */

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

    public short dobiIndex(char[] beseda, char crka) {
        /**
         * Vrne prvi indeks iskane črke v besedi (ali pa -1 če črke v besedi ni)
         * */

        for (short i=0; i<beseda.length; i++) {
            if (beseda[i] == crka) {
                return i;
            }
        }

        return -1;
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

        for (short i=0; i<odziv.length; i++) {
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
        for (short i=0; i<this.filtriraneBesede.length; i++) {
            char[] beseda = this.filtriraneBesede[i];

            // pravilne črke
            for (short j = 0; j < pravilneCrke.length; j++) {
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
            for (short j = 0; j < ugodneCrke.length; j++) {
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