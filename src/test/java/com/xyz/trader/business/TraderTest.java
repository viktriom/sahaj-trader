package com.xyz.trader.business;

import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.list.mutable.ListAdapter;
import com.xyz.trader.TraderTestSetUp;
import com.xyz.trader.bean.OrderStatus;
import com.xyz.trader.properties.ApplicationProperties;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import com.gs.collections.api.block.function.Function;

import static org.junit.Assert.*;

public class TraderTest extends TraderTestSetUp{
    private Trader trader;

    private Predicate2<Object, String> FILTER_IN_ORDERS_BY_STATUS = (object, status) -> (((OrderStatus)object).getStatus().equals(status));
    private Function<Object, Integer> EXTRACT_ORDER_IDS = (object) -> (((OrderStatus)object).getStockId());
    private Function<Object, Integer> EXTRACT_ORDER_QUANTITIES = (object) -> (((OrderStatus)object).getRemainingQuantity());

    @Before
    public void setUp() throws Exception {
        super.setUp();
        trader = new Trader();
    }

    @Test
    public void testProcessTrades() throws Exception {
        trader.processTrades();

        List<Object> objectList = persistenceHandler.readData("com.xyz.trader.bean.OrderStatus");
        MutableList<Object> closedOrders = ListAdapter.adapt(objectList).selectWith(FILTER_IN_ORDERS_BY_STATUS, ApplicationProperties.getOrderCloseStatus());
        MutableList<Integer> closedOrderIds = closedOrders.collect(EXTRACT_ORDER_IDS);
        MutableList<Integer> closedOrderQuantities = closedOrders.collect(EXTRACT_ORDER_QUANTITIES);
        MutableList<Object> openOrders = ListAdapter.adapt(objectList).selectWith(FILTER_IN_ORDERS_BY_STATUS, ApplicationProperties.getOrderOpenStatus());
        MutableList<Integer> openOrderIds = openOrders.collect(EXTRACT_ORDER_IDS);
        MutableList<Integer> openOrderQuantities = openOrders.collect(EXTRACT_ORDER_QUANTITIES);

        assertEquals(closedOrderIds.size(), 3);

        assertEquals(openOrderIds.size(), 2);

        assertEquals(closedOrderIds.makeString(","),("1,2,4"));

        assertEquals(openOrderIds.makeString(","), "3,5");

        assertEquals(closedOrderQuantities.makeString(","),"0,0,0");

        assertEquals(openOrderQuantities.makeString(","),"3,3");

    }
}