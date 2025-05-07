package models;

import java.util.Arrays;
import java.util.List;

public class BadWordDetector {
    private static final List<String> badWords = Arrays.asList(
            "merde", "con", "idiot" // Ajoute les mots interdits ici
    );

    public static boolean containsBadWords(String text) {
        String lower = text.toLowerCase();
        return badWords.stream().anyMatch(lower::contains);
    }
}
