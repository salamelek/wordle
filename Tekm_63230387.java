import java.util.*;


public class Tekm_63230387 implements Stroj {
    private int dolzinaBesed;
    private char[][] zacetneBesede;
    private char[][] filtriraneBesede;
    private char[] prejsnjaIzbira;
    private Set<String> slovar;
    private List<String> ugotovljeneBesede;
    private long ugotovljenSeed;
    private boolean semUgotovilSeed;
    private List<String> pravilneBesede;
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
        this.slovar = besede;
        this.zacetneBesede = pretvoriBesede(besede);
        this.ugotovljeneBesede = new ArrayList<>();
    }

    @Override
    public String poteza(List<Character> odzivSeznam) {
        // SEED CRACKING
        if (this.ugotovljeneBesede.size() == this.stBesedZaSeed && !this.semUgotovilSeed && !this.seedJeBilZgresen) {
            int numSeeds = 50000; // bo pogledalo bodisi v - kot v +

            crackSeed(numSeeds);
        }


        // Prva poteza
        if (odzivSeznam == null) {
            /*
            Tukaj re-inicializiramo vse spremenljivke (ker dobimo novo besedo) in
            vrnemo našo prvo besedo
            */

            this.filtriraneBesede = Arrays.copyOf(this.zacetneBesede, this.zacetneBesede.length);

            if (this.semUgotovilSeed) {
                return vrniZnanoBesedo();
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
            this.ugotovljeneBesede.add(new String(this.prejsnjaIzbira));

            // odstranimo že ugotovljeno besedo
            odstaniBesedo();
            this.zacetneBesede = odstraniVseNull(this.zacetneBesede);

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
            return vrniZnanoBesedo();
        }

        // odstranimo besede, ki niso več relevantne
        filtrirajBesede(odziv);

//        System.out.println(Arrays.deepToString(this.filtriraneBesede));

        // pregledamo črke, če manjka samo ena
        int indeksManjkajoce = preglejCeSeBesedeRazlikujejoLeZa1();
        if (indeksManjkajoce != -1 && this.filtriraneBesede.length > 2) {
            // nastavimo stevilo potrebnih pregledov
//            System.out.println("\niscem crke");
            return new String(this.prejsnjaIzbira = vrniBesedoZaIskanjeCrk(indeksManjkajoce));
        }

//        System.out.println("\niscem optimalno");
        return new String(this.prejsnjaIzbira = vrniOptimalnoBesedo());
    }


    public String vrniZnanoBesedo() {
        this.pravilneBesedeCounter++;
        return this.pravilneBesede.get(this.pravilneBesedeCounter - 1);
    }

    public void crackSeed(int numSeeds) {
        System.out.println("\niščem seed...");

        outer:
        for (int i=0; i<numSeeds; i++) {
            sign:
            for (int sign=-1; sign<=1; sign+=2) {
                int seedToCheck = i * sign;

                List<String> lstBesede = new ArrayList<>(this.slovar);
                Collections.shuffle(lstBesede, new Random(seedToCheck));

                // preglej prvih 10 besed
                for (int j=0; j<this.stBesedZaSeed; j++) {
                    if (lstBesede.get(j).equals(this.ugotovljeneBesede.get(j))) {
                        continue;
                    }

                    continue sign;
                }

                this.ugotovljenSeed = seedToCheck;
                this.semUgotovilSeed = true;

                List<String> pravilneBesede = new ArrayList<>(this.slovar);
                Collections.shuffle(pravilneBesede, new Random(this.ugotovljenSeed));
                this.pravilneBesede = pravilneBesede;

                System.out.printf("YESSSSS SEM GA UGOTOVIL: %d%n%n", this.ugotovljenSeed);
                break outer;
            }
        }

        if (!semUgotovilSeed) {
            System.out.println("Na žalost ga nisem dobil :(");
            this.seedJeBilZgresen = true;
        }
    }

    public int preglejCeSeBesedeRazlikujejoLeZa1() {
        /*
        mislim, da je ime funkcije samo posebej umevno

        najprej pregledamo vse črke na indeksu 0, nato 1, itd.
        */

        char[] prvaBeseda = this.filtriraneBesede[0];
        boolean seJeZeRazlikovala = false;
        int indeks = -1;

        for (int i=0; i<this.dolzinaBesed; i++) {
            for (int j=1; j<this.filtriraneBesede.length; j++) {
                char[] beseda = this.filtriraneBesede[j];

                if (beseda[i] != prvaBeseda[i] && !seJeZeRazlikovala) {
                    seJeZeRazlikovala = true;
                    indeks = i;
                    break;
                }

                if (beseda[i] != prvaBeseda[i] && seJeZeRazlikovala) {
                   return -1;
                }
            }
        }

        return indeks;
    }

    public char[] vrniBesedoZaIskanjeCrk(int indeks) {
        char[] crkeZaIskanje = "aaaaaa".toCharArray();

        int count = 0;
        for (char[] beseda: this.filtriraneBesede) {
            char crka = beseda[indeks];

            if (kolikoVTabeli(crkeZaIskanje, crka) == 0) {
                crkeZaIskanje[count] = crka;
                count++;
            }

            if (count >= this.dolzinaBesed) {
                break;
            }
        }

        return crkeZaIskanje;
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

    public void odstaniBesedo() {
        for (int i=0; i<this.zacetneBesede.length; i++) {
            if (Arrays.equals(this.zacetneBesede[i], this.prejsnjaIzbira)) {
                this.zacetneBesede[i] = null;
                break;
            }
        }
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

    public char[][] odstraniVseNull(char[][] seznam) {
        int stNull = kolikoVTabeli(seznam, null);
        int novaDolzina = seznam.length - stNull;
        char[][] novaTabela = new char[novaDolzina][this.dolzinaBesed];

//        if (novaDolzina == 0) {
//            throw new RuntimeException("ODSTRANILI SMO VSE IZ TABELE AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
//        }

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
