package com.acework.shabaretailer.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.acework.shabaretailer.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                if (isSimilar(itemInCart, itemToSet)) {
                    newCart.add(itemInCart);
                } else {
                    newCart.add(itemToSet);
                }
            }
        }
        cart.setValue(newCart);
        refresh();
    }

    private boolean isSimilar(Item item1, Item item2) {
        if (!item1.getName().equals(item2.getName())) return false;
        if (!(item1.getPrice() == item2.getPrice())) return false;
        if (!item1.getShape().equals(item2.getShape())) return false;
        if (!item1.getDimensions().equals(item2.getDimensions())) return false;
        if (!item1.getWeaving().equals(item2.getWeaving())) return false;
        if (!item1.getLeather().equals(item2.getLeather())) return false;
        if (!item1.getInsert().equals(item2.getInsert())) return false;
        if (!item1.getStrapLength().equals(item2.getStrapLength())) return false;
        return item1.getWeight() == item2.getWeight();
    }

    private Item getItemFromCart(String sku) {
        for (Item itemInCart : cart.getValue()) {
            if (itemInCart.getSku().equals(sku)) return itemInCart;
        }
        return null;
    }

    public void removeItemFromCart(String sku) {
        List<Item> newItemsInCart = new ArrayList<>();
        for (Item itemInCart : cart.getValue()) {
            if (!Objects.equals(itemInCart.getSku(), sku)) newItemsInCart.add(itemInCart);
        }
        cart.setValue(newItemsInCart);
        refresh();
    }

    public void refresh() {
        cart.setValue(cart.getValue());
    }

    public void resetCart(){
        for (Item item:cart.getValue()){
            item.setQuantity(0);
        }
        refresh();
    }
}
