package com.cloudfordev.itil;

import java.util.ArrayList;

/**
 * A record in an ITIL prescribed database such as the CMDB or ICMDB.
 * 
 * @author u1001
 * @version 1.0
 */
public class ITILRow {
    /*
     *  Because it is unknown what objects types may be contained by the table, the ArrayList does
     *  not use generics.
     */
    @SuppressWarnings("rawtypes")
    private ArrayList fields;

    /**
     * Instantiate a new ITILRow
     */
    @SuppressWarnings("rawtypes")
    public ITILRow() {
            fields = new ArrayList();
    }

    /**
     * Add an Object to the ITILRow
     *
     * @param o The Object to add
     */
    @SuppressWarnings("unchecked")
    public void add(Object o) {
            fields.add(o);
    }

    /**
     * Get an object based on a supplied index position
     *
     * @param index The index of the Object to return
     * @return Object the Object requested
     */
    public Object getObject(int index) {
            return fields.get(index);
    }
}
