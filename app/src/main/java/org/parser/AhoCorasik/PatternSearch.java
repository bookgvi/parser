package org.parser.AhoCorasik;

import java.util.Optional;
import java.util.Arrays;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class PatternSearch {
    private final int ABC = 65536;
    private final int[][] g;
    private final int[] suffixArr;
    private final int[] out;
    private final int MAX_STATES;
    private final String[] words;

    public PatternSearch(String[] words) {
        this.words = Optional.ofNullable(words).orElse(new String[]{});
        this.MAX_STATES = Arrays.stream(this.words).map(String::length).reduce(Integer::sum).orElse(0) + 1;
        this.g = new int[this.MAX_STATES][this.ABC];
        this.suffixArr = new int[this.MAX_STATES];
        this.out = new int[this.MAX_STATES];

        buildStateMachine();
    }
        
    private void buildStateMachine() {
        for (int i = 0; i < MAX_STATES; ++i) {
            Arrays.fill(g[i], -1);
        }
        int states = 1;
        for (int i = 0, wordsCount = words.length; i < wordsCount; ++i) {
            int curState = 0;
            for (int j = 0, len = words[i].length(); j < len; ++j) {
                int ch = words[i].charAt(j);
                if (g[curState][ch] == -1) {
                    g[curState][ch] = states++;
                }
                curState = g[curState][ch];
            }
            out[curState] |= (1 << i);
        }

        Arrays.fill(suffixArr, -1);
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < ABC; ++i) {
            if (g[0][i] == -1) {
                g[0][i] = 0;
            } else if (g[0][i] > 0) {
                suffixArr[g[0][i]] = 0;
                q.add(g[0][i]);
            }
        }
        while (!q.isEmpty()) {
            int curState = q.poll();
            for (int ch = 0; ch < ABC; ++ch) {
                if (g[curState][ch] == -1) {
                    continue;
                }
                int suffLink = suffixArr[curState];
                if (g[suffLink][ch] == -1) {
                    suffLink = suffixArr[suffLink];
                }
                suffLink = g[suffLink][ch];
                suffixArr[g[curState][ch]] = suffLink;
                out[g[curState][ch]] |= out[suffLink];
                q.add(g[curState][ch]);
            }
        }
    }

    private int nextState(int curState, int ch) {
        while (g[curState][ch] == -1) {
            curState = suffixArr[curState];
        }
        return g[curState][ch];
    }

    public Map<String, List<Integer>> search(String text) {
        Map<String, List<Integer>> res = new HashMap<>();
        text = Optional.ofNullable(text).orElse("");
        int state = 0;
        for (int i = 0, len = text.length(); i < len; ++i) {
            state = nextState(state, text.charAt(i));
            if (out[state] == 0) {
                continue;
            }
            for (int j = 0, wordsCount = words.length; j < wordsCount; ++j) {
                if ((out[state] & (1 << j)) != 0) {
                    List<Integer> indexes = res.getOrDefault(words[j], new ArrayList<>());
                    indexes.add(i - words[j].length() + 1);
                    res.putIfAbsent(words[j], indexes);
                }
            }
        }
        return res;
    }
}
