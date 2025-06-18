package Model;

public class ShoppingCar {
    private int id;
    private int productId;
    private int productAmount;

    public ShoppingCar(){}

    public void setId(int id) {this.id = id;}
    public void setProductId(int productId) {this.productId = productId;}
    public void setProductAmount(int productAmount) {this.productAmount = productAmount;}

    public int getId() {return this.id;}
    public int getProductId() {return this.productId;}
    public int getProductAmount() {return this.productAmount;}

}
