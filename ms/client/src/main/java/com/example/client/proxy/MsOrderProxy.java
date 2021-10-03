package com.example.client.proxy;

import com.example.client.bean.CartBean;
import com.example.client.bean.OrderBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@FeignClient(name="ms-order", url="127.0.0.1:8090")
public interface MsOrderProxy {
    @PostMapping("/orders")
    public List<OrderBean> getOrders ();

    @GetMapping("/order/{id}")
    public Optional<OrderBean> getOrder (@PathVariable Long id);

    @PostMapping("/order/save")
    public OrderBean saveOrder (@RequestBody OrderBean order);
    //public OrderBean saveOrder (CartBean order);
}
