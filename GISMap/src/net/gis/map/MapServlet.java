package net.gis.map;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mapinfo.dp.Attribute;
import com.mapinfo.dp.Feature;
import com.mapinfo.dp.FeatureSet;
import com.mapinfo.dp.Geometry;
import com.mapinfo.dp.PrimaryKey;
import com.mapinfo.dp.TableInfo;
import com.mapinfo.dp.annotation.AnnotationDataProviderHelper;
import com.mapinfo.dp.annotation.AnnotationTableDescHelper;
import com.mapinfo.dp.util.LocalDataProviderRef;
import com.mapinfo.dp.util.RewindableFeatureSet;
import com.mapinfo.graphics.Rendition;
import com.mapinfo.graphics.RenditionImpl;
import com.mapinfo.mapj.FeatureFactory;
import com.mapinfo.mapj.Layer;
import com.mapinfo.mapj.MapJ;
import com.mapinfo.mapj.Selection;
import com.mapinfo.mapxtreme.client.MapXtremeImageRenderer;
import com.mapinfo.theme.SelectionTheme;
import com.mapinfo.util.DoublePoint;
import com.mapinfo.util.DoubleRect;
import com.mapinfo.xmlprot.mxtj.ImageRequestComposer;

public class MapServlet extends HttpServlet {

	// 包含地图文件的路径
	private String m_mapPath = "D:/schoolmap";

	// 地图定义文件的完整路径
	private String m_fileToLoad = "D:/schoolmap/schoolmap.gst";

	private boolean errflag = false;

	private String errmessage = null;

	// mapxtremeservlet地图服务器url

	private String mapxtremeurl = "http://localhost:8080//GisMap/mapxtreme";

	private String imgtype = "png";

	private int imgsizex = 1200;

	private int imgsizey = 800;

	private int smallimgsizex = 600;

	private int smallimgsizey = 400;

	private Color imgbgcolor = Color.white;

	static DoublePoint resetpoint = null;

	static double resetzoom = 0.0D;

	public void init(ServletConfig config) throws ServletException {
		/**super.init(config);
		String strParam = "";
		URL url = this.getClass().getResource("/../..");
		strParam = getInitParameter("mapPath");
		if (strParam != null) {
			//也可以直接在web.xml 配置绝对路径 那么下面这一句就可以不要了
			strParam = url.getPath().substring(1).replaceAll("%20", " ")+strParam;
			
			m_mapPath = strParam;
		}
		strParam = getInitParameter("fileToLoad");
		if (strParam != null) {
		//也可以直接在web.xml 配置绝对路径 那么下面这一句就可以不要了
			strParam = url.getPath().substring(1).replaceAll("%20", " ")+strParam;
			m_fileToLoad = strParam;
		}
		
		strParam = getInitParameter("mapxtremeURL");
		if (strParam != null && strParam.length() > 0) {
			mapxtremeurl = strParam;
		}
		
		*/
        
	}

	/**
	 * *@加载地图
	 */
	public MapJ initMapJ() throws Exception {
		MapJ myMap = new MapJ();

		try {
			//加载.gst 格式的地图文件
			if (m_fileToLoad.endsWith(".gst")) {
				myMap.loadGeoset(m_fileToLoad, m_mapPath, null);
			} else { //加载.mdf 格式的地图文件
				myMap.loadMapDefinition(m_fileToLoad);
			}
		} catch (Exception e) {
			System.out.println("Can't load geoset: " + m_fileToLoad + "\n");
			System.out.println(e.getMessage());
			throw e;
		}
		return myMap;
	}

	/**
	 * @初始化地图
	 */
	private MapJ initmap(HttpServletRequest request) {
		MapJ mymap = null;
		mymap = (MapJ) request.getSession().getAttribute("mapj");
		if (mymap == null) {
			try {
				mymap = initMapJ();
				// 加载地图
				if ((request.getParameter("oldx") != null)
						&& (request.getParameter("oldy") != null)) {
					Double oldx = new Double(request.getParameter("oldx"));
					Double oldy = new Double(request.getParameter("oldy"));
					DoublePoint mappoint = new DoublePoint(oldx.doubleValue(),
							oldy.doubleValue());
					Double oldzoom = new Double(request.getParameter("oldzoom"));
					mymap.setCenter(mappoint);
					mymap.setZoom(oldzoom.doubleValue());
				}
				request.getSession().setAttribute("mapj", mymap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mymap;
	}

	/**
	 * @鹰眼地图初始化
	 */
	private MapJ initboundmap(HttpServletRequest request) {
		MapJ boundmap = null;
		boundmap = (MapJ) request.getSession().getAttribute("boundmap");
		if (boundmap == null) {
			try {
				boundmap = initMapJ();
				// 加载地图
				/**
				 * @添加图层的步骤
				 * @1--创建TableDescHelper
				 * @2--创建DataProviderHelper
				 * @3--创建DataProviderRef
				 * @4--创建layers.insert
				 */
				AnnotationTableDescHelper antable = new AnnotationTableDescHelper(
						"anlayer");
				AnnotationDataProviderHelper andata = new AnnotationDataProviderHelper();
				LocalDataProviderRef andataref = new LocalDataProviderRef(
						andata);
				boundmap.getLayers().insert(andataref, antable, 0, "anlayer");
				request.getSession().setAttribute("boundmap", boundmap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return boundmap;
	}

	/**
	 * @应该与缩放有关
	 */
	private void chgmapview(MapJ mymap, HttpServletRequest request) {
		try {
			double oldzoom = mymap.getZoom();
			// 原来的地图范围
			Double newzoomopr = new Double(request.getParameter("newzoom"));
			double newzoom = oldzoom * newzoomopr.doubleValue();
			// 新的地图范围
			Double centerx = new Double(request.getParameter("centerx"));
			Double centery = new Double(request.getParameter("centery"));
			// 取得鼠标坐标
			DoublePoint screenpoint = new DoublePoint(centerx.doubleValue(),
					centery.doubleValue());
			DoublePoint mappoint = mymap.transformScreenToNumeric(screenpoint);
			mymap.setCenter(mappoint);
			mymap.setZoom(newzoom);
			// 设定点和地图的范围
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @地图移动方法
	 */
	private void panmap(MapJ mymap, HttpServletRequest request) {
		try {
			Double centerx = new Double(request.getParameter("centerx"));
			// 从前台取得鼠标坐标 X ；
			Double centery = new Double(request.getParameter("centery"));
			// 从前台取得鼠标坐标 Y ；
			DoublePoint screenpoint = new DoublePoint(centerx.doubleValue(),
					centery.doubleValue());
			// 创建一个新的点
			DoublePoint mappoint = mymap.transformScreenToNumeric(screenpoint);
			// 转换坐标
			mymap.setCenter(mappoint);
			// 设定中心
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @地图还原方法
	 */
	private void resetmap(MapJ mymap, HttpServletRequest request) {
		try {
			mymap.setZoom(resetzoom);
			// 设定地图范围为最初的范围
			mymap.setCenter(resetpoint);
			// 设定地图中心为最初的中心点
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @地图渲染
	 */
	private void responseimg(MapJ mymap, HttpServletResponse response) {
		ServletOutputStream sout = null;
		mymap.setDeviceBounds(new DoubleRect(0.0D, 0.0D, this.imgsizex,
				this.imgsizey));
		// 设定地图的大小
		response.setContentType("image/jpeg");
		try {
			sout = response.getOutputStream();
			ImageRequestComposer irc = ImageRequestComposer.create(mymap,
					65535, Color.white, "image/jpeg");
			// 创建输出地图的属性---显示的像素，背景颜色，地图格式
			MapXtremeImageRenderer renderer = new MapXtremeImageRenderer(
					this.mapxtremeurl);
			// 加载mapxtreme的servlet
			renderer.render(irc);
			// 渲染
			renderer.toStream(sout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (sout != null)
				sout.close();
		} catch (Exception localException1) {
		}
	}

	private void responsetext(MapJ mymap, HttpServletResponse response,
			String flag) {
		try {
			PrintWriter out = response.getWriter();
			response.setContentType("text/html; charset=UTF-8");
			if (flag.equals("centerpoint")) {
				DoublePoint centerpoint = mymap.getCenter();
				out
						.print(String
								.valueOf(String
										.valueOf(new StringBuffer(
												"<body onload=\"setxy()\"><input name=centerx type=hidden value=")
												.append(centerpoint.x)
												.append(">")
												.append(
														"<input name=centery type=hidden value=")
												.append(centerpoint.y)
												.append(">")
												.append("</body>")
												.append(
														"<script language=\"JavaScript\">")
												.append("function setxy(){")
												.append(
														"parent.document.all.oldx.value=document.all.centerx.value;")
												.append(
														"parent.document.all.oldy.value=document.all.centery.value")
												.append("}")
												.append("</script>"))));
			} else {
				out
						.print(String
								.valueOf(String
										.valueOf(new StringBuffer(
												"<body onload=\"setzoom()\"><input name=zoom type=hidden value=")
												.append(mymap.getZoom())
												.append(">")
												.append("</body>")
												.append(
														"<script language=\"JavaScript\">")
												.append("function setzoom(){")
												.append(
														"parent.document.all.oldzoom.value=document.all.zoom.value;")
												.append("}")
												.append("</script>"))));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @鹰眼功能
	 */

	private void responsebound(MapJ mymap, MapJ boundmap,
			HttpServletResponse response) {
		ServletOutputStream sout = null;
		try {
			Layer anlayer = boundmap.getLayers().elementAt(0);
			// 取得一个图层 （鹰眼地图所有层的第一层）
			FeatureFactory ff = boundmap.getFeatureFactory();
			// 取得一个图元工厂类，可以用来创建图元---点、线、面
			PrimaryKey pk = new PrimaryKey(new Attribute(101));
			// 创建一个主键 具体是什么意思呢？？？
			Rendition rend = RenditionImpl.getDefaultRendition();
			// 创建一个样式的对象
			rend.setValue(Rendition.STROKE, Color.red);
			// 设置线的颜色为红色
			rend.setValue(Rendition.STROKE_WIDTH, 2);
			// 设置线的宽度
			rend.setValue(Rendition.FILL_OPACITY, new Float(0.0D));

			DoublePoint p1 = new DoublePoint(0.0D, 0.0D);
			DoublePoint p2 = new DoublePoint(this.imgsizex, 0.0D);
			DoublePoint p3 = new DoublePoint(this.imgsizex, this.imgsizey);
			DoublePoint p4 = new DoublePoint(0.0D, this.imgsizey);
			// 取得一个矩形的四个点
			DoublePoint mp1 = mymap.transformScreenToNumeric(p1);
			DoublePoint mp2 = mymap.transformScreenToNumeric(p2);
			DoublePoint mp3 = mymap.transformScreenToNumeric(p3);
			DoublePoint mp4 = mymap.transformScreenToNumeric(p4);
			double[] p = new double[10];
			p[0] = mp1.x;
			p[1] = mp1.y;
			p[2] = mp2.x;
			p[3] = mp2.y;
			p[4] = mp3.x;
			p[5] = mp3.y;
			p[6] = mp4.x;
			p[7] = mp4.y;
			p[8] = mp1.x;
			p[9] = mp1.y;

			Feature ft = ff.createRegion(p, rend, null, null, pk);
			/**
			 * @创建一个图元（通过featurefactory 创建---region是区域的意思）
			 * @第一个参数 p double[] ---thePoints
			 * @第二个参数 样式
			 * @第三个参数 lableRend
			 * @第四个参数 attrs
			 * @第五个参数 主键值
			 */
			PrimaryKey[] spk = { new PrimaryKey(new Attribute(101)) };
			Vector col = new Vector();
			FeatureSet ftset = anlayer.searchByPrimaryKey(col, spk, null);
			if (ftset == null) {
				PrimaryKey localPrimaryKey1 = anlayer.addFeature(ft);
			} else {
				anlayer.replaceFeature(pk, ft);
			}
			boundmap.setDeviceBounds(new DoubleRect(0.0D, 0.0D,
					this.smallimgsizex, this.smallimgsizey));
			boundmap.setDistanceUnits(mymap.getDistanceUnits());

			if (mymap.getZoom() / boundmap.getZoom() >= 0.8D) {
				boundmap.setZoom(mymap.getZoom() * 1.25D);
				boundmap.setCenter(mymap.getCenter());
			} else {
				boundmap.setZoom(resetzoom);
				boundmap.setCenter(resetpoint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.setContentType("image/jpeg");
		try {
			sout = response.getOutputStream();
			ImageRequestComposer irc = ImageRequestComposer.create(boundmap,
					65535, Color.white, "image/jpeg");
			MapXtremeImageRenderer renderer = new MapXtremeImageRenderer(
					this.mapxtremeurl);
			renderer.render(irc);
			renderer.toStream(sout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (sout != null)
				sout.close();
		} catch (Exception localException1) {
		}
	}

	/**
	 * @通过鹰眼移动地图
	 */
	private void resetbybound(MapJ mymap, MapJ boundmap,
			HttpServletRequest request) {
		try {
			Double centerx = new Double(request.getParameter("centerx"));
			// 取得x点
			Double centery = new Double(request.getParameter("centery"));
			// 取得Y点
			DoublePoint screenpoint = new DoublePoint(centerx.doubleValue(),
					centery.doubleValue());
			DoublePoint mappoint = boundmap
					.transformScreenToNumeric(screenpoint);
			mymap.setCenter(mappoint);
			// 主地图中点变化
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (this.errflag == true) {
			PrintWriter out = response.getWriter();
			response.setContentType("text/html; charset=UTF-8");
			out.print(new String(this.errmessage.getBytes("UTF-8"),
					"iso-8859-1"));
			out.close();
		}
		MapJ mymap = null;
		MapJ boundmap = null;
		String rqutype = request.getParameter("rqutype");
		if ((rqutype != null) && (rqutype.equals("initmap"))) {
			mymap = initmap(request);
			responseimg(mymap, response);
		} else if ((rqutype != null) && (rqutype.equals("chgmapview"))) {
			mymap = initmap(request);
			chgmapview(mymap, request);
			responseimg(mymap, response);
		} else if ((rqutype != null) && (rqutype.equals("panmap"))) {
			mymap = initmap(request);
			panmap(mymap, request);
			responseimg(mymap, response);
		} else if ((rqutype != null) && (rqutype.equals("resetmap"))) {
			mymap = initmap(request);
			resetmap(mymap, request);
			responseimg(mymap, response);
		} else if ((rqutype != null) && (rqutype.equals("centerpoint"))) {
			mymap = initmap(request);
			responsetext(mymap, response, "centerpoint");
		} else if ((rqutype != null) && (rqutype.equals("zoom"))) {
			mymap = initmap(request);
			responsetext(mymap, response, "zoom");
		} else if ((rqutype != null) && (rqutype.equals("boundmap"))) {
			mymap = initmap(request);
			boundmap = initboundmap(request);
			responsebound(mymap, boundmap, response);
		} else if ((rqutype != null) && (rqutype.equals("smallpanmap"))) {
			mymap = initmap(request);
			boundmap = initboundmap(request);
			resetbybound(mymap, boundmap, request);
			responseimg(mymap, response);
		} else if ((rqutype != null) && (rqutype.equals("querymap"))) {
			String layernames = request.getParameter("layernames");
			String selectnames = request.getParameter("selectnames");
			System.out.println("图层名称=" + layernames);
			System.out.println("查询名称=" + selectnames);
			mymap = initmap(request);

			try {
				selectF(mymap,layernames,selectnames,response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void errset(String msg) {
		this.errflag = true;
		this.errmessage = msg;
	}

	/**
	 * @指定查找一个图元
	 * @第一个参数 mymap ---Mapj
	 * @第二个参数 layernames 图层名称
	 * @第三个参数 selectnames 图元名称
	 * @第四个参数 Response
	 */
	private void selectF(MapJ mymap, String layernames, String selectnames,
			HttpServletResponse res) throws Exception {

		if (mymap == null) {
			mymap = initMapJ();
		}

		// 以下是进行图元的查找和渲染
		Layer m_Layer = mymap.getLayers().getLayer(layernames);

		if (m_Layer == null) {
			System.out.println("没有[" + layernames + "]这个图层");
			responseimg(mymap, res);
			return;
		}

		// 删除以上操作已经添加的theme列表
		m_Layer.getThemeList().removeAll(true);

		List columnNames = new ArrayList();
		Feature ftr;

		TableInfo tabInfo = m_Layer.getTableInfo();
		// fill vector with Column names
		for (int i = 0; i < tabInfo.getColumnCount(); i++) {
			columnNames.add(tabInfo.getColumnName(i));
			System.out.println(tabInfo.getColumnName(i) + "  --  ");
		}
		// Perform a search to get the Features(records)from the layer
		RewindableFeatureSet rFtrSet;
		rFtrSet = new RewindableFeatureSet(m_Layer.searchAll(columnNames, null));

		// FeatureSet fs = m_Layer.searchAll(columnNames,
		// QueryParams.ALL_PARAMS);
		ftr = rFtrSet.getNextFeature();

		while (ftr != null) {
			if (ftr.getAttribute(0).toString().equals(selectnames)) {
				// 定位点
				if (ftr.getGeometry().getType() == Geometry.TYPE_POINT) {
					double newZoomValue;
					double currentZoom = mymap.getZoom();
					if (m_Layer.isZoomLayer()
							&& (currentZoom > m_Layer.getZoomMax() || currentZoom < m_Layer
									.getZoomMin())) {
						newZoomValue = m_Layer.getZoomMax() / 2;
						mymap.setZoom(newZoomValue);
					}

					mymap.setCenter(ftr.getGeometry().getBounds().center());
				}
				// 定位线、面
				if (ftr.getGeometry().getType() == Geometry.TYPE_LINE
						|| ftr.getGeometry().getType() == Geometry.TYPE_REGION) {

					if (ftr.getGeometry().getBounds().width() > 0
							&& ftr.getGeometry().getBounds().height() > 0) {
						mymap.setBounds(ftr.getGeometry().getBounds());
						mymap.setZoom(mymap.getZoom() * 1.1);
					}
				}
				break;
			}
			ftr = rFtrSet.getNextFeature();
		}
		rFtrSet.rewind();

		// 高亮显示

		// 创建一个 SelectionTheme
		SelectionTheme selTheme = new SelectionTheme("LocateFeature");
		// 创建一个Selection对象并且把选择的图元加入
		Selection sel = new Selection();
		sel.add(ftr);

		// 把Selection对象加入到SelectionTheme
		selTheme.setSelection(sel);

		// 设置SelectionTheme的显示渲染的样式
		Rendition rend = RenditionImpl.getDefaultRendition();
		rend.setValue(Rendition.FILL, Color.red);
		selTheme.setRendition(rend);

		// 添加SelectionTheme到指定layer的theme列表中
		m_Layer.getThemeList().add(selTheme);
		// m_Layer.getThemeList().insert(selTheme, 0);

		responseimg(mymap, res);

	}

}
