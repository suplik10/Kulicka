package cz.kulicka.util;

import cz.kulicka.constant.CurrenciesConstants;
import cz.kulicka.entity.Ticker;
import cz.kulicka.timer.InstaBuyAndInstaSellTimer;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CommonUtil {

    static Logger log = Logger.getLogger(CommonUtil.class);

    public static boolean addTickerToDBList(ArrayList<Ticker> DBList, String newSymbol, List<String> whiteList) {
        Validate.notNull(newSymbol);

        if(DBList == null){
            DBList = new ArrayList<>();
        }
        //TODO white list from properties
        //for (int i = 0; i < CurrenciesConstants.BLACK_LIST.size(); i++) {
          //  if(symbolToFind.contains(CurrenciesConstants.BLACK_LIST.get(i))){
            //    return false;
            //}
        //}

        for (int y = 0; y < DBList.size(); y++) {
            if(DBList.get(y).getSymbol().equals(newSymbol))
                return false;
        }
        return true;
    }
}
