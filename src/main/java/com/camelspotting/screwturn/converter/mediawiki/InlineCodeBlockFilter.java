package com.camelspotting.screwturn.converter.mediawiki;

import org.jdom2.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
                int end = start != -1 ? line.indexOf("@@", start + 1) : -1;

                if (start == -1 && end == -1) {
                    // No more inline code blocks on line
                    String lineEnd = line.substring(idx);
                    sb.append(lineEnd);
                    break;
                }

                if (start != -1 && end == -1) {
                    // Block does not end on line
                    if (sb.length() > 0) {
                        sb.append(line.substring(start));
                    } else {
                        sb.append(line);
                    }
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

            String finalLine = sb.toString();
            if (lineChanged) {
                changed = true;
                System.out.println("Changed line to: " + finalLine);
            }
            output.add(finalLine);
        }

        element.setText(FilterUtil.marshal(output));
        return changed;
    }
}
