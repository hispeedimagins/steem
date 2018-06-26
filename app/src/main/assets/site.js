function setusernameslink(){
    var contents = document.getElementsByClassName("mylink");
    var tags = document.getElementsByClassName("mytag");
    var alla = document.getElementsByTagName("a");
    for(var i = 0; i < contents.length; i++){
        contents[i].addEventListener("click", ContentClick, false);
    }
    for(var i = 0; i < tags.length; i++){
        tags[i].addEventListener("click", TagClick, false);
    }
    for(var i = 0; i < alla.length; i++){
        alla[i].addEventListener("click", function() {
            var hre = this.getAttribute('href');
            console.log("tag clicked" + hre);
            if(hre.search("steemer") != -1){
            } else if(hre.search("https://steemit.com") != -1 || hre.search("https://busy.org") != -1){
                event.preventDefault();
                Android.LinkClicked(hre);
            }

        },false);
    }




}

 function ContentClick(event) {
 console.log(event.defaultPrevented);
 if(event.target.href.search("steemer") != -1){
      Android.UserClicked(event.target.href.split("@")[1]);
     event.preventDefault();
  }
 if(event.target.href.search("https://steemit.com") != -1 || event.target.href.search("https://busy.org") != -1){
    event.preventDefault();
     Android.LinkClicked(event.target.href);
 }
 }

 function TagClick(event) {
 console.log("tag clicked" + event.target.href);
       if(event.target.href.search("steemer") != -1){
        Android.TagClicked(event.target.href.split("#")[1]);
        event.preventDefault(); }
       if(event.target.href.search("https://steemit.com") != -1 || event.target.href.search("https://busy.org") != -1){
             event.preventDefault();
             Android.LinkClicked(event.target.href);
             }
}

 function AllAClick(event) {
 console.log("tag clicked" + event.target);
       if(event.target.href.search("steemer") != -1){
         }
       else if(event.target.href.search("https://steemit.com") != -1 || event.target.href.search("https://busy.org") != -1){
             event.preventDefault();Android.LinkClicked(event.target.href);}}

$(document).ready(function(){

});

function UserClickedK(user) {
    Android.UserClicked(user);
}

//reg ex to use [!]\[([\w\d\s.-]*)]\(((https?:\/\/(?:[-a-zA-Z0-9\._]*[-a-zA-Z0-9])(?::\d{2,5})?(?:[\/\?#](?:[^"<>\]\[\(\)]*[^"<>\]\[\(\)])?)?))\)

function loadremark(mar) {
    console.log("got mar");
    var remarkable = new Remarkable({
        html: true,
        breaks: true,
        linkify: false,
        typographer: false,
        quotes: '“”‘’'
    });
    //console.log("going to render now" + remarkable.render(mar.body));
    var reg = new RegExp("[!]\\[([\\w\\d\\s.-]*)]\\(((https?:\\/\\/(?:[-a-zA-Z0-9\\._]*[-a-zA-Z0-9])(?::\\d{2,5})?(?:[\\/\\?#](?:[^\"<>\\]\\[\\(\\)]*[^\"<>\\]\\[\\(\\)])?)?))\\)",'g');
    var reb = remarkable.render(mar.body).replace(reg,
        '<img src="$3" />'
    );
    console.log("shit " + reb);
    document.getElementById("md").innerHTML = reb;
}