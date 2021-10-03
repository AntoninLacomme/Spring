package com.example.cart.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Cart {
    @Id
    @GeneratedValue
    private Long id;
    @OneToMany(cascade = CascadeType.ALL)
    private List<CartItem> products;

    public List<CartItem> getProducts () {
        return products;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProducts(List<CartItem> products) {
        this.products = products;
    }

    public void addProduct (CartItem cartItem) {
        products.add(cartItem);
    }
}