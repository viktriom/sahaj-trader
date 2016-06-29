package com.xyz.trader.properties;

import com.xyz.trader.TraderTestSetUp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by sonu on 27/06/16.
 */
public class ApplicationPropertiesTest extends TraderTestSetUp {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testGetTraderDataFileName() throws Exception {
        assertEquals(ApplicationProperties.getTraderDataFileName(), "tradeData.csv");
    }

    @Test
    public void testGetIsFirstRowHeader() throws Exception {
        assertEquals(ApplicationProperties.getIsFirstRowHeader(), true);
    }

    @Test
    public void testGetTraderDataFileDelimiter() throws Exception {
        assertEquals(ApplicationProperties.getTraderDataFileDelimiter(),",");
    }

    @Test
    public void testGetDataFileDirectory() throws Exception{
        assertEquals(ApplicationProperties.getDataFileDirectory(),"/Users/sonu/dev/proj/java/trader/src/test/res/");
    }

    @After
    public void tearDown() throws Exception {

    }
}