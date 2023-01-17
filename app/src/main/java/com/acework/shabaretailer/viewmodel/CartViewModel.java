package com.acework.shabaretailer.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.acework.shabaretailer.model.Cart;
import com.acework.shabaretailer.model.Item;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class CartViewModel extends AndroidViewModel {
    private final MutableLiveData<Cart> cart;

    public CartViewModel(@NonNull Application application) {
        super(application);
        cart = new MutableLiveData<>(new Cart());
    }

    public static String getOrderTypeAsString(int orderType) {
        switch (orderType) {
            case 2:
                return "Commission";
            case 1:
                return "Consignment";
            case 0:
                return "Wholesale";
            default:
                return "Please set an order type";
        }
    }

    public MutableLiveData<Cart> getCart() {
        return cart;
    }

    public int getOrderType() {
        return cart.getValue().getOrderType();
    }

    public void setOrderType(int newOrderType) {
        cart.getValue().setOrderType(newOrderType);
        refresh();
    }

    public Item getItemFromCart(String sku, String insertColor) {
        for (Item itemInCart : cart.getValue().getItems()) {
            if (itemInCart.getSku().equals(sku) && itemInCart.getInsertColour().equals(insertColor))
                return itemInCart;
        }
        return null;
    }

    public void removeItemFromCart(Item itemToRemove) {
        cart.getValue().getItems().remove(itemToRemove);
        refresh();
    }

    public void refresh() {
        List<Item> itemsToRemove = new ArrayList<>();
        for (Item i : cart.getValue().getItems()) {
            if (i.getQuantity() < 1) {
                itemsToRemove.add(i);
            }
        }
        for (Item i : itemsToRemove) {
            cart.getValue().getItems().remove(i);
        }
        cart.setValue(cart.getValue());
    }

    public void resetCart() {
        for (Item item : cart.getValue().getItems()) {
            item.setQuantity(0);
        }
        refresh();
    }

    public void setItem(Item item) {
        Item itemInCart = getItemFromCart(item.getSku(), item.getInsertColour());
        if (itemInCart == null) {
            cart.getValue().getItems().add(item);
        } else {
            if (item.getQuantity() == 0) {
                cart.getValue().getItems().remove(itemInCart);
            } else {
                itemInCart.setQuantity(item.getQuantity());
            }
        }
    }

    public List<Item> getItemsInCart() {
        return cart.getValue().getItems();
    }
}
