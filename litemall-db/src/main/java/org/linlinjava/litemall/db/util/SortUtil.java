package org.linlinjava.litemall.db.util;

public class SortUtil {
    public static final String goodsSort(String sort, String order){
        if(sort == null){
            return null;
        }

        String sortWithOrder = "";
        if(sort.equalsIgnoreCase("price")){
            sortWithOrder = "retail_price";
        }
        else if(sort.equalsIgnoreCase("default") || sort.equalsIgnoreCase("category")){
            return null;
        }
        else{
            return null;
        }

        if(order == null) {
            return sortWithOrder;
        }

        if(order.equalsIgnoreCase("asc")){
            sortWithOrder += " ASC";
        }
        else if(order.equalsIgnoreCase("DESC")){
            sortWithOrder += " DESC";
        }

        return sortWithOrder;
    }
}
