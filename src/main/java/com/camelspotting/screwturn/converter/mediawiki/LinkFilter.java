package com.camelspotting.screwturn.converter.mediawiki;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LinkFilter implements TextFilter {

    @Override
    public String apply(String s) {
        Iterator<String> itr = FilterUtil.unmarshal(s).iterator();
        List<String> output = new ArrayList<>();
        outer:
        while (itr.hasNext()) {
            boolean lineChanged = false;
            String line = itr.next();

            /**
             * External link: [[^http://wso2.org/library/2605|WS-Addressing in action]]
             */
            if (!line.contains("[[^")) {
                output.add(line);
                continue;
            }

            int idx = 0;
            StringBuilder sb = new StringBuilder();
            while (true) {
                int start = line.indexOf("[[^", idx);
                int end = start != -1 ? line.indexOf("]]", start + 1) : -1;

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

                    String url = line.substring(start + 3, end);
                    boolean piped = url.contains("|");
                    String name = null;
                    if (piped) {
                        name = url.substring(url.indexOf("|") + 1);
                        url = url.substring(0, url.indexOf("|"));
                    }

                    if (!piped) {
                        sb.append(url);
                    } else {
                        sb.append("[").append(url).append(" ").append(name).append("]");
                    }

                    idx = end + 2;
                    lineChanged = true;
                }
            }

            String finalLine = sb.toString();
            if (lineChanged) {
                System.out.println("Changed line to: " + finalLine);
            }
            output.add(finalLine);
        }

        return FilterUtil.marshal(output);
    }
}
