package com.zhoutong.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

import jxl.Cell;

import com.zhoutong.constant.Const;
import com.zhoutong.jxl.ForWork;
import com.zhoutong.jxl.MainInformation;

public class Persistent {
	/**
	 * @author hahazhoutie4
	 * @return
	 * @website cnblogs.com/hahazhoutie4-blogs/
	 */
	private Connection connection;
	private Statement statement;
	private Map<String, String> map_;
	/**
	 * @return ���ع��������Map����
	 */
	public Map<String, String> getMap_() {
		return map_;
	}

	public void setMap_(Map<String, String> map_) {
		this.map_ = map_;
	}

	private void getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3305/ajax", "root", "jintian123");
			connection.setAutoCommit(true);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			System.out.println("fail to connect mysql");
		}
	}

	private void getStatement() {
		if (null == connection) {
			getConnection();
		}
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createTable(ForWork forwork) {
		if (null == statement) {
			getStatement();
		}
		Map<String,MainInformation> map=forwork.getMap_information();
		Set<String> set=forwork.getMap_information().keySet();
		for(String sheet_name:set){
		String schema = forwork.getSchema();
		String sql = "create database if not exists " + schema + ";";
		try {
			System.out.print(sql);
			statement.execute(sql);
			String sql2 = "create table if not exists " + schema + "."
					+ sheet_name + "( id INT PRIMARY KEY ,";
			List<String> data_names = map.get(sheet_name).getNames();
			int i = 1;
			for (String data_name : data_names) {
				System.out.println("---" + data_name);
				if (i != 2) {
					if (i == 1 || i == 3) {
						sql2 = sql2 + data_name.toString() + " varchar(45)"
								+ " NOT NULL";
					} else {
						sql2 = sql2 + data_name.toString() + " DOUBLE NOT NULL";
					}
					if (i < data_names.size()) {
						sql2 = sql2 + ",";
					}
				}
				i++;
			}
			if (sheet_name.equals("ǽ") || sheet_name.contains("��")
					|| sheet_name.contains("��") || sheet_name.contains("��")) {
				sql2 = sql2 + ",�������ȼ�  varchar(45)";
			}
			sql2 = sql2 + ");";
			System.out.println(sql2);
			statement.execute(sql2);
			// ����������
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}
	}

	public void insertData(ForWork forwork, boolean rewrite) {
		// Cell[3].getContents().equals("С��")||Cell[3].getContents().equals("�ϼ�")Ϊ�ϼ�������
		//¥��   rewrite Ϊfalse;
		Map<String,MainInformation> map=forwork.getMap_information();
		Set<String> set=forwork.getMap_information().keySet();
		for(String sheet_name:set){
		String sql = "";
		if (null == statement) {
			getStatement();
		}
		String schema = forwork.getSchema();
		if (rewrite) {
			try {
				System.out.println("delete  from " + schema + "." + sheet_name
						+ ";");
				statement.execute("delete  from " + schema + "." + sheet_name
						+ ";");
				System.out.println("ɾ�������������");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Map<Integer, Cell[]> map_ = map.get(sheet_name).getMap();
		int size = map_.size();
		System.out.println("size   --"+size);
		int id = 0;
		for (int j = 3; j < size; j++) {
			Cell[] c = map_.get(j); // ĳһ�е�����c
			int length = c.length;
			int caculate = 0;
				sql = sql + "insert into " + schema + "." + sheet_name
						+ " values(" + id + ",";
				for (Cell ci : c) {
					if (caculate > 0 && caculate != 2) {
						String content = ci.getContents();
						if (caculate != 1 && caculate != 3) {
							Float f = Float.valueOf(content);
							sql = sql + f;
						} else {
							sql = sql + "\"" + content + "\"";
						}
						if (caculate < length - 1) {
							sql = sql + ",";
						}
					}
					caculate++;
				}
				id++;
				if (sheet_name.equals("ǽ") || sheet_name.contains("��")
						|| sheet_name.contains("��") || sheet_name.contains("��")) {
					sql = sql + ",null";
				}
				sql = sql + ");";
				try {
					statement.addBatch(sql);
					statement.executeBatch();
				} catch (SQLException e) {
					e.printStackTrace();
				} // �˴�ִ�в�����伴��
				System.out.println(sql);
				System.out.println();
				sql = "";
			
		}
		}
	}

//	public boolean isDataExist(ForWork forwork) {
//		
//		
//		if (null == statement) {
//			getStatement();
//		}
//		try {
//			ResultSet res = statement.executeQuery("select * from "
//					+ forwork.getSchema() + "." + sheet_name);
//			return res.next();	
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}

	// ���������ǿ���У�ͨ������������key-valueֵ
	public void insert_other_information(ForWork forwork,String conrete_information, String floor_information) {
		String db_name = forwork.getSchema(); // ���ݿ�����
		String table_name = sheet_name; // ��ȡ�������
		String sql = "update " + db_name + "." + table_name + " set �������ȼ� "
				+ "=" + "'" + conrete_information + "'" + " where ¥��=" + "'"
				+ floor_information + "';";
		try {
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("sql���" + sql);
	}

	// ���������ǿ��������ͨ��properties�ļ�
	public void insert_other_information(ForWork forwork, Properties properties) {
		String db_name = forwork.getSchema(); // ���ݿ�����
		String table_name = sheet_name; // ��ȡ�������
		Set<Object> set = properties.keySet();
		for (Object o : set) {
			String floor_information = (String) o;
			String concrete_information = (String) properties
					.get(floor_information);
			String sql = "update " + db_name + "." + table_name + " set �������ȼ� "
					+ "=" + "'" + concrete_information + "'" + " where ¥��="
					+ "'" + floor_information + "';";
			System.out.println(sql);
			try {
				statement.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// ���������ǿ��������ͨ��xls�ļ�(������excel97)
	public void insert_other_information(ForWork forwork,
			Map<String, String> information) {
		String db_name = forwork.getSchema(); // ���ݿ�����
		String table_name = sheet_name; // ��ȡ�������
		Set<String> set = information.keySet();
		for (String o : set) {
			String concrete_information = (String) information.get(o);
			String sql = "update " + db_name + "." + table_name + " set �������ȼ� "
					+ "=" + "'" + concrete_information + "'" + " where ¥��="
					+ "'" + o + "';";
			System.out.println(sql);
			try {
				statement.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// ��ȡǽ������
	/**
	 * @param forwork
	 *            ��ǰ�����Ϣ
	 * @param map
	 *            ��ǰ������ǿ�ȵȼ���Ϣ,"����"---SortedSet1 ,"����"----SortedSet2
	 */
	public Map<String, String> getC_1(ForWork forwork,
			Map<String, SortedSet<String>> map_information) {
		initialize();
		SortedSet<String> set_001 = map_information.get("���Ϲ�����"); // ��ȡ���ϵĻ�����ǿ������,��������
		SortedSet<String> set_002 = map_information.get("���¹�����"); // ��ȡ���µĻ�����ǿ������,��������
		String schema = forwork.getSchema(); // �����ļ���
		String table_name = sheet_name; // ���ر���
		String sql_moban_001 = "select sum(ģ�����_m2_) from " + schema + "."
				+ table_name + ";"; // ��ȡ����ǽģ�������Ϣ
		String sql_moban_002 = "select sum(ģ�����_m2_) from " + schema + "."
				+ table_name + " where " + " ¥��" + " not like " + "'" + "_-%"
				+ "'" + " and ¥��" + "<>" + "'" + "������" + "'"; // ���ϵ�ģ�����
		String sql_moban_003 = "select sum(ģ�����_m2_) from " + schema + "."
				+ table_name + " where " + " ¥��" + "  like " + "'" + "_-%"
				+ "'" + " or ¥��" + "=" + "'" + "������" + "'"; // ���µ�ģ�����
		String sql_qtq_001;
		try {
			ResultSet res = statement.executeQuery(sql_moban_001);
			while (res.next()) {
				float s = res.getFloat(1);
				map_.put("ģ�����", String.valueOf(s)); // ��ȡǽ������ģ�����
			}
			ResultSet res_001 = statement.executeQuery(sql_moban_002);
			while (res_001.next()) {
				float f = res_001.getFloat(1);
				map_.put("����ģ�����", String.valueOf(f)); // ��ȡǽ���������ģ�����
			}
			ResultSet res_002 = statement.executeQuery(sql_moban_003);
			while (res_002.next()) {
				float f1 = res_002.getFloat(1);
				map_.put("����ģ�����", String.valueOf(f1)); // ��ȡǽ���������ģ�����
			}
			// ���»������ȼ�----
			// "select sum(���_m3_) from "+schema+"."+table_name+" where "+
			// " (¥��"+" like "+"'"+"_-%"+"'"+" or ¥��"+" ="+"'"+"������"+"') and "+" ģ�����_m2_ =0"+" and "+" �������ȼ� ="+"'"+"C"+String.valueOf(set_001);
			// ���ϻ������ȼ�----
			// "select sum(���_m3_) from "+schema+"."+table_name+" where "+
			// " (¥��"+" not like "+"'"+"_-%"+"'"+" and ¥��"+" <>"+"'"+"������"+"') and "+" ģ�����_m2_ =0"+" and "+" �������ȼ� ="+"'"+"C"+String.valueOf(set_002);
			for (String s1 : set_001) {
				String sql = "select sum(���_m3_) from " + schema + "."
						+ table_name + " where " + " (¥��" + " like " + "'"
						+ "_-%" + "'" + " or ¥��" + " =" + "'" + "������"
						+ "') and " + " ģ�����_m2_ =0" + " and " + " �������ȼ� ="
						+ "'" + "C" + s1 + "' ;";
				System.out.println(sql);
				ResultSet res_003 = statement.executeQuery(sql);
				while (res_003.next()) {
					map_.put("����C" + s1, String.valueOf(res_003.getFloat(1)));
				}
			} // ��ȡ���»���������ȼ�������
			for (String s2 : set_002) {
				String sql = "select sum(���_m3_) from " + schema + "."
						+ table_name + " where " + " (¥��" + " not like " + "'"
						+ "_-%" + "'" + " and ¥��" + " <>" + "'" + "������"
						+ "') and " + " ģ�����_m2_ =0" + " and " + " �������ȼ� ="
						+ "'" + "C" + s2 + "' ;";
				System.out.println(sql);
				ResultSet res_004 = statement.executeQuery(sql);
				while (res_004.next()) {
					map_.put("����C" + s2, String.valueOf(res_004.getFloat(1)));
				}
			} // ��ȡ���ϻ�����ǽ����ȼ�������
			int c = 100;
			for (int j = 0; j < 6; j++) {
				int f = c + j * 50;
				sql_qtq_001 = "select sum(���_m3_) from " + schema + "."
						+ table_name + " where " + " ģ�����_m2_ =0 and"
						+ " �������� like " + "'%" + f + "%'" + " and " + " (¥��"
						+ " not like " + "'" + "_-%" + "'" + " and ¥��" + " <>"
						+ "'" + "������" + "');";
				ResultSet res_003 = statement.executeQuery(sql_qtq_001);
				System.out.println(sql_qtq_001);
				while (res_003.next()) {
					map_.put("��������ǽ" + f, String.valueOf(res_003.getFloat(1)));
				} // ��ȡ����������ǽ�Ĺ�����
				sql_qtq_001 = "select sum(���_m3_) from " + schema + "."
						+ table_name + " where " + " ģ�����_m2_ =0 and"
						+ " �������� like " + "'%" + f + "%'" + " and " + " (¥��"
						+ " like " + "'" + "_-%" + "'" + " or ¥��" + " =" + "'"
						+ "������" + "');";
				System.out.println(sql_qtq_001);
				ResultSet res_004 = statement.executeQuery(sql_qtq_001);
				while (res_004.next()) {
					map_.put("��������ǽ" + f, String.valueOf(res_004.getFloat(1)));
				} // ��ȡ����������ǽ�Ĺ�����
			}
			return map_;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// ��ȡ��������
	public void getC_2(ForWork forwork,
			Map<String, SortedSet<String>> map_information) {
		initialize();
		SortedSet<String> set_001 = map_information.get("���Ϲ�����"); // ��ȡ���ϵ���������ǿ������,��������
		SortedSet<String> set_002 = map_information.get("���¹�����"); // ��ȡ���µ���������ǿ������,��������
		String schema = forwork.getSchema();
		String table_name = sheet_name;
		String moban = "select sum(ģ�����_m2_) from " + schema + "." + table_name
				+ " where ¥��" + " like " + "'" + "_-%" + "'" + " and ¥��" + " ="
				+ "'" + "������" + "';";
		ResultSet res;
		try {
			res = statement.executeQuery(moban);
			while (res.next()) {
				map_.put("������ģ��", String.valueOf(res.getFloat(1)));
			}
			for (String s : set_001) {
				String s_name = "select sum(���_m3_) from " + schema + "."
						+ table_name + " where ¥��" + " like " + "'" + "_-%"
						+ "'" + " and ¥��" + " =" + "'" + "������" + "' and "
						+ " �������ȼ� =" + "'" + "C" + s + "' ;";
				// ���¹���������ȡ
				ResultSet res_002 = statement.executeQuery(s_name);
				while (res.next()) {
					map_.put("������������", String.valueOf(res_002.getFloat(1)));
				}
			}
			for (String s1 : set_002) {
				String s_name = "select sum(���_m3_) from " + schema + "."
						+ table_name + " where ¥��" + " not like " + "'" + "_-%"
						+ "'" + " and ¥��" + "<>" + "'" + "������" + "' and "
						+ " �������ȼ� =" + "'" + "C" + s1 + "' ;";
				ResultSet res_003 = statement.executeQuery(s_name);
				while (res.next()) {
					map_.put("������������", String.valueOf(res_003.getFloat(1)));
				}
				// ���Ϲ���������ȡ
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// ��ȡ��������
	/**
	 * @param forwork
	 * @param map_infromation
	 */
	public void getC_3(ForWork forwork,
			Map<String, SortedSet<String>> map_information) {
		initialize();
		try {
			String schema = forwork.getSchema();
			String table_name = sheet_name;
			SortedSet<String> set_001 = map_information.get("���¹�����");
			SortedSet<String> set_002 = map_information.get("���Ϲ�����");
			for (String s : set_001) {
				String sql = "select sum(���_m3_) from " + schema + "."
						+ table_name + " where ¥��" + " like " + "'" + "_-%"
						+ "'" + " and ¥��" + " =" + "'" + "������"
						+ "' and ������ǿ�� =" + "'C" + s + "';"; // ���¹�����ѡ��
				ResultSet res = statement.executeQuery(sql);
				while (res.next()) {
					map_.put("������C" + s, String.valueOf(res.getFloat(1)));
				}
			}
			for (String s1 : set_002) {
				String sql_001 = "select sum(���_m3_) from " + schema + "."
						+ table_name + " where ¥��" + " not like " + "'" + "_-%"
						+ "'" + " and ¥��" + "<>" + "'" + "������" + "' and "
						+ " �������ȼ� =" + "'" + "C" + s1 + "' ;";
				ResultSet res = statement.executeQuery(sql_001);
				while (res.next()) {
					map_.put("������C" + s1, String.valueOf(res.getFloat(1)));
				}
			}
			String sql_003 = "select sum(ģ�����_m2_) from " + schema + "."
					+ table_name + ";";
			ResultSet res = statement.executeQuery(sql_003);
			while (res.next()) {
				map_.put("��ģ��", String.valueOf(res.getFloat(1)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// ��ȡ�幤����
	/**
	 * @param forwork
	 */
	public void getC_4(ForWork forwork,
			Map<String, SortedSet<String>> map_information) {
		initialize();
		String schema = forwork.getSchema();
		String table_name = sheet_name;
		SortedSet<String> set_001 = map_information.get("���Ϲ�����");
		SortedSet<String> set_002 = map_information.get("���¹�����");
		try {
			// ��ȡ���ϵİ�Ĺ�����
			for (String s : set_001) {
				String sql_001 = "select sum(���_m3_) from " + schema + "."
						+ table_name + " where ������ǿ��=" + "'C" + s + "' and "
						+ " ¥��" + " not like " + "'" + "_-%" + "'" + " and ¥��"
						+ "<>" + "'" + "������;";
				ResultSet res = statement.executeQuery(sql_001);
				while (res.next()) {
					map_.put("���ϰ�C" + s, String.valueOf(res.getFloat(1)));
				}
			}
			// ��ȡ���°�Ĺ�����
			for (String s : set_002) {
				String sql_002 = "select sum(���_m3_) from " + schema + "."
						+ table_name + " where ������ǿ��=" + "'C" + s + "' and "
						+ " ¥��" + " like " + "'" + "_-%" + "'" + " and ¥��"
						+ " =" + "'" + "������;";
				ResultSet res = statement.executeQuery(sql_002);
				while (res.next()) {
					map_.put("���°�C" + s, String.valueOf(res.getFloat(1)));
				}
			}
			// ��ȡ���ģ�幤����
			String sql_003 = "select sum(����ģ�����_m2_)+sum(����ģ�����_m2_) from "
					+ schema + "." + table_name + ";";
			ResultSet res = statement.executeQuery(sql_003);
			while (res.next()) {
				map_.put("��ģ�����", String.valueOf(res.getFloat(1)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ��ȡ����������
	public void getC_5(ForWork forwork,
			Map<String, SortedSet<String>> map_information) {
		initialize();
		String schema = forwork.getSchema();
		String table_name = sheet_name;
		SortedSet<String> set_001 = map_information.get("���¹�����");
		SortedSet<String> set_002 = map_information.get("���Ϲ�����");
		try {
			// ��ȡ���¹����Ĺ�����
			for (String s : set_001) {
				String sql_001 = "Select sum(���_m3_) from " + schema + "."
						+ table_name + " where ¥��" + " like " + "'" + "_-%"
						+ "'" + " and ¥��" + " =" + "'" + "������;";
				ResultSet res = statement.executeQuery(sql_001);
				while (res.next()) {
					map_.put("���¹���C" + s, String.valueOf(res.getFloat(1)));
				}
			}
			// ��ȡ���Ϲ����Ĺ�����
			for (String s : set_002) {
				String sql_002 = "select sum(���_m3_) from " + schema + "."
						+ table_name + " where ¥��" + " not like " + "'" + "_-%"
						+ "'" + " and ¥��" + "<>" + "'" + "������;";
				ResultSet res = statement.executeQuery(sql_002);
				while (res.next()) {
					map_.put("���Ϲ���C" + s, String.valueOf(res.getFloat(1)));
				}
			}
			// ��ȡ������ģ�幤����--m2
			String sql_003 = "select sum(ģ�����_m2_) from " + schema + "."
					+ table_name + ";";
			ResultSet res = statement.executeQuery(sql_003);
			while (res.next()) {
				map_.put("����ģ��", String.valueOf(res.getFloat(1)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// ��ȡ�����Ĺ�����
	public void getC_6(ForWork forwork,
			Map<String, SortedSet<String>> map_information) {
		initialize();
		String table_name = sheet_name;
		String schema = forwork.getSchema();
		SortedSet<String> s_001 = map_information.get("���¹�����");
		SortedSet<String> s_002 = map_information.get("���Ϲ�����");
		try {
			// ��ȡ������ģ�����
			String sql_001 = "select sum(ģ�����_m2_) from " + schema + "."
					+ table_name + ";";
			ResultSet res_001 = statement.executeQuery(sql_001);
			while (res_001.next()) {
				map_.put("����ģ�����", String.valueOf(res_001.getFloat(1)));
			}
			// ��ȡ���������Ĺ�����
			for (String s : s_001) {
				String sql = "select sum(���_m3_) from " + schema + "."
						+ table_name + " where ������ǿ��=" + "'C" + s + "' and "
						+ " ¥��" + " like " + "'" + "_-%" + "'" + " and ¥��"
						+ " =" + "'" + "������;";
				ResultSet res = statement.executeQuery(sql);
				while (res.next()) {
					map_.put("��������C" + s, String.valueOf(res.getFloat(1)));
				}
			}
			// ��ȡ���������Ĺ�����
			for (String s : s_002) {
				String sql = "select sum(���_m3_) from " + schema + "."
						+ table_name + " where ������ǿ��=" + "'C" + s + "' and "
						+ " ¥��" + " not like " + "'" + "_-%" + "'" + " and ¥��"
						+ "<>" + "'" + "������;";
				ResultSet res = statement.executeQuery(sql);
				while (res.next()) {
					map_.put("��������C" + s, String.valueOf(res.getFloat(1)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//��ȡ���幤�����������޻�����ǿ�ȵȼ�����
	public void getC_7(ForWork forwork){
		initialize();
		String table_name = sheet_name;
		String schema = forwork.getSchema();
		try{
			String sql="select sum(ģ�����_m2_) from "+schema+"."+table_name+";";
			ResultSet res=statement.executeQuery(sql);
			while(res.next()){
				map_.put("����ģ�����", String.valueOf(res.getFloat(1)));
			}
			//��ȡ�����������
				String sql_001="select sum(���_m3_) from "+schema+"."+table_name+" where ¥��" + " like " + "'" + "_-%" + "'" + " or ¥��"
						+ " =" + "'" + "������;";
				ResultSet res_001=statement.executeQuery(sql_001);
				while(res_001.next()){
					map_.put("�����������",	 String.valueOf(res_001.getFloat(1)));
				}
				//��ȡ�����������
				String sql_002="select sum(���_m3_) from "+schema+"."+table_name+" where ¥��" + " not like " + "'" + "_-%" + "'" + " and ¥��"
						+ "<>" + "'" + "������;";
				ResultSet res_002=statement.executeQuery(sql_002);
				while(res_002.next()){
					map_.put("�����������", String.valueOf(res_002.getFloat(1)));
				}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//��ȡ�������Ĺ�����
	public void getC_8(ForWork forwork){
		initialize();
		String schema=forwork.getSchema();
		String table_name=sheet_name;
		try{
			String sql_001="select sum(ģ�����_m2_) from "+schema+"."+table_name+";";	//��ȡ����ģ�����
			ResultSet res=statement.executeQuery(sql_001);
			while(res.next()){
				map_.put("������ģ�����", String.valueOf(res.getFloat(1)));
			}
			String sql_002="select sum(���_m3_) from "+schema+"."+table_name+" where ¥��" + " like " + "'" + "_-%" + "'" + "or ¥��"
						+ " =" + "'" + "������;";	//��ȡ���¹������Ĺ�����
			ResultSet res_001=statement.executeQuery(sql_002);
			while(res_001.next()){
				map_.put("���¹�����������", String.valueOf(res_001.getFloat(1)));
			}
			String sql_003="select sum(���_m3_) from "+schema+"."+table_name+" where ¥��" + " not like " + "'" + "_-%" + "'" + " and ¥��"
						+ "<>" + "'" + "������;";	//��ȡ���Ϲ������Ĺ�����
			ResultSet res_002=statement.executeQuery(sql_003);
			while(res_002.next()){
				map_.put("���Ϲ�����������", String.valueOf(res_002.getFloat(1)));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//��ȡ���������Ĺ�����--���¹�����
	public void getC_9(ForWork forwork){
		initialize();
		String schema=forwork.getSchema();
		String table_name=sheet_name;
		try{
		String sql="select sum(ģ�����_m2_) from "+schema+"."+table_name+" ;";
		ResultSet res=statement.executeQuery(sql);
			while(res.next()){
				map_.put("���¶�������ģ�����", String.valueOf(res.getFloat(1)));	//����������ģ�����
			}
		String sql_001="select sum(���_m3_) from "+schema+"."+table_name+";";
		ResultSet res_001=statement.executeQuery(sql_001);
		while(res_001.next()){
			map_.put("���¶�������������", String.valueOf(res_001.getFloat(1)));	//���������Ļ�����������
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//��ȡ��������Ĺ�����
	public void getC_10(ForWork forwork){
		initialize();
		String schema=forwork.getSchema();
		String table_name=sheet_name;
		try{
			String sql="select sum(ģ�����_m2_) from "+schema+"."+table_name;
			ResultSet res=statement.executeQuery(sql);
			while(res.next()){
				map_.put("�������ģ��",String.valueOf(res.getFloat(1)));
			}
			String sql_001="select sum(���_m3_) from "+schema+"."+table_name;
			ResultSet res_001=statement.executeQuery(sql_001);
			while(res_001.next()){
				map_.put("�������������", String.valueOf(res_001.getFloat(1)));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//��ȡװ�޹�����
	public void getC_11(ForWork forwork){
		initialize();
		
		
		
		
	}
	// ��ʼ����������
	private void initialize() {
		if (null == statement) {
			this.getStatement();
		}
		if (null == map_) {
			map_ = new HashMap<String, String>();
		}
	}
}