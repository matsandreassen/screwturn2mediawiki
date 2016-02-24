package com.camelspotting.screwturn.converter.mediawiki;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MultiLinePreFilter implements TextFilter {

    @Override
    public String apply(String element) {
        Iterator<String> itr = FilterUtil.unmarshal(element).iterator();
        List<String> output = new ArrayList<>();
        BlockType currentBlock = BlockType.NONE;
        while (itr.hasNext()) {
            String line = itr.next().trim();

            if (!line.contains("@@")) {
                output.add(line);
                continue;
            }

            int idx = line.indexOf("@@");

            switch (currentBlock) {
                case NONE:
                    if (idx != 0) {
                        output.add(line.substring(0, idx));
                    }

                    String endOfLine = line.substring(idx + 2).trim();

                    if (endOfLine.length() > 0) {
                        if (endOfLine.contains(" ")) {
                            output.add("<pre>");
                            output.add(endOfLine);
                            currentBlock = BlockType.REGULAR;
                        } else {
                            String langMarker = endOfLine;
                            String newLine = String.format("<syntaxhighlight lang=\"%s\" line=\"1\">", langMarker);
                            output.add(newLine);
                            System.out.println("Changed line to: " + newLine);
                            currentBlock = BlockType.HIGHLIGHTED;
                        }
                    } else {
                        String marker = "<pre>";
                        System.out.println("Changed line to: " + marker);
                        output.add(marker);

                        currentBlock = BlockType.REGULAR;
                    }
                    break;
                case HIGHLIGHTED:
                case REGULAR:
                    if (idx > 0) {
                        output.add(line.substring(0, idx));
                    }

                    String end = line.substring(idx + 2);
                    String marker = currentBlock == BlockType.REGULAR
                            ? "</pre>"
                            : "</syntaxhighlight>";

                    if (end.length() == 0) {
                        output.add(marker);
                    } else {
                        output.add(marker + end);
                    }
                    currentBlock = BlockType.NONE;
                    break;
            }
        }

        return FilterUtil.marshal(output);
    }

    private enum BlockType {
        NONE,
        REGULAR,
        HIGHLIGHTED

    }
}
