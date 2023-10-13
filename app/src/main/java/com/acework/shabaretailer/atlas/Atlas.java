package com.acework.shabaretailer.atlas;

public class Atlas {
    public static int WPW = 4700;
    public static int WPC = 5900;
    public static int WPS = 7225;
    public static int TPW = 4000;
    public static int TPC = 4500;
    public static int TPS = 5100;

    public static int getWahuraPrice(String orderType) {
        if (orderType.equals("Wholesale")) return WPW;
        if (orderType.equals("Consignment")) return WPC;
        return WPS;
    }

    public static int getTwendePrice(String orderType) {
        if (orderType.equals("Wholesale")) return TPW;
        if (orderType.equals("Consignment")) return TPC;
        return TPS;
    }

    public static String getItemName(String sku) {
        if (sku.equals("1")) return "Twende sling bag";
        if (sku.equals("3")) return "Wahura bucket bag";
        return "Unknown bag";
    }
}
