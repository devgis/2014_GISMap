var state=""
var eventstate=""
var oldx=0
var oldy=0
var startx=0
var starty=0
var mapserviceurl="servlet/MapServlet"
var mapboundserviceurl="servlet/MapServlet?rqutype=boundmap"
document.onmousedown=mapmousedown
document.onmousemove=mapmousemove
document.onmouseup=mapmouseup
document.all.imgmap.onreadystatechange=downloadstate
document.all.boundmap.onreadystatechange=bounddownloadstate
document.all.seltable.onmousemove=tablemove
window.name="mapwindow"
document.all.imgmap.src=mapserviceurl+"?rqutype=initmap"
document.all.center.src=mapserviceurl+"?rqutype=centerpoint"
document.all.zoom.src=mapserviceurl+"?rqutype=zoom"