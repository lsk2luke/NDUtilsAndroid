package com.nelepovds.ndutils.rest;

import java.util.ArrayList;

/**
 * Created by dmitrynelepov on 29.09.14.
 */
public class NDResultData <RT extends BaseClass> extends BaseClass {
    public Integer offset;
    public Integer limit;
    public Integer total;
    public String error;
    public ArrayList<RT> data;
}