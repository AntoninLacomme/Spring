package com.example.client.proxy;

import com.example.client.bean.ProductBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@FeignClient(name="ms-product", url="127.0.0.1:8091")
public interface MsProductProxy {
    @GetMapping("/products")
    public List<ProductBean> list ();

    @GetMapping ("/product/{id}")
    public Optional<ProductBean> get (@PathVariable Long id);

    @PostMapping ("/product/listid")
    public List<ProductBean> getProductsById (@RequestBody List<Long> listIds);

}
