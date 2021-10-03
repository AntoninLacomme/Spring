package com.example.client.bean;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class OrderBean {
    private Long id;
    private List<OrderItemBean> products = new ArrayList<OrderItemBean>();
    private String dateCreation;

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<OrderItemBean> getProducts () {
        return products;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProducts(List<OrderItemBean> products) {
        this.products = products;
    }

    public void addProduct (OrderItemBean cartItem) {
        products.add(cartItem);
    }
}