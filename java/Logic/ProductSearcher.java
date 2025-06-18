package Logic;

import Model.Product;
import Persistence.PersProduct;

import java.util.ArrayList;

public class ProductSearcher {
    public ArrayList<Product> searchProduct(int flag, String name, String type, int page) {
        PersProduct persProduct = new PersProduct();

        // 判断通过什么要求进行检索
        ArrayList<Product> productList = new ArrayList<>();
        if (name != null && !name.equals("noName")) {
            // 通过名字检索
            productList = persProduct.getProductsFromProductname(name, page, flag);
        } else if (type != null && !type.equals("全部")) {
            // 通过类型检索
            productList = persProduct.getProductsFromSortName(type, page, flag);
        } else {
            // 无条件检索
            productList = persProduct.getProductsAll(page, flag);
        }
        return productList;
    }
}
