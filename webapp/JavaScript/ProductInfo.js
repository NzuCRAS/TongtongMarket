// 加减数量操作
var true_amount=document.getElementById("true_amount");   //修改数量
var jia=document.getElementById("jia");   //加号
var jian=document.getElementById("jian");  //减号
var stock_num=document.getElementById("stock_amount");  //库存
jia.addEventListener("click",function(){
    var num = parseInt(true_amount.innerText);
    var stock = parseInt(stock_amount.innerText);
    if(num>=stock){}
    else{
        num++;
        true_amount.innerText=num;
    }
});
jian.onclick=function(){
    var num=true_amount.innerText;
    if(num==1){}
    else{
        num--;
        console.log(num);
        true_amount.innerText=num;
    }
};

//传商品编号和购买数量
var product_id=document.getElementById("product_id");
var buy=document.getElementById("buy");
buy.onclick=function (){
    var productId=product_id.innerText;
    var num=document.getElementById("true_amount").innerText;
    window.location.href = `servletAddProduct?productId=${productId}&num=${num}`;
}