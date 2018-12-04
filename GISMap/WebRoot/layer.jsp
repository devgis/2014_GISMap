<%@ page language="java"  contentType="text/html;charset=UTF-8" %>
<%@ page import="com.mapinfo.mapj.*" %>
<html>
<head>
<title>图层控制</title>
<style type="text/css">
<!--
.css1 {  font-size: 12pt; color: #FFFFFF}
-->
</style>
</head>
<body bgcolor="#DFFFDF">
<form name="layer" method="post" action="map.jsp" target="mapwindow">
  <table width="490" border="1" bgcolor="#66cc66" align="center" bordercolor="#339933" class="css1" cellpadding="0" cellspacing="0">
    <tr align="center"> 
      <td width="28%" height="19" class="title"> 图层名称</td>
      <td width="23%" height="19" class="title"> 可视</td>
      <td width="24%" height="19" class="title"> 可选择</td>
      <td width="25%" height="19" class="title"> 自动标注</td>
    </tr>
    <%
MapJ mymap=(MapJ)session.getAttribute("mapj");
Layer tlayer=null;
if(mymap!=null){
	for (int i=0;i<mymap.getLayers().size();i++){
		tlayer=mymap.getLayers().elementAt(i);%>
    <tr align="center"> 
      <td width="30%"><%=tlayer.getName()%> </td>
      <td width="20%"> 
        <input type="Checkbox" name="isview<%=i%>" <%if(tlayer.isVisible())out.print("checked=true");%> value="true">
      </td>
      <td width="20%"> 
        <input type="Checkbox" name="isselect<%=i%>" <%if(tlayer.isSelectable())out.print("checked=true");%> value="true">
      </td>
      <td width="30%"> 
        <input type="Checkbox" name="islabel<%=i%>" <%if(tlayer.isAutoLabel())out.print("checked=true");%> value="true">
      </td>
    </tr>
    <%	}
}
%>
  </table>

  <table width="490" border="0" align="center" height="23">
    <tr>
      <td align="right" width="235">&nbsp;</td>
      <td width="245">&nbsp;</td>
    </tr>
    <tr> 
      <td align="right" width="235"> 
        <input type="hidden" name="flag" value="true">
        <input type="button" value="确定" onclick="closesub()" >
        <input type="button" value="应用" onclick="subform()">
      </td>
      <td width="245"> 
        <input type="reset" value="恢复"  >
        <input type="button"  value="取消" onclick="closewindow()" >
      </td>
    </tr>
  </table>
</form>
<p class="title">&nbsp;</p>
</body>
<script language="JavaScript">
function closewindow(){
	window.close()
}

function subform(){
	document.layer.submit()

}

function closesub(){
	subform()
	closewindow()
}
</script>
</html>
