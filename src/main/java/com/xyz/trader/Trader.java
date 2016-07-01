package com.xyz.trader;

import com.xyz.trader.data.PersistenceHandler;
import com.xyz.trader.data.PersistenceHandlerFactory;
import com.xyz.trader.exceptions.AppInitException;
import com.xyz.trader.exceptions.TraderException;
import com.vt.o2f.exceptions.UnmappedBeanException;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by sonu on 28/06/16.
 */
public class Trader {

    private static Logger log = Logger.getLogger(Trader.class);

    public static void main(String[] args) throws UnmappedBeanException {
        log.info("Trader Application started.");
        com.xyz.trader.business.Trader trader = new com.xyz.trader.business.Trader();
        try {
            trader.processTrades();
        } catch (AppInitException e) {
            log.error("Error encountered During application initialization.");
            e.printStackTrace();
        } catch (TraderException e) {
            log.error("An error encountered during trade resolution process.");
            e.printStackTrace();
        }

    }
}
