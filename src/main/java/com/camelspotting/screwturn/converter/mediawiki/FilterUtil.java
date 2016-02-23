package com.camelspotting.screwturn.converter.mediawiki;

import java.util.List;
import java.util.stream.Collectors;

public final class FilterUtil {

    private FilterUtil() {
    }

    public static String marshal(List<String> lines) {
        return lines.stream().collect(Collectors.joining("\n"));
    }
}
