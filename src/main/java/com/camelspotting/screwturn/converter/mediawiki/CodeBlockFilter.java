package com.camelspotting.screwturn.converter.mediawiki;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CodeBlockFilter implements TextFilter {

    @Override
    public String apply(String s) {
        Iterator<String> itr = FilterUtil.unmarshal(s).iterator();
        List<String> output = new ArrayList<>();
        outer:
        while (itr.hasNext()) {
            boolean lineChanged = false;
            String line = itr.next();

            if (!line.contains("{{")) {
                output.add(line);
                continue;
            }

            int idx = 0;
            StringBuilder sb = new StringBuilder();
            while (true) {
                int start = line.indexOf("{{", idx);
                int end = line.indexOf("}}", idx);

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
                    sb.append(line.substring(idx, start));

                    String expression = line.substring(start + 2, end);
                    sb.append("<code>").append(expression).append("</code>");

                    idx = end + 2;

                    lineChanged = true;
                }
            }

            String finalLine = sb.toString();
            if (lineChanged) {
                System.out.println("Changed line to: " + finalLine);
            }
            output.add(finalLine);


            /*if (start != -1 && end != -1) {
                String lineStart = line.substring(0, start);
                String expression = line.substring(start + 2, end);
                String lineEnd = line.substring(end + 2);

                output.add(lineStart+"<code>"+expression+"</code>"+lineEnd);
            } else {
                output.add(line);
            }*/
        }

        return FilterUtil.marshal(output);
    }
}
