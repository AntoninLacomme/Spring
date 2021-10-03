package com.example.order.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class MyOrder {
    @Id
    @GeneratedValue
    private Long id;
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> products;
    private String dateCreation;

    public List<OrderItem> getProducts () {
        return products;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProducts(List<OrderItem> products) {
        this.products = products;
    }

    public void addProduct (OrderItem cartItem) { products.add(cartItem); }

    public String getDateCreation() { return dateCreation; }

    public void setDateCreation(String dateCreation) { this.dateCreation = dateCreation; }
    }