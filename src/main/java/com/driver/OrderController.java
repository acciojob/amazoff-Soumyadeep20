package com.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("orders")
public class OrderController {

OrderRepo or = new OrderRepo();

    @PostMapping("/add-order")
    public ResponseEntity<String> addOrder(@RequestBody Order order){
        or.addOrder(order);
        return new ResponseEntity<>("New order added successfully", HttpStatus.CREATED);
    }

    @PostMapping("/add-partner/{partnerId}")
    public ResponseEntity<String> addPartner(@PathVariable String partnerId){
        or.addPartner(partnerId);

        return new ResponseEntity<>("New delivery partner added successfully", HttpStatus.CREATED);
    }

    @PutMapping("/add-order-partner-pair")
    public ResponseEntity<String> addOrderPartnerPair(@RequestParam String orderId, @RequestParam String partnerId){
        or.addOrderPartnerPair(orderId,partnerId);
        //This is basically assigning that order to that partnerId
        return new ResponseEntity<>("New order-partner pair added successfully", HttpStatus.CREATED);
    }

    @GetMapping("/get-order-by-id/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId){

        Order order= or.getOrderById(orderId);
        //order should be returned with an orderId.

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/get-partner-by-id/{partnerId}")
    public ResponseEntity<DeliveryPartner> getPartnerById(@PathVariable String partnerId){

        DeliveryPartner deliveryPartner = or.getPartnerById(partnerId);

        //deliveryPartner should contain the value given by partnerId

        return new ResponseEntity<>(deliveryPartner, HttpStatus.CREATED);
    }

    @GetMapping("/get-order-count-by-partner-id/{partnerId}")
    public ResponseEntity<Integer> getOrderCountByPartnerId(@PathVariable String partnerId){

        Integer orderCount = or.getOrderCountByPartnerId(partnerId);

        //orderCount should denote the orders given by a partner-id

        return new ResponseEntity<>(orderCount, HttpStatus.CREATED);
    }

    @GetMapping("/get-orders-by-partner-id/{partnerId}")
    public ResponseEntity<List<String>> getOrdersByPartnerId(@PathVariable String partnerId){
        List<String> orders = or.getOrdersByPartnerId(partnerId);

        //orders should contain a list of orders by PartnerId

        return new ResponseEntity<>(orders, HttpStatus.CREATED);
    }

    @GetMapping("/get-all-orders")
    public ResponseEntity<List<String>> getAllOrders(){
        List<String> orders = or.getAllOrders();

        //Get all orders
        return new ResponseEntity<>(orders, HttpStatus.CREATED);
    }

    @GetMapping("/get-count-of-unassigned-orders")
    public ResponseEntity<Integer> getCountOfUnassignedOrders(){
        Integer countOfOrders = or.getCountOfUnassignedOrders();

        //Count of orders that have not been assigned to any DeliveryPartner

        return new ResponseEntity<>(countOfOrders, HttpStatus.CREATED);
    }

    @GetMapping("/get-count-of-orders-left-after-given-time/{partnerId}")
    public ResponseEntity<Integer> getOrdersLeftAfterGivenTimeByPartnerId(@PathVariable String time, @PathVariable String partnerId){


        Integer countOfOrders = or.getOrdersLeftAfterGivenTimeByPartnerId(partnerId,time);

        //countOfOrders that are left after a particular time of a DeliveryPartner

        return new ResponseEntity<>(countOfOrders, HttpStatus.CREATED);
    }

    @GetMapping("/get-last-delivery-time/{partnerId}")
    public ResponseEntity<String> getLastDeliveryTimeByPartnerId(@PathVariable String partnerId){
        String time = or.getLastDeliveryTimeByPartnerId(partnerId);

        //Return the time when that partnerId will deliver his last delivery order.

        return new ResponseEntity<>(time, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-partner-by-id/{partnerId}")
    public ResponseEntity<String> deletePartnerById(@PathVariable String partnerId){
        or.deletePartnerById(partnerId);
        //Delete the partnerId
        //And push all his assigned orders to unassigned orders.

        return new ResponseEntity<>(partnerId + " removed successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-order-by-id/{orderId}")
    public ResponseEntity<String> deleteOrderById(@PathVariable String orderId){
            or.deleteOrderById(orderId);
        //Delete an order and also
        // remove it from the assigned order of that partnerId

        return new ResponseEntity<>(orderId + " removed successfully", HttpStatus.CREATED);
    }
}
class OrderRepo {
    HashMap<String,Order> or=new HashMap<>();
    HashMap<String,DeliveryPartner> dr=new HashMap<>();
    HashMap<String, List<String>> odr=new HashMap<>();
    List<String> ol=new ArrayList<>();
    List<String> oul=new ArrayList<>();
    public void addOrder(Order order) {
        String s=order.getId();
        or.put(s,order);
        ol.add(s);
        oul.add(s);
    }

    public void addPartner(String partnerId) {
        DeliveryPartner x= new DeliveryPartner(partnerId);
        dr.put(partnerId,x);

    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        List<String> a;
        if(odr.containsKey((partnerId)))
            a= odr.get(partnerId);
        else
            a=new ArrayList<>();
        a.add(orderId);
        odr.put(partnerId,a);
        DeliveryPartner x=dr.get(partnerId);
        int n=x.getNumberOfOrders()+1;
        x.setNumberOfOrders(n);
        dr.put(partnerId,x);
        oul.remove(orderId);

    }

    public Order getOrderById(String orderId) {
        if(!or.containsKey(orderId))
            return null;
        return or.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        if(!dr.containsKey(partnerId))
            return null;
        return dr.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        return dr.get(partnerId).getNumberOfOrders();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        if(!odr.containsKey(partnerId))
            return null;
        return odr.get(partnerId);
    }

    public List<String> getAllOrders() {
        return ol;
    }

    public Integer getCountOfUnassignedOrders() {
        return oul.size();
    }

    public void deleteOrderById(String orderId) {
        or.remove(orderId);
        ol.remove(orderId);
        if(oul.contains(orderId))
        {
            oul.remove(orderId);
        }
        else {
            for(String key:odr.keySet())
            {
                List<String> a= odr.get(key);
                if(a.contains(orderId))
                {
                    a.remove(orderId);
                    odr.put(key,a);
                    DeliveryPartner x=dr.get(key);
                    int n=x.getNumberOfOrders()-1;
                    x.setNumberOfOrders(n);
                    dr.put(key,x);

                }
            }
        }
    }

    public void deletePartnerById(String partnerId) {
        if(dr.containsKey(partnerId))
            dr.remove(partnerId);
        if(odr.containsKey(partnerId)) {
            odr.remove(partnerId);
        }
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String partnerId, String time) {
        int x=(Integer.parseInt(time.substring(0,2))*60)+Integer.parseInt(time.substring(3,5));
        List<String> a=odr.get(partnerId);
        int c=0;
        for(int i=0;i<a.size();i++)
        {
            String s=a.get(i);
            Order o=or.get(s);
            if(o.getDeliveryTime()<x)
                c++;
        }
        return c;

    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        String  ans=null;
        if(odr.containsKey(partnerId)){
            List<String> a=odr.get(partnerId);
            int c=Integer.MAX_VALUE;
            for(int i=0;i<a.size();i++)
            {
                String s=a.get(i);
                Order o=or.get(s);
                c=Math.min(o.getDeliveryTime(),c);

            }
            int x=  c/60;
            if(x<10)
            {
                ans +="0" + Integer.toString(x);
            }
            else
                ans += Integer.toString(x);
            ans +=":";
            x =c%60;
            if(x<10)
            {
                ans +="0" + Integer.toString(x);
            }
            else
                ans += Integer.toString(x);
        }
        return   ans;

    }
}

