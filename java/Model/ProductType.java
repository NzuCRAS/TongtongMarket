package Model;

public class ProductType {
    private int sortId;
    private String sortName;

    public ProductType(){}

    public void setSortId(int sortId){this.sortId=sortId;}
    public void setSortName(String sortName){this.sortName=sortName;}

    public int getSortId(String sortName){return this.sortId;}
    public String getSortName(){return this.sortName;}

}
