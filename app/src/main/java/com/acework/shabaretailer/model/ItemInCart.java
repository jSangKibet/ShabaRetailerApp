package com.acework.shabaretailer.model;

/*
 this class contains information regarding a single handbag in the cart
 and the number of bags in the order for each insert color
 */

public class ItemInCart {
    private final Item item;
    private int mustardInsertNum=0;
    private int maroonInsertNum=0;
    private int darkBrownInsertNum=0;

    public Item getItem() {
        return item;
    }

    public ItemInCart(Item item) {
        this.item = item;
    }

    public int getMustardInsertNum() {
        return mustardInsertNum;
    }

    public void setMustardInsertNum(int mustardInsertNum) {
        this.mustardInsertNum = mustardInsertNum;
    }

    public int getMaroonInsertNum() {
        return maroonInsertNum;
    }

    public void setMaroonInsertNum(int maroonInsertNum) {
        this.maroonInsertNum = maroonInsertNum;
    }

    public int getDarkBrownInsertNum() {
        return darkBrownInsertNum;
    }

    public void setDarkBrownInsertNum(int darkBrownInsertNum) {
        this.darkBrownInsertNum = darkBrownInsertNum;
    }
}
