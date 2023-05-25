package com.acework.shabaretailer.atlas;

public class Atlas {
    public static int ORDER_TYPE_WHOLESALE = 0;
    public static int ORDER_TYPE_CONSIGNMENT = 1;
    public static int ORDER_TYPE_COMMISSION = 2;

    public static String getOrderTypeAsString(int orderType) {
        if (orderType == ORDER_TYPE_COMMISSION) {
            return "Commission";
        } else if (orderType == ORDER_TYPE_CONSIGNMENT) {
            return "Consignment";
        } else if (orderType == ORDER_TYPE_WHOLESALE) {
            return "Wholesale";
        } else {
            return "Please set an order type";
        }
    }
}
