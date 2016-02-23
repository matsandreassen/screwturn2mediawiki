package com.camelspotting.screwturn.converter.mediawiki;

import org.jdom2.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class InlineCodeBlockFilter implements TextFilter {

    @Override
    public boolean filter(Element element) {
        boolean changed = false;

        String[] lines = element.getText().split("\\n");
        Iterator<String> itr = Arrays.asList(lines).iterator();
        List<String> output = new ArrayList<>();
        outer:
        while (itr.hasNext()) {
            boolean lineChanged = false;
            String line = itr.next();

            if (!line.contains("@@")) {
                output.add(line);
                continue;
            }

            int idx = 0;
            StringBuilder sb = new StringBuilder();
            while (true) {
                int start = line.indexOf("@@", idx);
                int end = line.indexOf("@@", start + 1);

                if (start == -1) {
                    // No more inline code blocks on line
                    sb.append(line.substring(idx));
                    break;
                }

                if (end == -1) {
                    // Block does not end on line
                    sb.append(line.substring(start));
                    break;
                }

                if (start != end) {
                    sb.append(line.substring(0, start));

                    String expression = line.substring(start + 2, end);
                    sb.append("<code>").append(expression).append("</code>");

                    idx = end + 2;

                    lineChanged = true;
                    changed = true;
                }
            }

            String newLine = sb.toString();
            if (lineChanged) {
                changed = true;
                System.out.println("Changed line to: " + newLine);
            }
            output.add(newLine);
        }

        String txt = output.stream().collect(Collectors.joining("\n"));
        element.setText(txt);
        return changed;
    }
}
