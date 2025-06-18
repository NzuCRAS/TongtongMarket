package Model.secondary;

import Model.Product;

public class ShoppingCarProduct {
    private int number;
    private Product product;

    public ShoppingCarProduct(int number, Product product) {
        this.number = number;
        this.product = product;
    }

    public int getNumber() {return number;}
    public Product getProduct() {return product;}
}
