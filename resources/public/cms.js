$(".ui.rating").rating();
$(".ui.basic.blue.button").click(function () {
  var oldValue = $(".ui.basic.red.left.pointing.label").text().replace(/,/, "");
  var newValue = parseInt(oldValue) + 1;
  console.log(newValue);
  var secValue = newValue.toString();
  var n = secValue.slice(-3);
  var s =secValue.slice(0,-3);
  var f =s+","+n;
  $(".ui.basic.red.left.pointing.label").text(f);
console.log(f);
});

$(".ui.primary.submit.labeled.icon.button").click(
function (){
$(".ui.reply.form").submit();
}
);
