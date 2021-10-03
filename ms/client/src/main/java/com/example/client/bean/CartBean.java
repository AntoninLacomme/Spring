package com.example.client.bean;

import java.util.List;

public class CartBean {
    private Long id;
    private List<CartItemBean> products;

    public List<CartItemBean> getProducts () {
        return products;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProducts(List<CartItemBean> products) {
        this.products = products;
    }

    public void addProduct (CartItemBean cartItem) {
        products.add(cartItem);
    }
}