package com.example.client.controllers;

import com.example.client.bean.*;
import com.example.client.proxy.MsCartProxy;
import com.example.client.proxy.MsOrderProxy;
import com.example.client.proxy.MsProductProxy;
import com.example.client.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Controller
public class ClientController {
    public class CastCartItemBean {
        public String nomProduit;
        public int quantity;
        public double price;

        public CastCartItemBean (String nom, int qte, double price) {
            this.nomProduit = nom;
            this.quantity = qte;
            this.price = price;
        }
    }

    public class CastOrderItemBean {
        public String date;
        public List<CastCartItemBean> products;
        public double price = 0;
        public String nameCommande;

        public CastOrderItemBean (String date, List<CastCartItemBean> products, String name) {
            this.date = date;
            this.products = products;
            this.nameCommande = name;
            this.products.forEach((CastCartItemBean p) -> {
                price += p.price * p.quantity;
            });
        }
    }

    private Long idCurrentCart = null;

    @Autowired
    private MsProductProxy msProductProxy;
    @Autowired
    private MsCartProxy msCartProxy;
    @Autowired
    private MsOrderProxy msOrderProxy;

    @RequestMapping (value = "/", method = RequestMethod.GET)
    public String index (Model model) {
        if (idCurrentCart == null) {
            idCurrentCart = msCartProxy.createNewCart().getBody().getId();
        }
        List<ProductBean> products = msProductProxy.list();
        model.addAttribute ("products", products);
        model.addAttribute("countCart", msCartProxy.getCart(idCurrentCart).get().getProducts().size());
        return "index";
    }

    @RequestMapping (value="/product/{id}", method = RequestMethod.GET)
    public String productDetail (Model model, @PathVariable Long id) {
        if (idCurrentCart == null) {
            idCurrentCart = msCartProxy.createNewCart().getBody().getId();
        }

        ProductBean product = msProductProxy.get(id).get();
        model.addAttribute("product", product);
        model.addAttribute("countCart", msCartProxy.getCart(idCurrentCart).get().getProducts().size());
        return "productDetail";
    }

    @RequestMapping (value="/cart", method = RequestMethod.GET)
    public String cartDetail (Model model) {
        if (idCurrentCart == null) {
            idCurrentCart = msCartProxy.createNewCart().getBody().getId();
        }

        CartBean cartbean = msCartProxy.getCart(this.idCurrentCart).get();

        // on stocke dans une liste la totalité des ID des produits du panier
        List<Long> listIdProduct = new ArrayList<Long>();
        cartbean.getProducts().forEach((CartItemBean cartItemBean) -> {
            System.out.println(" - " + cartItemBean.getId());
            listIdProduct.add(cartItemBean.getProductId());
        });
        // on envoi cette liste au micro service produit pour obtenir les données correspondantes
        List<ProductBean> products = msProductProxy.getProductsById(listIdProduct);
        List<CastCartItemBean> listItems = new ArrayList<CastCartItemBean>();

        AtomicReference<Double> costTtl = new AtomicReference<>((double) 0);
        products.forEach((ProductBean productBean) -> {
            AtomicInteger qte = new AtomicInteger();
            cartbean.getProducts().forEach((CartItemBean cib) -> {
                if (cib.getProductId() == productBean.getId()) {
                    qte.set(cib.getQuantity());
                }
            });
            costTtl.updateAndGet(v -> new Double((double) (v + qte.get() * productBean.getPrice())));
            listItems.add(new CastCartItemBean(productBean.getName(), qte.get(), qte.get() * productBean.getPrice()));
        });

        model.addAttribute("products", listItems);
        model.addAttribute("costTotal", costTtl);
        model.addAttribute("countCart", msCartProxy.getCart(idCurrentCart).get().getProducts().size());
        return "cart";
    }

    @RequestMapping (value="/cart/addProduct", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public RedirectView addProductToCart (CartItemBean cartItemBean) {
        msCartProxy.addProductToCart(this.idCurrentCart, msCartProxy.createNewCartItem(cartItemBean).getBody());
        return new RedirectView("/cart");
    }


    @RequestMapping (value="/order/create", method = RequestMethod.POST)
    public RedirectView createOrder () {
        CartBean cartbean = msCartProxy.getCart(this.idCurrentCart).get();

        msOrderProxy.saveOrder (OrderService.convertCartToOrder(cartbean, new Date()));

        // cast du panier en commande, hop, on perd le panier courant
        this.idCurrentCart = null;
        return new RedirectView ("/orders");
    }

    @RequestMapping (value="/orders", method = RequestMethod.GET)
    public String showOrders (Model model) {
        List<OrderBean> orders = msOrderProxy.getOrders();

        // on commence par récupérer les produits à partir de leurs IDs
        LinkedHashSet<Long> idProducts = new LinkedHashSet<>();
        orders.forEach((OrderBean order) -> {
            order.getProducts().forEach((OrderItemBean oitem) -> {
                idProducts.add(oitem.getProductId());
            });
        });
        // on cast la LinkedHashSet en List
        List<Long> lIdProds = new ArrayList<>();
        idProducts.forEach((Long id) -> {
            lIdProds.add(id);
        });
        // et on va chercher la data
        List<ProductBean> allProducts = msProductProxy.getProductsById(lIdProds);

        // enfin on cast toute notre data dans un objet permettant la visualisation
        List<CastOrderItemBean> items = new ArrayList<>();
        AtomicInteger nbCommande = new AtomicInteger();
        orders.forEach((OrderBean order) -> {
            List<CastCartItemBean> cartitems = new ArrayList<>();
            order.getProducts().forEach((OrderItemBean oitem) -> {
                allProducts.forEach((ProductBean product) -> {
                    if (product.getId() == oitem.getProductId()) {
                        cartitems.add(new CastCartItemBean(product.getName(), oitem.getQuantity(), product.getPrice()));
                        return;
                    }
                });
            });
            nbCommande.getAndIncrement();
            items.add(new CastOrderItemBean(order.getDateCreation(), cartitems, "Commande " + nbCommande.get()));
        });

        idCurrentCart = msCartProxy.createNewCart().getBody().getId();
        model.addAttribute("countCart", msCartProxy.getCart(idCurrentCart).get().getProducts().size());
        model.addAttribute("orders", items);
        return "orders";
    }

}
