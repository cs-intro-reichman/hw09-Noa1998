import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;

    // The window length used in this model.
    int windowLength;

    // The random number generator used by this model.
    private Random randomGenerator;

    /**
     * Constructs a language model with the given window length and a given
     * seed value. Generating texts from this model multiple times with the
     * same seed value will produce the same random texts. Good for debugging.
     */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /**
     * Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production.
     */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
    public void train(String fileName) {
        // Your code goes here
        In file = new In(fileName);
        char c;
        String window = "";
        while (!file.isEmpty()) {
            c = file.readChar();
            if (window.length() < windowLength) {
                window += c;
            } else {
                if (CharDataMap.containsKey(window)) {
                    CharDataMap.get(window).update(c);
                } else {
                    List probs = new List();
                    probs.addFirst(c);
                    CharDataMap.put(window, probs);
                }
                window = window.substring(1);
                window += c;
            }
        }
        for (String windows : CharDataMap.keySet()) {
            calculateProbabilities(CharDataMap.get(windows));
        }
    }

    // Computes and sets the probabilities (p and cp fields) of all the
    // characters in the given list. */
    public static void calculateProbabilities(List probs) {
        // Your code goes here
        int size = probs.getSize();
        int total_chars = 0;
        for (int i = 0; i < size; i++) {
            total_chars += probs.get(i).count;
        }
        double cProb = 0;
        Node pointer = probs.listIterator(0).current;
        while (pointer.next != null) {
            pointer.cp.p = (double) pointer.cp.count / total_chars;
            cProb += pointer.cp.p;
            pointer.cp.cp = cProb;
            pointer = pointer.next;
        }
        if (pointer.next == null) {
            pointer.cp.p = (double) pointer.cp.count / total_chars;
            pointer.cp.cp = 1;

        }
    }

    // Returns a random character from the given probabilities list.
    public char getRandomChar(List probs) {
        // Your code goes here
        double random_number = randomGenerator.nextDouble();
        int list_size = probs.getSize();
        for (int i = 0; i < list_size; i++) {
            CharData rel = probs.get(i);
            if (rel.cp > random_number) {
                return rel.chr;
            }
        }
        char chr = 'c';
        return chr;
    }

    /**
     * Generates a random text, based on the probabilities that were learned during
     * training.
     * 
     * @param initialText     - text to start with. If initialText's last substring
     *                        of size numberOfLetters
     *                        doesn't appear as a key in Map, we generate no text
     *                        and return only the initial text.
     * @param numberOfLetters - the size of text to generate
     * @return the generated text
     */
    public String generate(String initialText, int textLength) {
        // Your code goes here
       if(initialText.length() < windowLength){
            return initialText;
        }
        StringBuilder window = new StringBuilder(initialText.substring(initialText.length() - windowLength));
        StringBuilder generatedText = window;

        while (generatedText.length() < (textLength + windowLength)){
            List currentList = CharDataMap.get(window.toString());
            if (currentList == null){
                break;
            }
            generatedText.append(getRandomChar(currentList));
            window = new StringBuilder(generatedText.substring(generatedText.length() - windowLength));
        }

        return generatedText.toString();
    }

    /** Returns a string representing the map of this language model. */
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (String key : CharDataMap.keySet()) {
            List keyProbs = CharDataMap.get(key);
            s.append(key + " : " + keyProbs + "\n");
        }
        return s.toString();
    }

    public static void main(String[] args) {
       
    }

}
