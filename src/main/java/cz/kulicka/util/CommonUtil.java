package cz.kulicka.util;

import cz.kulicka.constant.CurrenciesConstants;
import cz.kulicka.entity.Ticker;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;

public class CommonUtil {

    public static boolean addTickerToDBList(ArrayList<Ticker> DBList, String symbolToFind) {
        Validate.notNull(symbolToFind);

        if(DBList == null){
            DBList = new ArrayList<>();
        }

        for (int i = 0; i < CurrenciesConstants.BLACK_LIST.size(); i++) {
            if(symbolToFind.contains(CurrenciesConstants.BLACK_LIST.get(i))){
                return false;
            }
        }

        for (int y = 0; y < DBList.size(); y++) {
            if(DBList.get(y).getSymbol().equals(symbolToFind))
                return false;
        }
        return true;
    }

    public static int removeTickerFromDBList(ArrayList<Ticker> DBList, String symbolToFind) {
        Validate.notNull(symbolToFind);
        Validate.notNull(DBList);

        boolean symbolBlacklisted = false;

        for (int i = 0; i < CurrenciesConstants.BLACK_LIST.size(); i++) {
            if(symbolToFind.contains(CurrenciesConstants.BLACK_LIST.get(i))){
                symbolBlacklisted = true;
            }
        }

        if(symbolBlacklisted){
            for (int i = 0; i < DBList.size(); i++) {
                if(DBList.get(i).getSymbol().contains(symbolToFind)){
                    return i;
                }
            }
        }

        return -1;

    }
}
