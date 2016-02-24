package com.camelspotting.screwturn.converter.mediawiki;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class FilterUtil {

    private FilterUtil() {
    }

    public static List<String> unmarshal(String text) {
        String[] lines = text.split("\\n");
        return Arrays.asList(lines);
    }

    public static String marshal(List<String> lines) {
        return lines.stream().collect(Collectors.joining("\n"));
    }
}
