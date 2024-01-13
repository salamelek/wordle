import java.util.*;

public class DiscordShit implements Stroj {

    private Set<String> besede;
    private Set<String> besede2;
    private Map<Character, Integer>[] mapeScore;
    private Map<String, Integer> mapaScore;
    private String beseda;

    @SuppressWarnings("unchecked")
    public DiscordShit() {
        this.besede = new HashSet<>();
        this.besede2 = new HashSet<>();
        this.mapeScore = new HashMap[6];
        this.mapaScore = new HashMap<>();

        for (int i = 0; i < 6; i++) {
            this.mapeScore[i] = new HashMap<>();
        }

        burekInitialize();
        burekCalculate();
        this.beseda = burekChooseInitial();
    }

    private void burekInitialize() {
        for (String word : besede) {
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                mapeScore[i].put(c, mapeScore[i].getOrDefault(c, 0) + 1);
            }
        }
    }

    private void burekCalculate() {
        for (String word : besede) {
            int score = 0;
            Set<Character> uniqueChars = new HashSet<>();
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                if (uniqueChars.add(c)) {
                    score += mapeScore[i].getOrDefault(c, 0);
                }
            }
            mapaScore.put(word, score);
        }
    }

    private String burekChooseInitial() {
        String bestWord = "";
        int bestScore = Integer.MIN_VALUE;
        for (String word : besede) {
            int score = mapaScore.get(word);
            if (score > bestScore) {
                bestScore = score;
                bestWord = word;
            }
        }
        return bestWord;
    }

    @Override
    public void inicializiraj(Set<String> besede) {
        this.besede.clear();
        this.besede.addAll(besede);
        besede2.clear();
        besede2.addAll(besede);
        burekInitialize();
        burekCalculate();
        beseda = burekChooseInitial();
    }

    @Override
    public String poteza(List<Character> odziv) {
        if (odziv == null) {
            this.besede2 = new HashSet<>(besede);
            this.beseda = burekChooseInitial();
            return beseda;
        } else {
            String feedback = burekConvert(odziv);

            if (burekCorrect(feedback)) {
                besede.remove(beseda);
                besede2.remove(beseda);
                return null;
            }

            burekProcess(beseda, feedback);
            beseda = burekNext();
            return beseda;
        }
    }

    private boolean burekCorrect(String feedback) {
        return feedback.chars().allMatch(ch -> ch == '+');
    }

    private String burekNext() {
        if (besede2.isEmpty()) {
            throw new IllegalStateException("BurekException");
        }
        String bestGuess = "";
        int bestScore = Integer.MIN_VALUE;

        for (String word : besede2) {
            int score = mapaScore.getOrDefault(word, 0);
            if (score > bestScore) {
                bestScore = score;
                bestGuess = word;
            }
        }

        besede2.remove(bestGuess);
        return bestGuess;
    }

    private void burekProcess(String guess, String feedback) {
        if (burekCorrect(feedback)) {
            besede.remove(guess);
        } else {
            besede2.removeIf(word -> !burekCompatible(word, guess, feedback));
        }
    }
    private boolean burekCompatible(String word, String guess, String feedback) {
        boolean[] charInWordUsed = new boolean[word.length()];
        int[] charCountInWord = new int[26];

        for (int i = 0; i < word.length(); i++) {
            charCountInWord[word.charAt(i) - 'a']++;
        }

        for (int i = 0; i < guess.length(); i++) {
            if (feedback.charAt(i) == '+') {
                if (guess.charAt(i) != word.charAt(i)) {
                    return false;
                }
                charInWordUsed[i] = true;
                charCountInWord[guess.charAt(i) - 'a']--;
            }
        }

        for (int i = 0; i < guess.length(); i++) {
            char c = guess.charAt(i);
            int idx = c - 'a';

            if (feedback.charAt(i) == 'o') {
                if (charCountInWord[idx] <= 0 || c == word.charAt(i)) {
                    return false;
                }
                charCountInWord[idx]--;
            }
        }

        for (int i = 0; i < guess.length(); i++) {
            char c = guess.charAt(i);
            int idx = c - 'a';

            if (feedback.charAt(i) == '-') {
                if (charCountInWord[idx] > 0 && !charInWordUsed[word.indexOf(c)]) {
                    return false;
                }
            }
        }

        return true;
    }

    private String burekConvert(List<Character> odziv) {
        StringBuilder sb = new StringBuilder();
        for (Character ch : odziv) {
            sb.append(ch);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // Test the class here
    }
}