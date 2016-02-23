package com.camelspotting.screwturn.converter.mediawiki;

import org.jdom2.Element;

public interface TextFilter {

    boolean filter(Element element);
}
