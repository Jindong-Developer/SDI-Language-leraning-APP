var listElements = 0;

$(document).on("click", ".add-line", function(event) {
	listElements++;
	var targetId = event.target.id.replace('add', '');
	var listEntry = "<div id='line"+listElements+"' class='lines'><div class='form-group'><label for='personA"+listElements+"'>Person A</label><textarea id='personA"+listElements+"' class='form-control' rows='2' name='line["+listElements+"][question]'></textarea></div><div class='form-group'><label for='gapA"+listElements+"'>L&uuml;cke A</label><input type='text' class='form-control' id='gapA"+listElements+"' placeholder='L&uuml;cke A' name='line["+listElements+"][blanksA]'></div><div class='form-group'><label for='personB"+listElements+"'>Person B</label><textarea id='personB"+listElements+"' class='form-control' rows='2' name='line["+listElements+"][answer]'></textarea></div><div class='form-group'><label for='gapB"+listElements+"'>L&uuml;cke B</label><input type='text' class='form-control' id='gapB"+listElements+"' placeholder='L&uuml;cke B' name='line["+listElements+"][blanksB]'></div><button id='add"+listElements+"' type='button' class='btn btn-default add-line' aria-label='Left Align'><span class='glyphicon glyphicon-plus' aria-hidden='true'></span></button><button id='rem"+listElements+"' type='button' class='btn btn-default rem-line' aria-label='Left Align'><span class='glyphicon glyphicon-minus' aria-hidden='true'></span></button></div>";
  	$( "#line" + targetId ).after(listEntry);
});

$(document).on("click", ".rem-line", function(event) {
	var targetId = event.target.id.replace('rem', '');
	var r = confirm("Wollen Sie diese Zeile wirklich l\u00f6schen?");
	if (r == true) {
	    deleteLine(targetId);
	}});

function deleteLine(id){
	$( "#line" + id ).remove();
}