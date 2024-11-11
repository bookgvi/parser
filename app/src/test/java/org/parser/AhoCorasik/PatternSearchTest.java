package org.parser.AhoCorasik;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class PatternSearchTest {
        // Searches for patterns in a non-empty text and returns correct indices
    @Test
    public void test_search_patterns_in_non_empty_text() {
        PatternSearch patternSearch = new PatternSearch(new String[]{"ab", "abc"});
        String text = "ababcab";
        Map<String, List<Integer>> expected = new HashMap<>();
        expected.put("ab", Arrays.asList(0, 2, 5));
        expected.put("abc", Arrays.asList(2));
        Map<String, List<Integer>> result = patternSearch.search(text);
        assertEquals(expected, result);
    }

    // Initialize PatternSearch with a non-empty array of words and search for matches in a text
    @Test
    public void test_search_with_non_empty_words() {
        String[] words = {"he", "she", "his", "hers"};
        PatternSearch patternSearch = new PatternSearch(words);
        String text = "ahishers";
        Map<String, List<Integer>> result = patternSearch.search(text);
    
        assertEquals(1, result.get("he").size());
        assertEquals(4, result.get("he").get(0).intValue());
    
        assertEquals(1, result.get("she").size());
        assertEquals(3, result.get("she").get(0).intValue());
    
        assertEquals(1, result.get("his").size());
        assertEquals(1, result.get("his").get(0).intValue());
    
        assertEquals(1, result.get("hers").size());
        assertEquals(4, result.get("hers").get(0).intValue());
    }

    // Initialize PatternSearch with null as the words array
    @Test
    public void test_search_with_null_words() {
        PatternSearch patternSearch = new PatternSearch(null);
        String text = "ahishers";
        Map<String, List<Integer>> result = patternSearch.search(text);
    
        assertTrue(result.isEmpty());
    }

}