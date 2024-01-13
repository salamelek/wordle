import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class OdstraniCrke implements Stroj {
    private short dolzinaBesed;
    private char[][] zacetneBesede;
    private char[][] slovar;
    private char[][] filtriraneBesede;
    private char[] prejsnjaIzbira;
    private char[] uporabljeneCrke;

    @Override
    public void inicializiraj(Set<String> besede) {
        this.dolzinaBesed = 6;
        this.zacetneBesede = pretvoriBesede(besede);
        this.slovar = pretvoriBesede(besede);
        this.uporabljeneCrke = new char[0];
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

            char[] beseda = vrniBesedo();
            System.out.println(Arrays.toString(beseda));
            return new String(this.prejsnjaIzbira = beseda);
        }

        // pretvorimo tudi odziv v char[]
        char[] odziv = pretvoriOdziv(odzivSeznam);

        // Smo ugotovili
        if (kolikoVTabeli(odziv, '+') == this.dolzinaBesed) {
            /*
            Ugotovili smo besedo!
            */

            // odstranimo že ugotovljeno besedo iz slovarja
            odstaniUgotovljenoBesedo();

            return null;
        }

        // odstranimo besede, ki niso več relevantne
        filtrirajBesede(odziv);

//        System.out.println(Arrays.deepToString(this.filtriraneBesede));

        if (this.filtriraneBesede.length > 1) {
            char[] beseda = vrniBesedo();
            System.out.println(Arrays.toString(beseda));
            return new String(this.prejsnjaIzbira = beseda);
        }

        char[] beseda = this.filtriraneBesede[0];
        System.out.println(Arrays.toString(beseda));
        return new String(this.prejsnjaIzbira = beseda);
    }

    public char[] vrniBesedo() {
        short[] pogostostCrk = vrniPogostostCrk(this.filtriraneBesede);
        char[] beseda = new char[6];

        for (int i=0; i<this.dolzinaBesed; i++) {
            char novaCrka = vrniNajCrkoKiNiBlaZe(pogostostCrk);
            this.uporabljeneCrke = dodajVTabelo(this.uporabljeneCrke, novaCrka);

            beseda[i] = novaCrka;
        }
//        System.out.println(Arrays.toString(this.uporabljeneCrke));

        return beseda;
    }

    public char vrniNajCrkoKiNiBlaZe(short[] pogostost) {
        short najPojav = 0;
        char najCrka = 'a';

        for (char crka='a'; crka<='z'; crka++) {
            if (kolikoVTabeli(this.uporabljeneCrke, crka) > 0) {
                continue;
            }

            if (pogostost[crka] > najPojav) {
                najPojav = pogostost[crka];
                najCrka = crka;
            }
        }

        return najCrka;
    }

    public short[] vrniPogostostCrk(char[][] seznamBesed) {
        short[] pogostostCrk = new short['z' + 1];

        for (char[] beseda: seznamBesed) {
            for (char crka: beseda) {
                pogostostCrk[crka]++;
            }
        }

        return pogostostCrk;
    }

    public char[][] pretvoriBesede(Set<String> besede) {
        int stBesed = besede.size();
        char[][] tabelaBesed = new char[stBesed][this.dolzinaBesed];

        short indeks = 0;
        for (String beseda: besede) {
            tabelaBesed[indeks] = beseda.toCharArray();
            indeks++;
        }

        return tabelaBesed;
    }

    public char[] pretvoriOdziv(List<Character> odzivSeznam) {
        char[] odziv = new char[this.dolzinaBesed];

        for (short i=0; i<this.dolzinaBesed; i++) {
            odziv[i] = odzivSeznam.get(i);
        }

        return odziv;
    }

    public short kolikoVTabeli(char[][] tabela, char[] iskani) {
        short stevilo = 0;

        for (char[] element: tabela) {
            if (Arrays.equals(element, iskani)) {
                stevilo++;
            }
        }

        return stevilo;
    }

    public short kolikoVTabeli(char[] tabela, char iskani) {
        short stevilo = 0;

        for (char element: tabela) {
            if (element == iskani) {
                stevilo++;
            }
        }

        return stevilo;
    }

    public short kolikoVTabeli(boolean[] tabela, boolean iskani) {
        short stevilo = 0;

        for (boolean element: tabela) {
            if (element == iskani) {
                stevilo++;
            }
        }

        return stevilo;
    }

    public void odstaniUgotovljenoBesedo() {
        for (short i=0; i<this.zacetneBesede.length; i++) {
            if (Arrays.equals(this.zacetneBesede[i], this.prejsnjaIzbira)) {
                this.zacetneBesede[i] = null;
                break;
            }
        }

        this.zacetneBesede = odstraniVseNull(this.zacetneBesede);
    }

    public char[][] odstraniVseNull(char[][] seznam) {
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

    public void filtrirajBesede(char[] odziv) {
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

    public char[] dodajVTabelo(char[] tabela, char crka) {
        char[] tabela2 = new char[tabela.length + 1];

        for (int i=0; i<tabela.length; i++) {
            tabela2[i] = tabela[i];
        }

        tabela2[tabela.length] = crka;

        return tabela2;
    }
}
