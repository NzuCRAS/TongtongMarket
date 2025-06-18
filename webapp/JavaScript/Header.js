var search_icon=document.getElementById("search_icon");
var search_input=document.getElementById("search_input");
search_icon.onclick=function (){
    var name=search_input.value;
    window.location.href = `servletProductSearch?name=${name}`;
}
var baocun_name=document.getElementById("response_name").innerText;
if(baocun_name && baocun_name!== ""){
    search_input.value=baocun_name;
}