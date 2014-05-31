package com.nelepovds.ndutils.rest;

import java.util.ArrayList;

/**
 * Created by dmitrynelepov on 17.05.14.
 */
public class NDRestFilter {

    public static class NDSearchItem {
        public String field;
        public String value;
    }

    public ArrayList<NDSearchItem> searchItems = new ArrayList<NDSearchItem>();

    public Integer limit=-1;

    public Integer offset=0;



}
