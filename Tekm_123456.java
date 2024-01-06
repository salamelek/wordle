import java.util.*;

//
// Primer stroja za igro Wordle. Stroj vzdržuje množico slovarskih besed, ki
// so združljive z vsemi dosedanjimi omejitvami, in vsakokrat izbere ``prvo''
// besedo iz množice (tj. tisto, ki jo vrne iterator ob prvem klicu metode
// next).
//

/*
SEED: 1

Normalno:
Vsota: 80856
Povprečje: 5.0649

Če dam kot prvi ugib "parien", potem:
Vsota: 67800
Povprečje: 4.2471

Če dam kot prvi ugib "pariea", potem:
Vsota: 70192
Povprečje: 4.3969

Vsota: 66207
Povprečje: 4.1473
*/

public class Tekm_123456 implements Stroj {

    // izhodiščna množica besed
    // (nastavi se ob inicializaciji, potem pa se ne spreminja)
    private Set<String> izhodiscneBesede;

    // množica besed, ki še ustreza omejitvam
    private Set<String> besede;

    // nazadnje izbrana beseda
    private String zadnjaIzbira;

    //
    // Nastavi izhodiščno množico besed.
    //
    @Override
    public void inicializiraj(Set<String> besede) {
        this.izhodiscneBesede = new TreeSet<>(besede);
    }

    //
    // Na podlagi podanega odziva na prejšnjo izbiro vrne naslednjo izbiro.
    //
    // odziv[i]: odziv ('+', 'o' ali '-') na i-to črko prejšnje izbire;
    //           null, ko ogrodje zahteva prvo izbiro.
    //
    @Override
    public String poteza(List<Character> odziv) {

        if (odziv == null) {
            // Prva ``poteza'': ponastavi množico besed.
            this.besede = new TreeSet<>(this.izhodiscneBesede);
//            return this.zadnjaIzbira = "parien";
            return this.zadnjaIzbira = "parien";

        } else {
            if (vseEnako(odziv, '+')) {
                // Konec, naša zadnja izbira je bila pravilna!
                return null;
            }

            if (this.besede.isEmpty()) {
                // Množica besed je prazna, kljub temu da besede še nismo
                // uganili.
                throw new RuntimeException("Nekaj močno smrdi!");
            }

            // Iz množice odstranimo vse besede, ki niso združljive z odzivom
            // na zadnjo izbiro.
            Set<String> odstrani = new TreeSet<>();
            for (String beseda: this.besede) {
                if (!jeZdruzljiva(beseda, this.zadnjaIzbira, odziv)) {
                    odstrani.add(beseda);
                }
            }
            this.besede.removeAll(odstrani);
        }

        // Vrnemo ``prvo'' besedo iz množice (tj. prvo, ki jo vrne iterator).
        return this.zadnjaIzbira = this.besede.iterator().next();
    }

    //
    // Vrne true natanko v primeru, če so vsi elementi podanega seznama enaki
    // podanemu elementu.
    //
    private static <T> boolean vseEnako(List<T> seznam, T element) {
        return seznam.stream().allMatch(e -> e.equals(element));
    }

    //
    // Vrne true natanko v primeru, če je beseda <beseda> združljiva s
    // podanim odzivom na podano izbiro.
    //
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
            if (odziv.get(i) == '-' && crkeBesede.indexOf(crkeIzbire.get(i)) >= 0) {
                return false;
            }
        }

        return true;
    }
}
