function mapbigger(){
	var centerx
	var centery
	var newzoom
	var frametop
	var frameleft
	var tablewidth
	var tablehigh
	var picwidth
	var pichigh
	frametop=parseInt(document.all.mapframe.style.top)
	frameleft=parseInt(document.all.mapframe.style.left)
	tablehigh=parseInt(document.all.seltable.style.height)
	tablewidth=parseInt(document.all.seltable.style.width)
	tableleft=parseInt(document.all.seltable.style.left)
	tabletop=parseInt(document.all.seltable.style.top)
	picwidth=parseInt(document.all.imgmap.style.width)
	pichigh=parseInt(document.all.imgmap.style.height)
	centerx=tablewidth/2+tableleft-frameleft
	centery=tablehigh/2+tabletop-frametop
	if(tablewidth>tablehigh){
		//newzoom=tablewidth/picwidth
		newzoom=0.5
	}
	else{
		//newzoom=tablehigh/pichigh
		newzoom=0.5
	}
	if(newzoom==0){
		newzoom=0.5
	}
	chgmapsrc("rqutype=chgmapview&centerx="+centerx+"&centery="+centery+"&newzoom="+newzoom)
}

function mapsmaller(){
	var centerx
	var centery
	var frametop
	var frameleft
	frametop=parseInt(document.all.mapframe.style.top)
	frameleft=parseInt(document.all.mapframe.style.left)
	centerx=window.event.clientX-(frameleft+1)
	centery=window.event.clientY-(frametop+1)
	chgmapsrc("rqutype=chgmapview&centerx="+centerx+"&centery="+centery+"&newzoom=2")
}

function mappaner(){
	var centerx
	var centery
	var picwidth
	var pichigh
	var picleft
	var pictop
	picwidth=parseInt(document.all.imgmap.style.width)
	pichigh=parseInt(document.all.imgmap.style.height)
	pictop=parseInt(document.all.imgmap.style.top)
	picleft=parseInt(document.all.imgmap.style.left)
	//alert("wi--"+picwidth+"--he--"+pichigh+"--pictop--"+pictop+"--le--"+picleft);
	if(pictop!=0&&picleft!=0){
		centerx=picwidth/2-picleft
		centery=pichigh/2-pictop
		//alert(picwidth + ',' + picleft + ',' + centerx + ',' + centery);
		chgmapsrc("rqutype=panmap&centerx="+centerx+"&centery="+centery)
		document.all.imgmap.style.left=0
		document.all.imgmap.style.top=0
	}
}

function mapsmallpaner(){
	var centerx
	var centery
	var frametop
	var frameleft
	var boundhigh
	var maphigh
	frametop=parseInt(document.all.mapboundframe.style.top)
	frameleft=parseInt(document.all.mapboundframe.style.left)
	centerx=window.event.clientX-(frameleft+1)
	centery=window.event.clientY-(frametop+1)
	chgmapsrc("rqutype=smallpanmap&centerx="+centerx+"&centery="+centery)
}

function mapreset(){
    resetimg()
	state="reset"
	chgmapsrc("rqutype=resetmap")
}

function mapbound(){
	if(document.all.mapboundframe.style.display=="none"){
		mapbounder()
		document.all.mapboundframe.style.display=""
	}
	else{
		document.all.mapboundframe.style.display="none"
	}
	resetimg()
	state="bound"
}

function mapbounder(){
	document.all.boundmap.src=mapboundserviceurl
}

function chgmapsrc(querystring){
	var locationrul
	locatinourl="&oldx="+document.all.oldx.value+"&oldy="+document.all.oldy.value+"&oldzoom="+document.all.oldzoom.value
	document.all.imgmap.src=mapserviceurl+"?"+querystring+locatinourl
}

function setlocation(){	
	center.document.location.reload()
	zoom.document.location.reload()
}


function Find()
{
	var layernames = document.getElementById("layerid").value;
	var selectnames = document.getElementById("selectid").value;
	if(layernames==""||layernames==null){
	   alert("请选择要查询的图层");
	}else if(selectnames==""||selectnames==null){
	   alert("请填写查询的准确的名称");
	   document.getElementById("selectid").focus();
	}else{
	   chgmapsrc("rqutype=querymap&layernames="+layernames+"&selectnames="+selectnames)
	}

}
