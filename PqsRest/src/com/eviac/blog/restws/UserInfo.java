package com.eviac.blog.restws;
 
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.json.JSONException;  
import org.json.JSONObject;  
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;




/**
* 
* @author pavithra
* 
*/
 
// 这里@Path定义了类的层次路径。 
// 指定了资源类提供服务的URI路径。
@Path("PlatformToUstb")
public class UserInfo {
 
// @GET表示方法会处理HTTP GET请求
@GET
// 这里@Path定义了类的层次路径。指定了资源类提供服务的URI路径。
@Path("/name/{i}")
// @Produces定义了资源类方法会生成的媒体类型。
@Produces(MediaType.TEXT_XML)
// @PathParam向@Path定义的表达式注入URI参数值。
public String userName(@PathParam("i") String i) {
 
String name = i;
return "<User>" + "<Name>" + name + "</Name>" + "</User>";
}
 
@GET
@Path("/age/{j}") 
@Produces(MediaType.TEXT_XML)
public String userAge(@PathParam("j") int j) {
 
int age = j;
return "<User>" + "<Age>" + age + "</Age>" + "</User>";
}

@POST
@Path("/receive")
public String subscriber1(String k) throws JSONException , Exception
{
  String result = k;
  JSONObject strJson= new JSONObject(result);
  System.out.println("\r\n接收到JSON数据包:");
  System.out.println(strJson);
  System.out.println("一条推送已成功");
  
  if(strJson.getString("notifyType").equals("deviceDataChanged")==true)
  {
	  Class.forName("com.mysql.jdbc.Driver");//加载数据库驱动
	  String url="jdbc:mysql://localhost:3306/tp5";//声明数据库test的url
	  String user="root";//数据库的用户名
	  String password="root";//数据库的密码
	  String NattierSeaSensor;
	  Connection conn=DriverManager.getConnection(url, user, password);
	  Statement stmt = conn.createStatement();  
	  
	  JSONObject servicestr = new JSONObject(strJson.getString("service"));
	  JSONObject datastr = new JSONObject(servicestr.getString("data"));
	  NattierSeaSensor = datastr .getString("NattierSeaSensor");
	  System.out.println("接收到节点数据类型推送");
	  System.out.println("节点："+NattierSeaSensor.substring(0, 2));
	  System.out.println("温度："+NattierSeaSensor.substring(2, 4));
	  System.out.println("湿度："+NattierSeaSensor.substring(4, 6));
	  System.out.println("光照强度："+NattierSeaSensor.substring(6, 9));
	  System.out.println("空气质量："+NattierSeaSensor.substring(9, 12));
	  
	  String sql = "INSERT INTO qs_sensor(nodenum,temperature,humidity,light,air,msgtype,time) "
	  		+ "VALUES("+Integer.parseInt(NattierSeaSensor.substring(0, 2))+","
	  		+ Integer.parseInt(NattierSeaSensor.substring(2, 4))+","
	  		+ Integer.parseInt(NattierSeaSensor.substring(4, 6))+","
	  		+ Integer.parseInt(NattierSeaSensor.substring(6, 9))+","
	  		+ Integer.parseInt(NattierSeaSensor.substring(9, 12))+","
	  		+ "'COAP',NOW())";  
	  int res = stmt.executeUpdate(sql); 
	  if(res==1)
		  System.out.println("该条数据已插入数据库");

	  
  }
  return result;
}

@POST
@Path("/server")
public String subscriber7(String k) throws JSONException , Exception
{
  String result = k;
  int res = 0;
  JSONObject strJson= new JSONObject(result);
  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
  System.out.println("\r\n接收到JSON数据包:");
  System.out.println(strJson);
  System.out.println(df.format(new Date())+" 一条推送已成功");// new Date()为获取当前系统时间
  
  if(strJson.getString("notifyType").equals("deviceDataChanged")==true)
  {
	  Class.forName("com.mysql.jdbc.Driver");//加载数据库驱动
	  String url="jdbc:mysql://localhost:3306/tp5";//声明数据库test的url
	  String user="root";//数据库的用户名
	  String password="root";//数据库的密码
	  String data;
	  Connection conn=DriverManager.getConnection(url, user, password);
	  Statement stmt = conn.createStatement();  
	  
	  JSONObject servicestr = new JSONObject(strJson.getString("service"));
	  JSONObject datastr = new JSONObject(servicestr.getString("data"));
	  data = datastr .getString("NattierSeaSensor");
	  if(data.length() < 30)
	  {
		  System.out.println("接收数据为空:"+data);
		  return result;
	  }
	  String key = data.substring(1, 9);
	  String serial = data.substring(9, 17);
	  if(data.substring(0, 1).equals("d")==true)
	  {
		  String sql = "SELECT * FROM qs_experiment_device WHERE dserial LIKE \""+serial+"\" ORDER BY id DESC LIMIT 1";
		  ResultSet rs = stmt.executeQuery(sql);
		  if (rs.next())
		  {
			  String aid = rs.getString("aid");
			  int tid = rs.getInt("tid");
			  sql = "SELECT * FROM qs_experiment_application WHERE id="+aid;
			  rs = stmt.executeQuery(sql);
			  if(rs.next() && rs.getString("key").equals(key)==true)
			  {
				  //鉴权成功
				  switch(tid)
				  {
				  //温湿度
				  case 3: sql = "INSERT INTO qs_tempandhum (serial,temp,hum,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Double.parseDouble(data.substring(17,20))/10 + ","
						  + Double.parseDouble(data.substring(20,23))/10 + ","
						  + "'COAP',NOW())";
					  res = stmt.executeUpdate(sql); 
					  break;
				  //温度
				  case 5: sql = "INSERT INTO qs_wendu (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Double.parseDouble(data.substring(17,20))/10 + ","
						  + "'COAP',NOW())";
				          res = stmt.executeUpdate(sql); 
				          break;
				 //湿度
				  case 6: sql = "INSERT INTO qs_shidu (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,20)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //红外测距
				  case 7: sql = "INSERT INTO qs_hongwaiceju (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,20)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //光照度
				  case 8: sql = "INSERT INTO qs_guangzhaodu (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,22)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //人体脉搏
				  case 9: sql = "INSERT INTO qs_rentimaibo (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,20)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //线性霍尔
				  case 10: sql = "INSERT INTO qs_xianxinghuoer (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,22)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql);
						  break;
				 //超声波
				  case 11: sql = "INSERT INTO qs_chaoshengbo (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,20)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //电子罗盘
				  case 12: sql = "INSERT INTO qs_dianziluopan (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,20)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //加速度
				  case 13:  
						  break;
				 //陀螺仪
				  case 14:  
						  break;
				 //气压
				  case 15: sql = "INSERT INTO qs_qiya (serial,data,msgtype,time)"
                          + "VALUES('" + serial + "'," 
						  + Double.parseDouble(data.substring(17,23))/10 + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //PM2.5
				  case 16: sql = "INSERT INTO qs_pm (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,20)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //气敏
				  case 17: sql = "INSERT INTO qs_qimin (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,24)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql);
						  break;
				 //压力
				  case 18: sql = "INSERT INTO qs_yali (serial,data,msgtype,time)"
                          + "VALUES('" + serial + "'," 
						  + Double.parseDouble(data.substring(17,21))/10 + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break; 
				 //磁角度
				  case 19: sql = "INSERT INTO qs_cijiaodu (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,20)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //电压
				  case 20: sql = "INSERT INTO qs_dianya (serial,data,msgtype,time)"
                          + "VALUES('" + serial + "'," 
						  + Double.parseDouble(data.substring(17,20))/100 + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //电流
				  case 21: sql = "INSERT INTO qs_dianliu (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,21)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql);
						  break;
				 //紫外线
				  case 22: sql = "INSERT INTO qs_ziwaixian (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,22)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql);
						  break;
				 //火焰
				  case 24: sql = "INSERT INTO qs_huoyan (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,18)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //红外热释电
				  case 25: sql = "INSERT INTO qs_hongwaireshidian (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,18)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //振动
				  case 26: sql = "INSERT INTO qs_zhendong (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,18)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //开关霍尔
				  case 27: sql = "INSERT INTO qs_kaiguanhuoer (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,18)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //声音检测
				  case 28: sql = "INSERT INTO qs_shengyinjiance (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,18)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //干簧管
				  case 29: sql = "INSERT INTO qs_ganhuangguan (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,18)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //工业环境传感器：	例3字节pm2.5 3字节温度 3字节湿度 3字节雨量  用12字节长的字符串存储
				  case 37: sql = "INSERT INTO qs_gongyehuanjing (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + "'" + data.substring(17, 29) + "',"
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				  }
			  }
			  else
			  {
				  System.out.println("秘钥错误");
			  }
			  if( res == 1)
				  System.out.println("该条数据已插入数据库");
			  
		  }
		  else
		  {
			  System.out.println("设备不存在");
		  }
	  }

	  
  }
  return result;
}

@POST
@Path("/bd")
public String bdsubscriber(String k) throws JSONException , Exception
{
  String result = k;
  JSONObject strJson= new JSONObject(result);
  System.out.println("\r\n接收到JSON数据包:");
  System.out.println(strJson);
  System.out.println("一条推送已成功");
  
  if(strJson.getString("notifyType").equals("deviceDataChanged")==true)
  {
	  Class.forName("com.mysql.jdbc.Driver");//加载数据库驱动
	  String url="jdbc:mysql://localhost:3306/tp5";//声明数据库test的url
	  String user="root";//数据库的用户名
	  String password="root";//数据库的密码
	  String NattierSeaSensor;
	  Connection conn=DriverManager.getConnection(url, user, password);
	  Statement stmt = conn.createStatement();  
	  
	  JSONObject servicestr = new JSONObject(strJson.getString("service"));
	  JSONObject datastr = new JSONObject(servicestr.getString("data"));
	  NattierSeaSensor = datastr .getString("NattierSeaSensor");
	  System.out.println("接收到节点数据类型推送");
	  System.out.println("设备编号："+NattierSeaSensor.substring(23, 30));
	  System.out.println("纬度："+NattierSeaSensor.substring(0, 2)+"."+NattierSeaSensor.substring(2, 8)+"-"+NattierSeaSensor.substring(21, 22));
	  System.out.println("经度："+NattierSeaSensor.substring(8, 11)+"."+NattierSeaSensor.substring(11, 17)+"-"+NattierSeaSensor.substring(22, 23));
	  System.out.println("温度："+NattierSeaSensor.substring(17, 19));
	  System.out.println("湿度："+NattierSeaSensor.substring(19, 21));
	  
	  String sql = "INSERT INTO qs_nbbd(device,latitude,longitude,ns,ew,temperature,humidity,time,la,lon) "
	  		+ "VALUES(\'"+NattierSeaSensor.substring(23, 30)+"\',"
			+ Float.parseFloat(NattierSeaSensor.substring(0, 8))/1000000+","
	  		+ Float.parseFloat(NattierSeaSensor.substring(8, 17))/1000000+","
	  		+ "\'"+NattierSeaSensor.substring(21, 22)+"\'"+","
	  		+ "\'"+NattierSeaSensor.substring(22, 23)+"\'"+","
	  		+ Integer.parseInt(NattierSeaSensor.substring(17, 19))+","
	  		+ Integer.parseInt(NattierSeaSensor.substring(19, 21))+","
	  		+ "NOW(),"
	  		+ NattierSeaSensor.substring(0, 8)+","
	  		+ NattierSeaSensor.substring(8, 17)+")";  
	  int res = stmt.executeUpdate(sql); 
	  if(res==1)
		  System.out.println("该条数据已插入数据库");

	  
  }
  return result;
}


@POST
@Path("/transmit")
public String subscriber2(String k) throws JSONException , Exception
{
  String result = k;
  JSONObject strJson= new JSONObject(result);
  System.out.println("\r\n接收到JSON数据包:");
  System.out.println(strJson);
  System.out.println("一条推送已成功");
  
  if(strJson.getString("notifyType").equals("deviceDataChanged")==true)
  {
	  Class.forName("com.mysql.jdbc.Driver");//加载数据库驱动
	  String url="jdbc:mysql://localhost:3306/mysql";//声明数据库test的url
	  String user="root";//数据库的用户名
	  String password="root";//数据库的密码
	  String NattierSeaSensor;
	  Connection conn=DriverManager.getConnection(url, user, password);
	  Statement stmt = conn.createStatement();  
	  
	  JSONObject servicestr = new JSONObject(strJson.getString("service"));
	  JSONObject datastr = new JSONObject(servicestr.getString("data"));
	  NattierSeaSensor = datastr .getString("NattierSeaSensor");
	  System.out.println("透传数据是:"+  NattierSeaSensor);
	  
	  String sql = "INSERT INTO testtable(testField,msgtype,time) VALUES( '"+ NattierSeaSensor+ "','COAP',NOW())";  
	  int res = stmt.executeUpdate(sql); 
	  if(res==1)
		  System.out.println("该条数据已插入数据库");

	  
  }
  return result;
}

@POST
@Path("/analysis")
public String subscriber4(String k) throws JSONException , Exception
{
  String result = k;
  JSONObject strJson= new JSONObject(result);
  System.out.println("\r\n接收到JSON数据包:");
  System.out.println(strJson);
  System.out.println("一条推送已成功");
  
  if(strJson.getString("notifyType").equals("deviceDataChanged")==true)
  {
	  String NattierSeaSensor; 
	  
	  JSONObject servicestr = new JSONObject(strJson.getString("service"));
	  JSONObject datastr = new JSONObject(servicestr.getString("data"));
	  NattierSeaSensor = datastr .getString("NattierSeaSensor");
	  System.out.println("透传数据是:"+  NattierSeaSensor);
	  
  }
  return result;
}


@POST
@Path("/justshow")
public String subscriber3(String k)
{
  String result = k;
  System.out.println(result);
  return result;
}


}