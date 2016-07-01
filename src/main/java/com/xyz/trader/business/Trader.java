package com.xyz.trader.business;

import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.list.mutable.FastList;
import com.xyz.trader.bean.Order;
import com.xyz.trader.bean.OrderStatus;
import com.xyz.trader.data.PersistenceHandler;
import com.xyz.trader.data.PersistenceHandlerFactory;
import com.xyz.trader.exceptions.AppInitException;
import com.xyz.trader.exceptions.TraderException;
import com.vt.o2f.PersistenceProperties;
import com.vt.o2f.exceptions.UnmappedBeanException;
import com.xyz.trader.properties.ApplicationProperties;
import com.xyz.trader.util.PropertyReader;
import javafx.application.Application;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by sonu on 28/06/16.
 */
public class Trader {

    private Logger log = Logger.getLogger(Trader.class);
    private List<Object> tradeOrders;
    private PersistenceHandler persistenceHandler;
    private MutableList<Object> orderStatusList;

    private Predicate2<Object, String> ACCEPT_SAME_COMPANY_ORDERS = (object, companyName) -> {
        OrderStatus orderStatus = (OrderStatus)object;
        return orderStatus.getCompany().equals(companyName);
    };

    private Predicate2<Object,String> ACCEPT_COUNTER_ORDERS = (object, currentOrderSide) -> {
        OrderStatus orderStatus = (OrderStatus)object;
        return !orderStatus.getSide().equals(currentOrderSide);
    };

    private Predicate2<Object, String> ACCEPT_OPEN_ORDERS = (object, openOrderStatus) -> {
        OrderStatus orderStatus = (OrderStatus)object;
        return orderStatus.getStatus().equals(openOrderStatus);
    };

    public Trader(){

    }

    public void processTrades() throws AppInitException, UnmappedBeanException, TraderException {

        initializeApplication();
        readTradeData();
        resolveOrders();
        persistResolvedOrderStatus();
    }

    private void initializeApplication() throws AppInitException {
        initializeSystemProperties();
        initializePersistenceSystem();
    }

    private void initializeSystemProperties() throws AppInitException {
        PropertyReader propertyReader = new PropertyReader(
                "trader.properties",
                "com.xyz.trader.properties.ApplicationProperties");
        propertyReader.initializeClassFieldsFromPropertiesFile(true);
    }

    private void initializePersistenceSystem() {
        PersistenceProperties.setDataFileDelimiter(ApplicationProperties.getTraderDataFileDelimiter());
        PersistenceProperties.setDataFileDirectory(ApplicationProperties.getDataFileDirectory());
        PersistenceProperties.setGetterMethodPrefix(ApplicationProperties.getGetterMethodPrefix());
        PersistenceProperties.setSetterMethodPrefix(ApplicationProperties.getSetterMethodPrefix());
        PersistenceProperties.setIsFirstRowHeader(ApplicationProperties.getIsFirstRowHeader());
        PersistenceHandlerFactory factory = new PersistenceHandlerFactory();
        persistenceHandler = factory.getPersistenceHandler("File");
    }

    private void readTradeData() throws UnmappedBeanException, TraderException {
        tradeOrders = persistenceHandler.readData("com.xyz.trader.bean.Order");
    }

    private void resolveOrders() {
        prepareOrderStatusList();
        for(Object obj : orderStatusList) {
            OrderStatus order = (OrderStatus) obj;
            if(order.getStatus().equals(ApplicationProperties.getOrderCloseStatus())) continue;
            computeStatusForCurrentOrderStatus(order);
        }
    }

    private void prepareOrderStatusList() {
        orderStatusList= FastList.newList();
        for(Object obj : tradeOrders){
            Order order = (Order) obj;
            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setStockId(order.getStockId());
            orderStatus.setCompany(order.getCompany());
            orderStatus.setQuantity(order.getQuantity());
            orderStatus.setSide(order.getSide());
            orderStatus.setRemainingQuantity(order.getQuantity());
            orderStatus.setStatus(ApplicationProperties.getOrderOpenStatus());
            orderStatusList.add(orderStatus);
        }
    }

    private void computeStatusForCurrentOrderStatus(final OrderStatus orderStatus) {
        MutableList<Object> counterOrderStatuses = orderStatusList.selectWith(ACCEPT_SAME_COMPANY_ORDERS, orderStatus.getCompany())
                .selectWith(ACCEPT_COUNTER_ORDERS, orderStatus.getSide())
                .selectWith(ACCEPT_OPEN_ORDERS, ApplicationProperties.getOrderOpenStatus());
        for(Object obj : counterOrderStatuses){
            OrderStatus odrStatus = (OrderStatus)obj;
            if(orderStatus.equals(odrStatus))continue;
            if(!computeRemainingQuantityAndGetStatus(orderStatus, odrStatus).equals(ApplicationProperties.getOrderOpenStatus()))
                break;
        }
    }

    private String computeRemainingQuantityAndGetStatus(OrderStatus currentOrder, OrderStatus targetOrder) {
        Integer remainingQuantity = currentOrder.getRemainingQuantity() - targetOrder.getRemainingQuantity();
        String status =ApplicationProperties.getOrderCloseStatus();
        if(remainingQuantity == 0){
            targetOrder.setRemainingQuantity(0);
            targetOrder.setStatus(ApplicationProperties.getOrderCloseStatus());
            currentOrder.setRemainingQuantity(0);
            currentOrder.setStatus(ApplicationProperties.getOrderCloseStatus());
        }else if(remainingQuantity > 0){
            currentOrder.setRemainingQuantity(remainingQuantity);
            targetOrder.setRemainingQuantity(0);
            targetOrder.setStatus(ApplicationProperties.getOrderCloseStatus());
            status = ApplicationProperties.getOrderOpenStatus();
        }else if(remainingQuantity < 0){
            currentOrder.setRemainingQuantity(0);
            currentOrder.setStatus(ApplicationProperties.getOrderCloseStatus());
            targetOrder.setRemainingQuantity((0 - remainingQuantity));
        }
        return status;
    }

    private void persistResolvedOrderStatus() throws UnmappedBeanException, TraderException {
        persistenceHandler.writeData("com.xyz.trader.bean.OrderStatus", orderStatusList);
    }
}