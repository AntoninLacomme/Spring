package com.example.client.service;

import com.example.client.bean.CartBean;
import com.example.client.bean.CartItemBean;
import com.example.client.bean.OrderBean;
import com.example.client.bean.OrderItemBean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderService {
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    static public OrderBean convertCartToOrder (CartBean cartBean, Date date) {
        OrderBean od = new OrderBean();
        cartBean.getProducts().forEach((CartItemBean cib) -> {
            od.addProduct(OrderService.convertCartItemToOrderItem(cib));
        });
        od.setDateCreation(formatter.format(date));
        od.setId(cartBean.getId());
        return od;
    }

    static public OrderItemBean convertCartItemToOrderItem (CartItemBean cartItemBean) {
        OrderItemBean oib = new OrderItemBean();
        oib.setId(cartItemBean.getId());
        oib.setProductId(cartItemBean.getProductId());
        oib.setQuantity(cartItemBean.getQuantity());
        return oib;
    }
}
