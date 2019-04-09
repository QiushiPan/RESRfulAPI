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
 
// ����@Path��������Ĳ��·���� 
// ָ������Դ���ṩ�����URI·����
@Path("PlatformToUstb")
public class UserInfo {
 
// @GET��ʾ�����ᴦ��HTTP GET����
@GET
// ����@Path��������Ĳ��·����ָ������Դ���ṩ�����URI·����
@Path("/name/{i}")
// @Produces��������Դ�෽�������ɵ�ý�����͡�
@Produces(MediaType.TEXT_XML)
// @PathParam��@Path����ı��ʽע��URI����ֵ��
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
  System.out.println("\r\n���յ�JSON���ݰ�:");
  System.out.println(strJson);
  System.out.println("һ�������ѳɹ�");
  
  if(strJson.getString("notifyType").equals("deviceDataChanged")==true)
  {
	  Class.forName("com.mysql.jdbc.Driver");//�������ݿ�����
	  String url="jdbc:mysql://localhost:3306/tp5";//�������ݿ�test��url
	  String user="root";//���ݿ���û���
	  String password="root";//���ݿ������
	  String NattierSeaSensor;
	  Connection conn=DriverManager.getConnection(url, user, password);
	  Statement stmt = conn.createStatement();  
	  
	  JSONObject servicestr = new JSONObject(strJson.getString("service"));
	  JSONObject datastr = new JSONObject(servicestr.getString("data"));
	  NattierSeaSensor = datastr .getString("NattierSeaSensor");
	  System.out.println("���յ��ڵ�������������");
	  System.out.println("�ڵ㣺"+NattierSeaSensor.substring(0, 2));
	  System.out.println("�¶ȣ�"+NattierSeaSensor.substring(2, 4));
	  System.out.println("ʪ�ȣ�"+NattierSeaSensor.substring(4, 6));
	  System.out.println("����ǿ�ȣ�"+NattierSeaSensor.substring(6, 9));
	  System.out.println("����������"+NattierSeaSensor.substring(9, 12));
	  
	  String sql = "INSERT INTO qs_sensor(nodenum,temperature,humidity,light,air,msgtype,time) "
	  		+ "VALUES("+Integer.parseInt(NattierSeaSensor.substring(0, 2))+","
	  		+ Integer.parseInt(NattierSeaSensor.substring(2, 4))+","
	  		+ Integer.parseInt(NattierSeaSensor.substring(4, 6))+","
	  		+ Integer.parseInt(NattierSeaSensor.substring(6, 9))+","
	  		+ Integer.parseInt(NattierSeaSensor.substring(9, 12))+","
	  		+ "'COAP',NOW())";  
	  int res = stmt.executeUpdate(sql); 
	  if(res==1)
		  System.out.println("���������Ѳ������ݿ�");

	  
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
  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
  System.out.println("\r\n���յ�JSON���ݰ�:");
  System.out.println(strJson);
  System.out.println(df.format(new Date())+" һ�������ѳɹ�");// new Date()Ϊ��ȡ��ǰϵͳʱ��
  
  if(strJson.getString("notifyType").equals("deviceDataChanged")==true)
  {
	  Class.forName("com.mysql.jdbc.Driver");//�������ݿ�����
	  String url="jdbc:mysql://localhost:3306/tp5";//�������ݿ�test��url
	  String user="root";//���ݿ���û���
	  String password="root";//���ݿ������
	  String data;
	  Connection conn=DriverManager.getConnection(url, user, password);
	  Statement stmt = conn.createStatement();  
	  
	  JSONObject servicestr = new JSONObject(strJson.getString("service"));
	  JSONObject datastr = new JSONObject(servicestr.getString("data"));
	  data = datastr .getString("NattierSeaSensor");
	  if(data.length() < 30)
	  {
		  System.out.println("��������Ϊ��:"+data);
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
				  //��Ȩ�ɹ�
				  switch(tid)
				  {
				  //��ʪ��
				  case 3: sql = "INSERT INTO qs_tempandhum (serial,temp,hum,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Double.parseDouble(data.substring(17,20))/10 + ","
						  + Double.parseDouble(data.substring(20,23))/10 + ","
						  + "'COAP',NOW())";
					  res = stmt.executeUpdate(sql); 
					  break;
				  //�¶�
				  case 5: sql = "INSERT INTO qs_wendu (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Double.parseDouble(data.substring(17,20))/10 + ","
						  + "'COAP',NOW())";
				          res = stmt.executeUpdate(sql); 
				          break;
				 //ʪ��
				  case 6: sql = "INSERT INTO qs_shidu (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,20)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //������
				  case 7: sql = "INSERT INTO qs_hongwaiceju (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,20)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //���ն�
				  case 8: sql = "INSERT INTO qs_guangzhaodu (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,22)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //��������
				  case 9: sql = "INSERT INTO qs_rentimaibo (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,20)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //���Ի���
				  case 10: sql = "INSERT INTO qs_xianxinghuoer (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,22)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql);
						  break;
				 //������
				  case 11: sql = "INSERT INTO qs_chaoshengbo (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,20)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //��������
				  case 12: sql = "INSERT INTO qs_dianziluopan (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,20)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //���ٶ�
				  case 13:  
						  break;
				 //������
				  case 14:  
						  break;
				 //��ѹ
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
				 //����
				  case 17: sql = "INSERT INTO qs_qimin (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,24)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql);
						  break;
				 //ѹ��
				  case 18: sql = "INSERT INTO qs_yali (serial,data,msgtype,time)"
                          + "VALUES('" + serial + "'," 
						  + Double.parseDouble(data.substring(17,21))/10 + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break; 
				 //�ŽǶ�
				  case 19: sql = "INSERT INTO qs_cijiaodu (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,20)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //��ѹ
				  case 20: sql = "INSERT INTO qs_dianya (serial,data,msgtype,time)"
                          + "VALUES('" + serial + "'," 
						  + Double.parseDouble(data.substring(17,20))/100 + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //����
				  case 21: sql = "INSERT INTO qs_dianliu (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,21)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql);
						  break;
				 //������
				  case 22: sql = "INSERT INTO qs_ziwaixian (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,22)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql);
						  break;
				 //����
				  case 24: sql = "INSERT INTO qs_huoyan (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,18)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //�������͵�
				  case 25: sql = "INSERT INTO qs_hongwaireshidian (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,18)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //��
				  case 26: sql = "INSERT INTO qs_zhendong (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,18)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //���ػ���
				  case 27: sql = "INSERT INTO qs_kaiguanhuoer (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,18)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //�������
				  case 28: sql = "INSERT INTO qs_shengyinjiance (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,18)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //�ɻɹ�
				  case 29: sql = "INSERT INTO qs_ganhuangguan (serial,data,msgtype,time)"
						  + "VALUES('" + serial + "'," 
						  + Integer.parseInt(data.substring(17,18)) + ","
						  + "'COAP',NOW())";
						  res = stmt.executeUpdate(sql); 
						  break;
				 //��ҵ������������	��3�ֽ�pm2.5 3�ֽ��¶� 3�ֽ�ʪ�� 3�ֽ�����  ��12�ֽڳ����ַ����洢
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
				  System.out.println("��Կ����");
			  }
			  if( res == 1)
				  System.out.println("���������Ѳ������ݿ�");
			  
		  }
		  else
		  {
			  System.out.println("�豸������");
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
  System.out.println("\r\n���յ�JSON���ݰ�:");
  System.out.println(strJson);
  System.out.println("һ�������ѳɹ�");
  
  if(strJson.getString("notifyType").equals("deviceDataChanged")==true)
  {
	  Class.forName("com.mysql.jdbc.Driver");//�������ݿ�����
	  String url="jdbc:mysql://localhost:3306/tp5";//�������ݿ�test��url
	  String user="root";//���ݿ���û���
	  String password="root";//���ݿ������
	  String NattierSeaSensor;
	  Connection conn=DriverManager.getConnection(url, user, password);
	  Statement stmt = conn.createStatement();  
	  
	  JSONObject servicestr = new JSONObject(strJson.getString("service"));
	  JSONObject datastr = new JSONObject(servicestr.getString("data"));
	  NattierSeaSensor = datastr .getString("NattierSeaSensor");
	  System.out.println("���յ��ڵ�������������");
	  System.out.println("�豸��ţ�"+NattierSeaSensor.substring(23, 30));
	  System.out.println("γ�ȣ�"+NattierSeaSensor.substring(0, 2)+"."+NattierSeaSensor.substring(2, 8)+"-"+NattierSeaSensor.substring(21, 22));
	  System.out.println("���ȣ�"+NattierSeaSensor.substring(8, 11)+"."+NattierSeaSensor.substring(11, 17)+"-"+NattierSeaSensor.substring(22, 23));
	  System.out.println("�¶ȣ�"+NattierSeaSensor.substring(17, 19));
	  System.out.println("ʪ�ȣ�"+NattierSeaSensor.substring(19, 21));
	  
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
		  System.out.println("���������Ѳ������ݿ�");

	  
  }
  return result;
}


@POST
@Path("/transmit")
public String subscriber2(String k) throws JSONException , Exception
{
  String result = k;
  JSONObject strJson= new JSONObject(result);
  System.out.println("\r\n���յ�JSON���ݰ�:");
  System.out.println(strJson);
  System.out.println("һ�������ѳɹ�");
  
  if(strJson.getString("notifyType").equals("deviceDataChanged")==true)
  {
	  Class.forName("com.mysql.jdbc.Driver");//�������ݿ�����
	  String url="jdbc:mysql://localhost:3306/mysql";//�������ݿ�test��url
	  String user="root";//���ݿ���û���
	  String password="root";//���ݿ������
	  String NattierSeaSensor;
	  Connection conn=DriverManager.getConnection(url, user, password);
	  Statement stmt = conn.createStatement();  
	  
	  JSONObject servicestr = new JSONObject(strJson.getString("service"));
	  JSONObject datastr = new JSONObject(servicestr.getString("data"));
	  NattierSeaSensor = datastr .getString("NattierSeaSensor");
	  System.out.println("͸��������:"+  NattierSeaSensor);
	  
	  String sql = "INSERT INTO testtable(testField,msgtype,time) VALUES( '"+ NattierSeaSensor+ "','COAP',NOW())";  
	  int res = stmt.executeUpdate(sql); 
	  if(res==1)
		  System.out.println("���������Ѳ������ݿ�");

	  
  }
  return result;
}

@POST
@Path("/analysis")
public String subscriber4(String k) throws JSONException , Exception
{
  String result = k;
  JSONObject strJson= new JSONObject(result);
  System.out.println("\r\n���յ�JSON���ݰ�:");
  System.out.println(strJson);
  System.out.println("һ�������ѳɹ�");
  
  if(strJson.getString("notifyType").equals("deviceDataChanged")==true)
  {
	  String NattierSeaSensor; 
	  
	  JSONObject servicestr = new JSONObject(strJson.getString("service"));
	  JSONObject datastr = new JSONObject(servicestr.getString("data"));
	  NattierSeaSensor = datastr .getString("NattierSeaSensor");
	  System.out.println("͸��������:"+  NattierSeaSensor);
	  
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