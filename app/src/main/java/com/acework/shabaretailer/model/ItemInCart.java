package com.acework.shabaretailer.model;

/*
 this class contains information regarding a single handbag in the cart
 and the number of bags in the order for each insert color
 */

public class ItemInCart {
    private final Item item;
    private int mustardInsertNum = 0;
    private int maroonInsertNum = 0;
    private int darkBrownInsertNum = 0;

    public ItemInCart(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
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

    public int getQuantity() {
        return maroonInsertNum + mustardInsertNum + darkBrownInsertNum;
    }

    public void decrementMustard(int quantity) {
        if (mustardInsertNum > 0) {
            mustardInsertNum = mustardInsertNum - quantity;
            if (mustardInsertNum < 0) mustardInsertNum = 0;
        }
    }

    public void decrementMaroon(int quantity) {
        if (maroonInsertNum > 0) {
            maroonInsertNum = maroonInsertNum - quantity;
            if (maroonInsertNum < 0) maroonInsertNum = 0;
        }
    }

    public void decrementDarkBrown(int quantity) {
        if (darkBrownInsertNum > 0) {
            darkBrownInsertNum = darkBrownInsertNum - quantity;
            if (darkBrownInsertNum < 0) darkBrownInsertNum = 0;
        }
    }

    public void incrementMustard(int quantity) {
        if (mustardInsertNum <30) {
            mustardInsertNum = mustardInsertNum + quantity;
            if (mustardInsertNum > 30) mustardInsertNum = 30;
        }
    }

    public void incrementMaroon(int quantity) {
        if (maroonInsertNum <30) {
            maroonInsertNum = maroonInsertNum + quantity;
            if (maroonInsertNum > 30) maroonInsertNum = 30;
        }
    }

    public void incrementDarkBrown(int quantity) {
        if (darkBrownInsertNum <30) {
            darkBrownInsertNum = darkBrownInsertNum + quantity;
            if (darkBrownInsertNum > 30) darkBrownInsertNum = 30;
        }
    }
}
