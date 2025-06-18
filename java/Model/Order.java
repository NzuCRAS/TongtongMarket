package Model;

public class Order {
    private String orderId;
    private int id;
    private String date;    //不确定存
    private boolean isPay;
    private double sumPrice;

    public Order(){}

    public void setOrderId(String orderId){this.orderId = orderId;}
    public void setId(int id){this.id = id;}
    public void setDate(String date){this.date = date;}
    public void setIsPay(boolean isPay){this.isPay = isPay;}
    public void setSumPrice(double sumPrice){this.sumPrice = sumPrice;}

    public String getOrderId(){return this.orderId;}
    public int getId(){return this.id;}
    public String getDate(){return this.date;}
    public boolean isPay(){return this.isPay;}
    public double getSumPrice(){return this.sumPrice;}

}
