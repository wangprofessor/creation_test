package com.creation.unit_test.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lines {
    public static void main(String[] args) {
        String s = "";
        s += "a16\n\t";
        s += "a17\n\t";
        s += "a28\n\t";
        s += "a19\n\t";
        s += "a2b\n\t";
        s += "a2c\n\t";
        List<String> lines = Arrays.asList(s.split("\n\t"));

        lines = filterLines(lines, "ab", "ab");
        System.out.println(Arrays.toString(lines.toArray()));

    }

    private static List<String> deleteLines(List<String> lines, String... words) {
        int index = -1;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            boolean contains = false;
            for (String word : words) {
                if (line.contains(word)) {
                    contains = true;
                    break;
                }
            }
            if (contains) {
                index = i;
            } else {
                break;
            }
        }
        if (index == -1) {
            return lines;
        }
        List<String> remainLines = new ArrayList<>(lines.size() - index);
        for (int i = index + 1; i < lines.size(); i++) {
            String line = lines.get(i);
            remainLines.add(line);
        }
        return remainLines;
    }

    private static List<String> filterLines(List<String> lines, String... words) {
        List<String> filterLines = new ArrayList<>();
        for (String word : words) {
            for (String line : lines) {
                if (line.contains(word)) {
                    filterLines.add(line);
                    break;
                }
            }
        }
        return filterLines;
    }
}
