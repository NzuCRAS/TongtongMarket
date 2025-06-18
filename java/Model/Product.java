package Model;

public class Product {
    private int productId;
    private String productName;
    private double price;
    private String introduction;
    private String picture;
    private int stock;
    private int sortId;
    private int sales;

    public Product() {}

    public void setId(int productId) {this.productId = productId;}
    public void setProductName(String productName) {this.productName = productName;}
    public void setPrice(double price) {this.price = price;}
    public void setIntroduction(String introduction) {this.introduction = introduction;}
    public void setPicture(String picture) {this.picture = picture;}
    public void setStock(int stock) {this.stock = stock;}
    public void setSortId(int sortId) {this.sortId = sortId;}
    public void setSales(int sales) {this.sales = sales;}

    public int getProductId() {return this.productId;}
    public String getProductName() {return this.productName;}
    public double getPrice() {return this.price;}
    public String getIntroduction() {return this.introduction;}
    public String getPicture() {return this.picture;}
    public int getStock() {return this.stock;}
    public int getSortId() {return this.sortId;}
    public int getSales() {return this.sales;}

}
