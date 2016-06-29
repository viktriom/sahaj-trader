package com.xyz.trader.data;

import com.xyz.trader.persistence.DataPersistence;

/**
 * Created by sonu on 27/06/16.
 */
public class PersistenceHandlerFactory {

    public PersistenceHandler getPersistenceHandler(String dataReaderType){
        PersistenceHandler persistenceHandler = null;
        if(dataReaderType.equals("File")){
            persistenceHandler = new DataPersistence();
        }
        return persistenceHandler;
    }

}
