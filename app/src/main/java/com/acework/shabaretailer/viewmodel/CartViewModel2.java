package com.acework.shabaretailer.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.acework.shabaretailer.model.Item;
import com.acework.shabaretailer.model.ItemInCart;
import com.acework.shabaretailer.model.Retailer;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel2 extends AndroidViewModel {
    private final List<ItemInCart> itemsInCart = new ArrayList<>();
    private final MutableLiveData<List<ItemInCart>> itemsInCartLive;
    private final MutableLiveData<Integer> orderTypeLive;
    private Retailer retailer = null;
    private int orderType = 3;

    public CartViewModel2(Application application) {
        super(application);
        itemsInCartLive = new MutableLiveData<>(itemsInCart);
        orderTypeLive = new MutableLiveData<>(orderType);
    }

    public MutableLiveData<List<ItemInCart>> getItemsInCartLive() {
        return itemsInCartLive;
    }

    public MutableLiveData<Integer> getOrderTypeLive() {
        return orderTypeLive;
    }

    public Retailer getRetailer() {
        return retailer;
    }

    public void setRetailer(Retailer retailer) {
        this.retailer = retailer;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public List<ItemInCart> getItemsInCart() {
        return itemsInCart;
    }

    public void setItemsInCart(List<Item> items) {
        itemsInCart.clear();
        for (Item item : items) {
            itemsInCart.add(new ItemInCart(item));
        }
    }
}
