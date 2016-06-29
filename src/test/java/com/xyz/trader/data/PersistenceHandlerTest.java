package com.xyz.trader.data;

import com.xyz.trader.TraderTestSetUp;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by sonu on 29/06/16.
 */
public class PersistenceHandlerTest extends TraderTestSetUp{

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testReadData() throws Exception {
        try {
            List<Object> objList = persistenceHandler.readData(null);
            fail("ReadData functionality broken.");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testWriteData() throws Exception {
        persistenceHandler.writeData("com.xyz.trader.bean.OrderStatus", null);
        List<Object> statusList = persistenceHandler.readData("com.xyz.trader.bean.OrderStatus");
        if(statusList == null) fail("Null list returned for empty file with only headers.");
        if(statusList.size() != 0) fail("List read contains records where as the baking file has only headers and no records.");

        try {
            persistenceHandler.writeData(null, null);
            fail("TraderException not thrown.");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}