package com.acework.shabaretailer.atlas;

import com.acework.shabaretailer.model.Item;
import com.acework.shabaretailer.model.ItemInCart;

import java.util.ArrayList;
import java.util.List;

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

    // get individual items in cart from the simplified version stored in the cart
    public static List<Item> getItemsInCart(List<ItemInCart> itemsInCart) {
        List<Item> items = new ArrayList<>();
        for (ItemInCart itemInCart : itemsInCart) {
            if (itemInCart.getMustardInsertNum() > 0) {
                Item itemToAdd = itemInCart.getItem().cloneItemWithZeroQuantity("Mustard");
                itemToAdd.setQuantity(itemInCart.getMustardInsertNum());
                items.add(itemToAdd);
            }
            if (itemInCart.getMaroonInsertNum() > 0) {
                Item itemToAdd = itemInCart.getItem().cloneItemWithZeroQuantity("Maroon");
                itemToAdd.setQuantity(itemInCart.getMaroonInsertNum());
                items.add(itemToAdd);
            }
            if (itemInCart.getDarkBrownInsertNum() > 0) {
                Item itemToAdd = itemInCart.getItem().cloneItemWithZeroQuantity("Dark brown");
                itemToAdd.setQuantity(itemInCart.getDarkBrownInsertNum());
                items.add(itemToAdd);
            }
        }
        return items;
    }

    // calculate total value of items in the cart
    public static int calculateItemTotal(int orderType, List<ItemInCart> itemsInCart) {
        int itemTotal = 0;

        for (ItemInCart itemInCart : itemsInCart) {
            int priceToUse=getPriceToUse(itemInCart.getItem(), orderType);
            itemTotal += itemInCart.getMustardInsertNum() * priceToUse;
            itemTotal += itemInCart.getMaroonInsertNum() * priceToUse;
            itemTotal += itemInCart.getDarkBrownInsertNum() * priceToUse;
        }
        return itemTotal;
    }


    // determine item price based on order type
    public static int getPriceToUse(Item item, int orderType) {
        if (orderType == Atlas.ORDER_TYPE_COMMISSION) {
            return item.getPriceShaba();
        } else if (orderType == Atlas.ORDER_TYPE_CONSIGNMENT) {
            return item.getPriceConsignment();
        } else {
            return item.getPriceWholesale();
        }
    }

    // calculate total weight of the items in the cart
    public static int calculateItemWeight(List<ItemInCart> itemsInCart) {
        int itemWeight = 0;

        for (ItemInCart itemInCart : itemsInCart) {
            itemWeight += itemInCart.getMustardInsertNum() * itemInCart.getItem().getWeight();
            itemWeight += itemInCart.getMaroonInsertNum() * itemInCart.getItem().getWeight();
            itemWeight += itemInCart.getDarkBrownInsertNum() * itemInCart.getItem().getWeight();
        }
        return itemWeight;
    }

    // calculate estimated transport cost
    public static int calculateEstimatedTransportCost(String county, int weight) {
        if (county.equals("Nairobi")) {
            return calculateTransportCost(weight, 250);
        } else {
            return calculateTransportCost(weight, 500);
        }
    }

    public static int calculateTransportCost(int weight, int costPerKG) {
        int kg = weight / 1000;
        int remainder = weight % 1000;
        if (remainder > 0) kg += 1;
        return kg * costPerKG;
    }
}
