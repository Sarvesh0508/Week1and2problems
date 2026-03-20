package Problem4;

import java.util.*;

public class PlagiarismDetector {

    // n-gram → set of document IDs
    private HashMap<String, Set<String>> nGramIndex;
    private int N = 5; // 5-gram

    public PlagiarismDetector() {
        nGramIndex = new HashMap<>();
    }

    // Generate n-grams
    private List<String> generateNGrams(String text) {
        List<String> ngrams = new ArrayList<>();
        String[] words = text.split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder gram = new StringBuilder();
            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }
            ngrams.add(gram.toString().trim());
        }
        return ngrams;
    }

    // Add document to database
    public void addDocument(String docId, String content) {
        List<String> ngrams = generateNGrams(content);

        for (String gram : ngrams) {
            nGramIndex.putIfAbsent(gram, new HashSet<>());
            nGramIndex.get(gram).add(docId);
        }
    }

    // Analyze new document
    public void analyzeDocument(String docId, String content) {
        List<String> ngrams = generateNGrams(content);
        Map<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {
            if (nGramIndex.containsKey(gram)) {
                for (String existingDoc : nGramIndex.get(gram)) {
                    matchCount.put(existingDoc,
                            matchCount.getOrDefault(existingDoc, 0) + 1);
                }
            }
        }

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        // Find similarity
        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {
            String otherDoc = entry.getKey();
            int matches = entry.getValue();

            double similarity = (matches * 100.0) / ngrams.size();

            System.out.println("Found " + matches +
                    " matching n-grams with \"" + otherDoc + "\"");
            System.out.printf("Similarity: %.2f%% ", similarity);

            if (similarity > 60) {
                System.out.println("(PLAGIARISM DETECTED)");
            } else if (similarity > 15) {
                System.out.println("(Suspicious)");
            } else {
                System.out.println("(Safe)");
            }
        }
    }

    // Main test
    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        // Existing documents
        String doc1 = "AI is transforming the world with advanced technologies and innovation";
        String doc2 = "Machine learning and AI are changing the world rapidly with new innovation";

        detector.addDocument("essay_089.txt", doc1);
        detector.addDocument("essay_092.txt", doc2);

        // New submission
        String newDoc = "AI is transforming the world with advanced technologies and innovation in society";

        detector.analyzeDocument("essay_123.txt", newDoc);
    }
}