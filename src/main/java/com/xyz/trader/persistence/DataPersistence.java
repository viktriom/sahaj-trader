package com.vt.o2f;

import com.vt.o2f.exceptions.PersistenceException;
import com.xyz.trader.data.PersistenceHandler;
import com.vt.o2f.binder.FileDataBinder;
import com.vt.o2f.dataWriter.BeanToFileConverter;
import com.vt.o2f.exceptions.UnmappedBeanException;
import com.xyz.trader.exceptions.TraderException;

import java.util.List;

/**
 * Created by sonu on 28/06/16.
 */
public class DataPersistence implements PersistenceHandler {
    BeanToFileConverter beanToFileConverter;
    FileDataBinder fileDataBinder;

    public DataPersistence(){
        beanToFileConverter = new BeanToFileConverter();
        fileDataBinder = new FileDataBinder();
    }


    @Override
    public List<Object> readData(String fullyQualifiedClassName) throws UnmappedBeanException, TraderException {
        List<Object> list = null;
        try{
            list = fileDataBinder.readData(fullyQualifiedClassName);
        }catch (PersistenceException ex){
            throw new TraderException(ex.getMessage());
        }
        return list;
    }

    @Override
    public void writeData(String fullyQualifiedClassName, List<Object> objects) throws UnmappedBeanException, TraderException {
        try{
        beanToFileConverter.writeData(fullyQualifiedClassName, objects);
        }catch (PersistenceException ex){
            throw new TraderException(ex.getMessage());
        }
    }
}
