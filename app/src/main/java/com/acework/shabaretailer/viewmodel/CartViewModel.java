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
    private final MutableLiveData<List<Item>> cart;

    public CartViewModel(@NonNull Application application) {
        super(application);
        cart = new MutableLiveData<>(new ArrayList<>());
    }

    public MutableLiveData<List<Item>> getCart() {
        return cart;
    }

    public Item getItemFromCart(String sku, String insertColor) {
        for (Item itemInCart : cart.getValue()) {
            if (itemInCart.getSku().equals(sku) && itemInCart.getInsertColor().equals(insertColor))
                return itemInCart;
        }
        return null;
    }

    public void removeItemFromCart(Item itemToRemove) {
        cart.getValue().remove(itemToRemove);
        refresh();
    }

    public void refresh() {
        cart.setValue(cart.getValue());
    }

    public void resetCart() {
        for (Item item : cart.getValue()) {
            item.setQuantity(0);
        }
        refresh();
    }

    public void setItem(Item item) {
        Item itemInCart = getItemFromCart(item.getSku(), item.getInsertColor());
        if (itemInCart == null) {
            cart.getValue().add(item);
        } else {
            if (item.getQuantity() == 0) {
                cart.getValue().remove(itemInCart);
            } else {
                itemInCart.setQuantity(item.getQuantity());
            }
        }
        refresh();
    }
}
