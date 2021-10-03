package com.example.cart.controllers;

import com.example.cart.domain.Cart;
import com.example.cart.domain.CartItem;
import com.example.cart.repositories.CartItemRepository;
import com.example.cart.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class CartController {

    @Autowired
    CartRepository cartRepository;
    @Autowired
    CartItemRepository cartItemRepository;

    @PostMapping ("/cart")
    public ResponseEntity<Cart> createNewCart () {
        Cart cart = cartRepository.save (new Cart ());
        System.out.println("CRéation d'un nouveau panier d'ID " + cart.getId());
        // .save ne retournera jamais null :(
        if (cart.getId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while creating a new cart");
        return new ResponseEntity<Cart>(cart, HttpStatus.CREATED);
    }

    @GetMapping(value = "/cart/{id}")
    public Optional<Cart> getCart(@PathVariable Long id) {
        Optional<Cart> cart = cartRepository.findById(id);
        if (cart.get() == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Couldn't get cart");
        return cart;
    }

    @PostMapping ("/cart/{id}")
    @Transactional
    public ResponseEntity<CartItem> addProductToCart (@PathVariable Long id, @RequestBody CartItem cartItem) {
        Cart cart = cartRepository.getById(id);
        if (cart == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't get cart");

        AtomicReference<CartItem> tmpCI = new AtomicReference<CartItem>();
        cart.getProducts().forEach((CartItem ci) -> {
            if (ci != null && (ci.getProductId() == cartItem.getProductId())) {
                tmpCI.set(ci);
                return;
            }
        });

        if (tmpCI.get() != null) {
            tmpCI.get().setQuantity(tmpCI.get().getQuantity() + cartItem.getQuantity());
            cartItemRepository.save(tmpCI.get());
            return new ResponseEntity<CartItem>(tmpCI.get(), HttpStatus.CREATED);
        }

        cart.addProduct(cartItem);
        cartRepository.save(cart);
        return new ResponseEntity<CartItem>(cartItem, HttpStatus.CREATED);
    }

    @PostMapping("/cartitem/create")
    public ResponseEntity<CartItem> createNewCartItem (@RequestBody CartItem cartItem) {
        cartItemRepository.save (cartItem);
        if (cartItem.getId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while creating a new cart");

        return new ResponseEntity<CartItem>(cartItem, HttpStatus.CREATED);
    }
}
