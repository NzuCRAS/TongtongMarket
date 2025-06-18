package Model.secondary;

import Model.Order;
import Model.OrderInfo;

import java.util.ArrayList;

public class OrderDetail {
    private Order order;
    private ArrayList<OrderInfo> orderInfoList;

    public OrderDetail(Order order, ArrayList<OrderInfo> orderInfoList){
        this.order = order;
        this.orderInfoList = orderInfoList;
    }

    public void setOrder(Order order){this.order = order;}
    public void setOrderInfoList(ArrayList<OrderInfo> orderInfoList){this.orderInfoList = orderInfoList;}

    public Order getOrder() {return order;}
    public ArrayList<OrderInfo> getOrderInfoList() {return orderInfoList;}
}
