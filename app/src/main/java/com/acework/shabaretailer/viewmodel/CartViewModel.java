package com.acework.shabaretailer.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.acework.shabaretailer.model.Item;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class CartViewModel extends AndroidViewModel {
    public static final int ORDER_TYPE_WHOLESALE = 0;
    public static final int ORDER_TYPE_CONSIGNMENT = 1;
    public static final int ORDER_TYPE_COMMISSION = 2;
    private final MutableLiveData<List<Item>> cart;
    private final MutableLiveData<Integer> orderType;

    public CartViewModel(@NonNull Application application) {
        super(application);
        cart = new MutableLiveData<>(new ArrayList<>());
        orderType = new MutableLiveData<>(ORDER_TYPE_WHOLESALE);
    }

    public MutableLiveData<List<Item>> getCart() {
        return cart;
    }

    public MutableLiveData<Integer> getOrderType() {
        return orderType;
    }

    public void setOrderType(int newOrderType) {
        orderType.setValue(newOrderType);
        refresh();
    }

    public Item getItemFromCart(String sku, String insertColor) {
        for (Item itemInCart : cart.getValue()) {
            if (itemInCart.getSku().equals(sku) && itemInCart.getInsertColour().equals(insertColor))
                return itemInCart;
        }
        return null;
    }

    public void removeItemFromCart(Item itemToRemove) {
        cart.getValue().remove(itemToRemove);
        refresh();
    }

    public void refresh() {
        List<Item> itemsToRemove = new ArrayList<>();
        for (Item i : cart.getValue()) {
            if (i.getQuantity() < 1) {
                itemsToRemove.add(i);
            }
        }
        for (Item i : itemsToRemove) {
            cart.getValue().remove(i);
        }
        cart.setValue(cart.getValue());
        orderType.setValue(orderType.getValue());
    }

    public void resetCart() {
        for (Item item : cart.getValue()) {
            item.setQuantity(0);
        }
        refresh();
    }

    public void setItem(Item item) {
        Item itemInCart = getItemFromCart(item.getSku(), item.getInsertColour());
        if (itemInCart == null) {
            cart.getValue().add(item);
        } else {
            if (item.getQuantity() == 0) {
                cart.getValue().remove(itemInCart);
            } else {
                itemInCart.setQuantity(item.getQuantity());
            }
        }
    }
}
