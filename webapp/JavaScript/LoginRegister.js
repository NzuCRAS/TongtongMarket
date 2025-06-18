// alert("1111");
var login_name=document.getElementById("login_name");
var register_name=document.getElementById("register_name");
login_name.onclick=function(){
    login_name.style.color="#66adca";
    register_name.style.color="#1c1b1e";
}
register_name.onclick=function(){
    login_name.style.color="#1c1b1e";
    register_name.style.color="#66adca";
}



