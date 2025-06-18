package Model;

public class OrderInfo {
    private String orderId;
    private int productId;
    private String productName;
    private double price;
    private int productAmount;

    public OrderInfo(){}

    public void setOrderId(String orderId){this.orderId = orderId;}
    public void setProductId(int productId){this.productId = productId;}
    public void setProductName(String productName){this.productName = productName;}
    public void setPrice(double price){this.price = price;}
    public void setProductAmount(int productAmount){this.productAmount = productAmount;}

    public String getOrderId(){return this.orderId;}
    public int getProductId(){return this.productId;}
    public String getProductName(){return this.productName;}
    public double getPrice(){return this.price;}
    public int getProductAmount(){return this.productAmount;}

}
