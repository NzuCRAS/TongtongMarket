// 筛选框变色
var types=document.getElementsByClassName("sort_name");
var flags=document.getElementsByClassName("flag");
console.log(types);
console.log(flags);
var true_type=document.getElementById("response_type").innerText;
var true_flag=document.getElementById("response_flag").innerText;
console.log(true_type);
console.log(true_flag);

for(var i=0;i<types.length;i++){
    if(types[i].innerText===true_type){
        types[i].style.color="pink";
        types[i].querySelector('a').style.color = "pink";
        console.log("yes");
        break;
    }
    console.log("no");
}
true_flag=parseInt(true_flag);
flags[true_flag-1].style.color="pink";
flags[true_flag - 1].querySelector('a').style.color = "pink";