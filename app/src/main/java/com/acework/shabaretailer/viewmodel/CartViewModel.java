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

    public void setCartItems(List<Item> itemsToSet) {
        List<Item> newCart = new ArrayList<>();
        for (Item itemToSet : itemsToSet) {
            Item itemInCart = getItemFromCart(itemToSet.getSku());
            if (itemInCart == null) {
                newCart.add(itemToSet);
            } else {
                newCart.add(itemToSet);
            }
        }
        cart.setValue(newCart);
    }

    private Item getItemFromCart(String sku) {
        for (Item itemInCart : cart.getValue()) {
            if (itemInCart.getSku().equals(sku)) return itemInCart;
        }
        return null;
    }

    public void removeItemFromCart(String sku) {
        for (Item item : cart.getValue()) {
            if (item.getSku().equals(sku)) {
                item.setQuantity(0);
                break;
            }
        }
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

    public void setQuantity(String sku, int quantity) {
        for (Item item : cart.getValue()) {
            if (item.getSku().equals(sku)) {
                item.setQuantity(quantity);
                break;
            }
        }
        refresh();
    }
}
