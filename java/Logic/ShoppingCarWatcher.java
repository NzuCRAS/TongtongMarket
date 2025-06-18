package Logic;

import Model.Product;
import Model.ShoppingCar;
import Model.secondary.ShoppingCarProduct;
import Persistence.PersProduct;
import Persistence.PersShoppingCar;

import java.util.ArrayList;

public class ShoppingCarWatcher {

    // 给出userId,返回商品数量 + 商品具体信息
    public ArrayList<ShoppingCarProduct> showShoppingCar(int userId) {
        // 通过userId查找购物车信息,返回所有在该用户购物车里的商品
        PersShoppingCar persShoppingCar = new PersShoppingCar();
        PersProduct persProduct = new PersProduct();
        ArrayList<ShoppingCarProduct> shoppingCarProductList = new ArrayList<>();
        ArrayList<ShoppingCar> shoppingCars = persShoppingCar.showShoppingCar(userId);

        // 得到所有商品id以及数量,通过商品id查询具体商品信息,并将所有商品信息包装成shoppingCarProduct存进ArrayList
        for (ShoppingCar shoppingCar : shoppingCars) {
            Product product= persProduct.getProduct(shoppingCar.getProductId());
            shoppingCarProductList.add(new ShoppingCarProduct(shoppingCar.getProductAmount(), product));
        }

        return shoppingCarProductList;
    }


}
