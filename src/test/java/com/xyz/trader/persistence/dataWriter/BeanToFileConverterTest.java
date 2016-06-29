package com.xyz.trader.persistence.dataWriter;

import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.impl.list.mutable.ListAdapter;
import com.xyz.trader.bean.OrderStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.fail;

/**
 * Created by sonu on 28/06/16.
 */
public class BeanToFileConverterTest extends PersistenceTestSetup{

    BeanToFileConverter persistence;

    Predicate2<Object,Object> FIND_EQUAL_OBJECTS = (object1, object2) -> {
        OrderStatus os1 = (OrderStatus)object1;
        OrderStatus os2 = (OrderStatus)object2;
        return os1.equals(os2);
    };

    @Before
    public void setUp() throws Exception {
        super.setUp();
        prepareTestBeans();
        persistence = new BeanToFileConverter("com.xyz.trader.bean.OrderStatus");
    }

    @Test
    public void testWriteBeansToFile() {
        try {
            persistence.writeBeansToFile(orderStatusList);
            List<Object> orderStatus = persistenceHandler.readData("com.xyz.trader.bean.OrderStatus");
            orderStatus.stream().filter(obj -> !ListAdapter.adapt(orderStatusList).anySatisfyWith(FIND_EQUAL_OBJECTS, obj))
                    .forEach(obj -> fail("The List of objects written and the list of objects read from the database is not same."));
        }catch(Exception e){
            fail("Unable to persist bean successfully.");
        }
    }
}