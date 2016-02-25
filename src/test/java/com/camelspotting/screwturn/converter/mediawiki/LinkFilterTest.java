package com.camelspotting.screwturn.converter.mediawiki;

import org.junit.Test;

import static org.junit.Assert.*;

public class LinkFilterTest {

    @Test
    public void testApply() throws Exception {
        String input = "noe [[^http://wso2.org/library/2605|WS-Addressing in action]] annet";
        String actual = new LinkFilter().apply(input);
        assertEquals("noe [http://wso2.org/library/2605 WS-Addressing in action] annet", actual);
    }
}