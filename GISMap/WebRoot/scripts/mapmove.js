function box(){
	var newx
	var newy
	var framehigh
	var framewidth
	var frametop
	var frameleft
	framehigh=parseInt(document.all.mapframe.style.height)
	framewidth=parseInt(document.all.mapframe.style.width)
	frametop=parseInt(document.all.mapframe.style.top)
	frameleft=parseInt(document.all.mapframe.style.left)
	if(window.event.clientX>(frameleft+framewidth-2)){
		newx=frameleft+framewidth-2
	}
	else if(window.event.clientX<(frameleft+1)){
		newx=frameleft+1
	}
	else{
		newx=window.event.clientX
	}
	if(window.event.clientY>(frametop+framehigh-2)){
		newy=frametop+framehigh-2
	}
	else if(window.event.clientY<(frametop+1)){
		newy=frametop+1
	}
	else{
		newy=window.event.clientY
	}
	width=newx-oldx
	height=newy-oldy
	if(width<0&&height<0){
		document.all.seltable.style.left=newx
		document.all.seltable.style.top=newy
	}
	else if(width<0){
		document.all.seltable.style.left=newx
	}
	else if(height<0){
		document.all.seltable.style.top=newy
	}
	document.all.seltable.style.height=Math.abs(height)+1
	document.all.seltable.style.width=Math.abs(width)+1
}

function pan(){
	var newx
	var newy
	var picwidth
	var pichigh
	picwidth=parseInt(document.all.imgmap.style.width)
	pichigh=parseInt(document.all.imgmap.style.height)
	newx=startx+(window.event.clientX-oldx)
	//startx startx=parseInt(document.all.imgmap.style.left)
	newy=starty+(window.event.clientY-oldy)
	//alert(startx) ;
	//alert("newx"+newx);
	if (newx>picwidth) newx=picwidth
	if (newx<-picwidth) newx=-picwidth
	if (newy>pichigh) newy=pichigh
	if (newy<-pichigh) newy=-pichigh
	document.all.imgmap.style.left=newx
	document.all.imgmap.style.top=newy
}
