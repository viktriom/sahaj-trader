package com.xyz.trader.data;

import com.xyz.trader.exceptions.TraderException;
import com.vt.o2f.exceptions.UnmappedBeanException;

import java.util.List;

/**
 * Created by sonu on 27/06/16.
 */
public interface PersistenceHandler {
    public List<Object> readData(String fullyQualifiedClassName) throws UnmappedBeanException, TraderException;
    public void writeData(String fullyQualifiedClassName, List<Object> objects) throws UnmappedBeanException, TraderException;
}
