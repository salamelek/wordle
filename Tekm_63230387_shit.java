import java.util.*;

// lukati, mujast, nunati, jajati, hrkati, mrkati, manski, kacati

public class Tekm_63230387_shit implements Stroj {
    private Set<String> izhodiscneBesede;
    private List<String> filtriraneBesede;
    private String prejsnjaIzbira;
    private List<Character> pravilnaBesedaOdziv;
    private List<Character> vseNapacnoOdziv;
    private boolean semZePreverilEnoCrko;
    private int indeksNapacne;

    @Override
    public void inicializiraj(Set<String> besede) {
        this.izhodiscneBesede = besede;
        this.filtriraneBesede = new ArrayList<>(besede);
        this.pravilnaBesedaOdziv = new ArrayList<>(List.of('+', '+', '+', '+', '+', '+'));
        this.vseNapacnoOdziv = new ArrayList<>(List.of('-', '-', '-', '-', '-', '-'));
        this.semZePreverilEnoCrko = false;
    }

    @Override
    public String poteza(List<Character> odzivPrejsnjePoteze) {
        // majhna pomoč pri debuggingu
        if (this.filtriraneBesede.isEmpty()) {
            throw new RuntimeException("Nekaj močno smrdi!");
        }

        // prva poteza
        if (odzivPrejsnjePoteze == null) {
            // re-inicializiramo spremenljivke za vsako novo besedo
            this.filtriraneBesede = new ArrayList<>(this.izhodiscneBesede);
            this.semZePreverilEnoCrko = false;

            return this.prejsnjaIzbira = vrniOptimalnoBesedo();
        }

        // preverimo, če smo ugotovili
        if (odzivPrejsnjePoteze.equals(this.pravilnaBesedaOdziv)) {
            return null;
        }

        // TODO optimiziraj (je pocasno)
        odstraniNeprimerneBesede(odzivPrejsnjePoteze);

//        System.out.println(this.filtriraneBesede);
//        System.out.println(this.filtriraneBesede.size());

        // ce nam manjka ena crka in seznam besed je daljsi od 2, potem ugibamo use mozne crke
        int indeksNapacne = manjkaEna(odzivPrejsnjePoteze);
        if (indeksNapacne > -1 && this.filtriraneBesede.size() > 2) {
            this.semZePreverilEnoCrko = true;
            this.indeksNapacne = indeksNapacne;
            return this.prejsnjaIzbira = vrniBesedoZaIskanje(indeksNapacne);
        }

        // nadaljujemo z ugibanjem napacnih crk
        if (this.vseNapacnoOdziv.equals(odzivPrejsnjePoteze) && this.semZePreverilEnoCrko && this.filtriraneBesede.size() > 2) {
            return this.prejsnjaIzbira = vrniBesedoZaIskanje(this.indeksNapacne);
        }

        if (this.filtriraneBesede.size() > 2) {
            return this.prejsnjaIzbira = vrniOptimalnoBesedo();
        } else {
            return this.prejsnjaIzbira = this.filtriraneBesede.getFirst();
        }
    }

    public String myToString(List<Character> seznam) {
        StringBuilder sb = new StringBuilder();

        for (Character crka: seznam) {
            sb.append(crka);
        }

        return sb.toString();
    }

    public int manjkaEna(List<Character> odziv) {
        if (odziv.contains('o')) {
            return -1;
        }

        int napaCounter = 0;
        for (char o: odziv) {
            if (o == '-') {
                napaCounter++;
            }

            if (napaCounter > 1) {
                return -1;
            }
        }

        return odziv.indexOf('-');
    }

    public String vrniBesedoZaIskanje(int indeksNapacne) {
        /*
        naj bi morale bit vse črke pravilne razen tiste na indeksu
         */
        List<Character> mozneCrke = new ArrayList<>();

        int count = 0;
        for (String beseda: this.filtriraneBesede) {
            char crka = beseda.toCharArray()[indeksNapacne];

            if (!mozneCrke.contains(crka)) {
                mozneCrke.add(crka);
            }

            count++;

            if (count >= 6) {
                break;
            }
        }

        int manjka = 6 - mozneCrke.size();
        for (int i=0; i<manjka; i++) {
            mozneCrke.add('a');
        }

        return myToString(mozneCrke);
    }

    public String vrniOptimalnoBesedo() {
        Map<Character, Integer[]> mapaStCrk = vrniMapStCrk();
        List<Character> najCrke = new ArrayList<>();

        for (int i=0; i<6; i++) {
            Character najCrka = null;
            int najPojav = -1;

            for (Character crka: mapaStCrk.keySet()) {

                // TODO tukaj je se prostor za optimizacijo
                if (najCrke.contains(crka) && this.filtriraneBesede.size() > 4) {
//                    System.out.println("skipped");
                    continue;
                }

                Integer[] stCrk = mapaStCrk.get(crka);

                if (stCrk[i] > najPojav) {
                    najPojav = stCrk[i];
                    najCrka = crka;
                }
            }

            najCrke.add(najCrka);
        }

        return myToString(najCrke);
    }

    public List<Integer> vrniVseIndekse(String beseda, char crka) {
        List<Integer> indeksi = new ArrayList<>();

        for (int i = 0; i < beseda.length(); i++) {
            if (beseda.charAt(i) == crka) {
                indeksi.add(i);
            }
        }

        return indeksi;
    }

    public Map<Character, Integer[]> vrniMapStCrk() {
        char[] crke = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'r', 's', 't', 'u', 'v', 'z'};
        List<String> besede = this.filtriraneBesede;
        Map<Character, Integer[]> mapaStCrk = new HashMap<>();

        for (char crka: crke) {
            for (String beseda: besede) {
                // to pogleda vse crke v besedi (pocasno ampak optimalno)

                List<Integer> indeksiCrk = vrniVseIndekse(beseda, crka);

                if (indeksiCrk == null || indeksiCrk.isEmpty()) {
                    continue;
                }

                // IDE predlaga to fino zadevo
                mapaStCrk.computeIfAbsent(crka, k -> new Integer[]{0, 0, 0, 0, 0, 0});

                Integer[] stevilaCrk = mapaStCrk.get(crka);

                for (Integer indeks: indeksiCrk) {
                    stevilaCrk[indeks]++;
                }


                // vzame samo prvi indeks iskane crke (bolj optimalno, ampak ni tocno)
                /*
                int indeksCrke = beseda.indexOf(crka);

                if (indeksCrke == -1) {
                    continue;
                }

                mapaStCrk.computeIfAbsent(crka, k -> new Integer[]{0, 0, 0, 0, 0, 0});
                Integer[] stevilaCrk = mapaStCrk.get(crka);
                stevilaCrk[indeksCrke]++;
                */
            }
        }

        return mapaStCrk;
    }

    public void odstraniNeprimerneBesede(List<Character> odziv) {
        // Iz množice odstranimo vse besede, ki niso združljive z odzivom
        // na zadnjo izbiro.
        Set<String> odstrani = new TreeSet<>();
        for (String beseda: this.filtriraneBesede) {
            if (!jeZdruzljiva(beseda, this.prejsnjaIzbira, odziv)) {
                odstrani.add(beseda);
            }
        }
        this.filtriraneBesede.removeAll(odstrani);
    }

    private static boolean jeZdruzljiva(String beseda, String izbira, List<Character> odziv) {
        int n = odziv.size();

        // Izdelamo seznam znakov besed <beseda> in <izbira>
        // (nizi so nespremenljivi, seznami pa niso).
        // (npr. "znanka" -> ['z', 'n', 'a', 'n', 'k', 'a'].
        List<Character> crkeBesede = new ArrayList<>();
        List<Character> crkeIzbire = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            crkeBesede.add(beseda.charAt(i));
            crkeIzbire.add(izbira.charAt(i));
        }

        // Preverimo, ali za vse i-je, pri katerih je odziv[i] == '+',
        // velja beseda[i] == izbira[i].
        for (int i = 0; i < n; i++) {
            if (odziv.get(i) == '+') {
                if (crkeBesede.get(i) != crkeIzbire.get(i)) {
                    return false;
                }

                // Označimo, da smo ta položaj že pregledali
                // (to je pomembno za pravilno obravnavo odzivov 'o').
                crkeBesede.set(i, '#');
                crkeIzbire.set(i, '_');
            }
        }

        // Preverimo, ali za vse i-je, pri katerih je odziv[i] == 'o',
        // velja, da <beseda> vsebuje črko izbira[i], a ne na indeksu <i>.
        // Vsako tako črko beseda <izbira> je treba povezati z eno samo črko
        // besede <beseda>.

        for (int ixIzbira = 0; ixIzbira < n; ixIzbira++) {

            if (odziv.get(ixIzbira) == 'o') {
                char crka = crkeIzbire.get(ixIzbira);
                int ixBeseda = crkeBesede.indexOf(crka);
                if (ixBeseda < 0 || crkeBesede.get(ixIzbira) == crka) {
                    return false;
                }

                // Označimo, da smo pripadajoča položaja že pregledali.
                crkeBesede.set(ixBeseda, '#');
                crkeIzbire.set(ixIzbira, '_');
            }
        }

        // Preverimo, ali za vse i-je, pri katerih je odziv[i] == '-',
        // velja, da <beseda> ne vsebuje črke izbira[i].
        for (int i = 0; i < n; i++) {
            if (odziv.get(i) == '-' && crkeBesede.contains(crkeIzbire.get(i))) {
                return false;
            }
        }

        return true;
    }
}
