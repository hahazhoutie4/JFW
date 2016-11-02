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
	 * @return 返回工程量表格Map对象
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
			if (sheet_name.equals("墙") || sheet_name.contains("柱")
					|| sheet_name.contains("梁") || sheet_name.contains("板")) {
				sql2 = sql2 + ",混凝土等级  varchar(45)";
			}
			sql2 = sql2 + ");";
			System.out.println(sql2);
			statement.execute(sql2);
			// 建立表格完成
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}
	}

	public void insertData(ForWork forwork, boolean rewrite) {
		// Cell[3].getContents().equals("小计")||Cell[3].getContents().equals("合计")为合计数列行
		//楼梯   rewrite 为false;
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
				System.out.println("删除表格所有数据");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Map<Integer, Cell[]> map_ = map.get(sheet_name).getMap();
		int size = map_.size();
		System.out.println("size   --"+size);
		int id = 0;
		for (int j = 3; j < size; j++) {
			Cell[] c = map_.get(j); // 某一行的数据c
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
				if (sheet_name.equals("墙") || sheet_name.contains("柱")
						|| sheet_name.contains("梁") || sheet_name.contains("板")) {
					sql = sql + ",null";
				}
				sql = sql + ");";
				try {
					statement.addBatch(sql);
					statement.executeBatch();
				} catch (SQLException e) {
					e.printStackTrace();
				} // 此处执行插入语句即可
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

	// 加入混凝土强度列，通过给定的两个key-value值
	public void insert_other_information(ForWork forwork,String conrete_information, String floor_information) {
		String db_name = forwork.getSchema(); // 数据库名称
		String table_name = sheet_name; // 获取表格名称
		String sql = "update " + db_name + "." + table_name + " set 混凝土等级 "
				+ "=" + "'" + conrete_information + "'" + " where 楼层=" + "'"
				+ floor_information + "';";
		try {
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("sql语句" + sql);
	}

	// 加入混凝土强度列数，通过properties文件
	public void insert_other_information(ForWork forwork, Properties properties) {
		String db_name = forwork.getSchema(); // 数据库名称
		String table_name = sheet_name; // 获取表格名称
		Set<Object> set = properties.keySet();
		for (Object o : set) {
			String floor_information = (String) o;
			String concrete_information = (String) properties
					.get(floor_information);
			String sql = "update " + db_name + "." + table_name + " set 混凝土等级 "
					+ "=" + "'" + concrete_information + "'" + " where 楼层="
					+ "'" + floor_information + "';";
			System.out.println(sql);
			try {
				statement.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// 加入混凝土强度列数，通过xls文件(必须是excel97)
	public void insert_other_information(ForWork forwork,
			Map<String, String> information) {
		String db_name = forwork.getSchema(); // 数据库名称
		String table_name = sheet_name; // 获取表格名称
		Set<String> set = information.keySet();
		for (String o : set) {
			String concrete_information = (String) information.get(o);
			String sql = "update " + db_name + "." + table_name + " set 混凝土等级 "
					+ "=" + "'" + concrete_information + "'" + " where 楼层="
					+ "'" + o + "';";
			System.out.println(sql);
			try {
				statement.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// 提取墙工程量
	/**
	 * @param forwork
	 *            当前表格信息
	 * @param map
	 *            当前混凝土强度等级信息,"地上"---SortedSet1 ,"地下"----SortedSet2
	 */
	public Map<String, String> getC_1(ForWork forwork,
			Map<String, SortedSet<String>> map_information) {
		initialize();
		SortedSet<String> set_001 = map_information.get("地上工程量"); // 获取地上的混凝土强度序列,方便提量
		SortedSet<String> set_002 = map_information.get("地下工程量"); // 获取地下的混凝土强度序列,方便提量
		String schema = forwork.getSchema(); // 返回文件名
		String table_name = sheet_name; // 返回表名
		String sql_moban_001 = "select sum(模板面积_m2_) from " + schema + "."
				+ table_name + ";"; // 获取剪力墙模板面积信息
		String sql_moban_002 = "select sum(模板面积_m2_) from " + schema + "."
				+ table_name + " where " + " 楼层" + " not like " + "'" + "_-%"
				+ "'" + " and 楼层" + "<>" + "'" + "基础层" + "'"; // 地上的模板面积
		String sql_moban_003 = "select sum(模板面积_m2_) from " + schema + "."
				+ table_name + " where " + " 楼层" + "  like " + "'" + "_-%"
				+ "'" + " or 楼层" + "=" + "'" + "基础层" + "'"; // 地下的模板面积
		String sql_qtq_001;
		try {
			ResultSet res = statement.executeQuery(sql_moban_001);
			while (res.next()) {
				float s = res.getFloat(1);
				map_.put("模板面积", String.valueOf(s)); // 获取墙面所有模板面积
			}
			ResultSet res_001 = statement.executeQuery(sql_moban_002);
			while (res_001.next()) {
				float f = res_001.getFloat(1);
				map_.put("地上模板面积", String.valueOf(f)); // 获取墙面地上所有模板面积
			}
			ResultSet res_002 = statement.executeQuery(sql_moban_003);
			while (res_002.next()) {
				float f1 = res_002.getFloat(1);
				map_.put("地下模板面积", String.valueOf(f1)); // 获取墙面地下所有模板面积
			}
			// 地下混凝土等级----
			// "select sum(体积_m3_) from "+schema+"."+table_name+" where "+
			// " (楼层"+" like "+"'"+"_-%"+"'"+" or 楼层"+" ="+"'"+"基础层"+"') and "+" 模板面积_m2_ =0"+" and "+" 混凝土等级 ="+"'"+"C"+String.valueOf(set_001);
			// 地上混凝土等级----
			// "select sum(体积_m3_) from "+schema+"."+table_name+" where "+
			// " (楼层"+" not like "+"'"+"_-%"+"'"+" and 楼层"+" <>"+"'"+"基础层"+"') and "+" 模板面积_m2_ =0"+" and "+" 混凝土等级 ="+"'"+"C"+String.valueOf(set_002);
			for (String s1 : set_001) {
				String sql = "select sum(体积_m3_) from " + schema + "."
						+ table_name + " where " + " (楼层" + " like " + "'"
						+ "_-%" + "'" + " or 楼层" + " =" + "'" + "基础层"
						+ "') and " + " 模板面积_m2_ =0" + " and " + " 混凝土等级 ="
						+ "'" + "C" + s1 + "' ;";
				System.out.println(sql);
				ResultSet res_003 = statement.executeQuery(sql);
				while (res_003.next()) {
					map_.put("地下C" + s1, String.valueOf(res_003.getFloat(1)));
				}
			} // 获取地下混凝土分类等级工程量
			for (String s2 : set_002) {
				String sql = "select sum(体积_m3_) from " + schema + "."
						+ table_name + " where " + " (楼层" + " not like " + "'"
						+ "_-%" + "'" + " and 楼层" + " <>" + "'" + "基础层"
						+ "') and " + " 模板面积_m2_ =0" + " and " + " 混凝土等级 ="
						+ "'" + "C" + s2 + "' ;";
				System.out.println(sql);
				ResultSet res_004 = statement.executeQuery(sql);
				while (res_004.next()) {
					map_.put("地上C" + s2, String.valueOf(res_004.getFloat(1)));
				}
			} // 获取地上混凝土墙分类等级工程量
			int c = 100;
			for (int j = 0; j < 6; j++) {
				int f = c + j * 50;
				sql_qtq_001 = "select sum(体积_m3_) from " + schema + "."
						+ table_name + " where " + " 模板面积_m2_ =0 and"
						+ " 构件名称 like " + "'%" + f + "%'" + " and " + " (楼层"
						+ " not like " + "'" + "_-%" + "'" + " and 楼层" + " <>"
						+ "'" + "基础层" + "');";
				ResultSet res_003 = statement.executeQuery(sql_qtq_001);
				System.out.println(sql_qtq_001);
				while (res_003.next()) {
					map_.put("地上砌体墙" + f, String.valueOf(res_003.getFloat(1)));
				} // 获取到地上砌体墙的工程量
				sql_qtq_001 = "select sum(体积_m3_) from " + schema + "."
						+ table_name + " where " + " 模板面积_m2_ =0 and"
						+ " 构件名称 like " + "'%" + f + "%'" + " and " + " (楼层"
						+ " like " + "'" + "_-%" + "'" + " or 楼层" + " =" + "'"
						+ "基础层" + "');";
				System.out.println(sql_qtq_001);
				ResultSet res_004 = statement.executeQuery(sql_qtq_001);
				while (res_004.next()) {
					map_.put("地下砌体墙" + f, String.valueOf(res_004.getFloat(1)));
				} // 获取到地下砌体墙的工程量
			}
			return map_;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// 提取梁工程量
	public void getC_2(ForWork forwork,
			Map<String, SortedSet<String>> map_information) {
		initialize();
		SortedSet<String> set_001 = map_information.get("地上工程量"); // 获取地上的梁混凝土强度序列,方便提量
		SortedSet<String> set_002 = map_information.get("地下工程量"); // 获取地下的梁混凝土强度序列,方便提量
		String schema = forwork.getSchema();
		String table_name = sheet_name;
		String moban = "select sum(模板面积_m2_) from " + schema + "." + table_name
				+ " where 楼层" + " like " + "'" + "_-%" + "'" + " and 楼层" + " ="
				+ "'" + "基础层" + "';";
		ResultSet res;
		try {
			res = statement.executeQuery(moban);
			while (res.next()) {
				map_.put("地下梁模板", String.valueOf(res.getFloat(1)));
			}
			for (String s : set_001) {
				String s_name = "select sum(体积_m3_) from " + schema + "."
						+ table_name + " where 楼层" + " like " + "'" + "_-%"
						+ "'" + " and 楼层" + " =" + "'" + "基础层" + "' and "
						+ " 混凝土等级 =" + "'" + "C" + s + "' ;";
				// 地下工程量的提取
				ResultSet res_002 = statement.executeQuery(s_name);
				while (res.next()) {
					map_.put("地下梁工程量", String.valueOf(res_002.getFloat(1)));
				}
			}
			for (String s1 : set_002) {
				String s_name = "select sum(体积_m3_) from " + schema + "."
						+ table_name + " where 楼层" + " not like " + "'" + "_-%"
						+ "'" + " and 楼层" + "<>" + "'" + "基础层" + "' and "
						+ " 混凝土等级 =" + "'" + "C" + s1 + "' ;";
				ResultSet res_003 = statement.executeQuery(s_name);
				while (res.next()) {
					map_.put("地上梁工程量", String.valueOf(res_003.getFloat(1)));
				}
				// 地上工程量的提取
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 提取柱工程量
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
			SortedSet<String> set_001 = map_information.get("地下工程量");
			SortedSet<String> set_002 = map_information.get("地上工程量");
			for (String s : set_001) {
				String sql = "select sum(体积_m3_) from " + schema + "."
						+ table_name + " where 楼层" + " like " + "'" + "_-%"
						+ "'" + " and 楼层" + " =" + "'" + "基础层"
						+ "' and 混凝土强度 =" + "'C" + s + "';"; // 地下工程量选择
				ResultSet res = statement.executeQuery(sql);
				while (res.next()) {
					map_.put("地下柱C" + s, String.valueOf(res.getFloat(1)));
				}
			}
			for (String s1 : set_002) {
				String sql_001 = "select sum(体积_m3_) from " + schema + "."
						+ table_name + " where 楼层" + " not like " + "'" + "_-%"
						+ "'" + " and 楼层" + "<>" + "'" + "基础层" + "' and "
						+ " 混凝土等级 =" + "'" + "C" + s1 + "' ;";
				ResultSet res = statement.executeQuery(sql_001);
				while (res.next()) {
					map_.put("地上柱C" + s1, String.valueOf(res.getFloat(1)));
				}
			}
			String sql_003 = "select sum(模板面积_m2_) from " + schema + "."
					+ table_name + ";";
			ResultSet res = statement.executeQuery(sql_003);
			while (res.next()) {
				map_.put("柱模板", String.valueOf(res.getFloat(1)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 提取板工程量
	/**
	 * @param forwork
	 */
	public void getC_4(ForWork forwork,
			Map<String, SortedSet<String>> map_information) {
		initialize();
		String schema = forwork.getSchema();
		String table_name = sheet_name;
		SortedSet<String> set_001 = map_information.get("地上工程量");
		SortedSet<String> set_002 = map_information.get("地下工程量");
		try {
			// 获取地上的板的工程量
			for (String s : set_001) {
				String sql_001 = "select sum(体积_m3_) from " + schema + "."
						+ table_name + " where 混凝土强度=" + "'C" + s + "' and "
						+ " 楼层" + " not like " + "'" + "_-%" + "'" + " and 楼层"
						+ "<>" + "'" + "基础层;";
				ResultSet res = statement.executeQuery(sql_001);
				while (res.next()) {
					map_.put("地上板C" + s, String.valueOf(res.getFloat(1)));
				}
			}
			// 获取地下板的工程量
			for (String s : set_002) {
				String sql_002 = "select sum(体积_m3_) from " + schema + "."
						+ table_name + " where 混凝土强度=" + "'C" + s + "' and "
						+ " 楼层" + " like " + "'" + "_-%" + "'" + " and 楼层"
						+ " =" + "'" + "基础层;";
				ResultSet res = statement.executeQuery(sql_002);
				while (res.next()) {
					map_.put("地下板C" + s, String.valueOf(res.getFloat(1)));
				}
			}
			// 获取板的模板工程量
			String sql_003 = "select sum(底面模板面积_m2_)+sum(侧面模板面积_m2_) from "
					+ schema + "." + table_name + ";";
			ResultSet res = statement.executeQuery(sql_003);
			while (res.next()) {
				map_.put("板模板面积", String.valueOf(res.getFloat(1)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 提取过梁工程量
	public void getC_5(ForWork forwork,
			Map<String, SortedSet<String>> map_information) {
		initialize();
		String schema = forwork.getSchema();
		String table_name = sheet_name;
		SortedSet<String> set_001 = map_information.get("地下工程量");
		SortedSet<String> set_002 = map_information.get("地上工程量");
		try {
			// 获取地下过梁的工程量
			for (String s : set_001) {
				String sql_001 = "Select sum(体积_m3_) from " + schema + "."
						+ table_name + " where 楼层" + " like " + "'" + "_-%"
						+ "'" + " and 楼层" + " =" + "'" + "基础层;";
				ResultSet res = statement.executeQuery(sql_001);
				while (res.next()) {
					map_.put("地下过梁C" + s, String.valueOf(res.getFloat(1)));
				}
			}
			// 获取地上过梁的工程量
			for (String s : set_002) {
				String sql_002 = "select sum(体积_m3_) from " + schema + "."
						+ table_name + " where 楼层" + " not like " + "'" + "_-%"
						+ "'" + " and 楼层" + "<>" + "'" + "基础层;";
				ResultSet res = statement.executeQuery(sql_002);
				while (res.next()) {
					map_.put("地上过梁C" + s, String.valueOf(res.getFloat(1)));
				}
			}
			// 获取过梁的模板工程量--m2
			String sql_003 = "select sum(模板面积_m2_) from " + schema + "."
					+ table_name + ";";
			ResultSet res = statement.executeQuery(sql_003);
			while (res.next()) {
				map_.put("过梁模板", String.valueOf(res.getFloat(1)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 获取连梁的工程量
	public void getC_6(ForWork forwork,
			Map<String, SortedSet<String>> map_information) {
		initialize();
		String table_name = sheet_name;
		String schema = forwork.getSchema();
		SortedSet<String> s_001 = map_information.get("地下工程量");
		SortedSet<String> s_002 = map_information.get("地上工程量");
		try {
			// 获取连梁的模板面积
			String sql_001 = "select sum(模板面积_m2_) from " + schema + "."
					+ table_name + ";";
			ResultSet res_001 = statement.executeQuery(sql_001);
			while (res_001.next()) {
				map_.put("连梁模板面积", String.valueOf(res_001.getFloat(1)));
			}
			// 获取地下连梁的工程量
			for (String s : s_001) {
				String sql = "select sum(体积_m3_) from " + schema + "."
						+ table_name + " where 混凝土强度=" + "'C" + s + "' and "
						+ " 楼层" + " like " + "'" + "_-%" + "'" + " and 楼层"
						+ " =" + "'" + "基础层;";
				ResultSet res = statement.executeQuery(sql);
				while (res.next()) {
					map_.put("地下连梁C" + s, String.valueOf(res.getFloat(1)));
				}
			}
			// 获取地上连梁的工程量
			for (String s : s_002) {
				String sql = "select sum(体积_m3_) from " + schema + "."
						+ table_name + " where 混凝土强度=" + "'C" + s + "' and "
						+ " 楼层" + " not like " + "'" + "_-%" + "'" + " and 楼层"
						+ "<>" + "'" + "基础层;";
				ResultSet res = statement.executeQuery(sql);
				while (res.next()) {
					map_.put("地上连梁C" + s, String.valueOf(res.getFloat(1)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//获取栏板工程量，栏板无混凝土强度等级限制
	public void getC_7(ForWork forwork){
		initialize();
		String table_name = sheet_name;
		String schema = forwork.getSchema();
		try{
			String sql="select sum(模板面积_m2_) from "+schema+"."+table_name+";";
			ResultSet res=statement.executeQuery(sql);
			while(res.next()){
				map_.put("栏板模板面积", String.valueOf(res.getFloat(1)));
			}
			//获取地下栏板体积
				String sql_001="select sum(体积_m3_) from "+schema+"."+table_name+" where 楼层" + " like " + "'" + "_-%" + "'" + " or 楼层"
						+ " =" + "'" + "基础层;";
				ResultSet res_001=statement.executeQuery(sql_001);
				while(res_001.next()){
					map_.put("地下栏板体积",	 String.valueOf(res_001.getFloat(1)));
				}
				//获取地上栏板体积
				String sql_002="select sum(体积_m3_) from "+schema+"."+table_name+" where 楼层" + " not like " + "'" + "_-%" + "'" + " and 楼层"
						+ "<>" + "'" + "基础层;";
				ResultSet res_002=statement.executeQuery(sql_002);
				while(res_002.next()){
					map_.put("地上栏板体积", String.valueOf(res_002.getFloat(1)));
				}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//获取构造柱的工程量
	public void getC_8(ForWork forwork){
		initialize();
		String schema=forwork.getSchema();
		String table_name=sheet_name;
		try{
			String sql_001="select sum(模板面积_m2_) from "+schema+"."+table_name+";";	//获取过梁模板面积
			ResultSet res=statement.executeQuery(sql_001);
			while(res.next()){
				map_.put("构造柱模板面积", String.valueOf(res.getFloat(1)));
			}
			String sql_002="select sum(体积_m3_) from "+schema+"."+table_name+" where 楼层" + " like " + "'" + "_-%" + "'" + "or 楼层"
						+ " =" + "'" + "基础层;";	//获取地下构造柱的工程量
			ResultSet res_001=statement.executeQuery(sql_002);
			while(res_001.next()){
				map_.put("地下构造柱工程量", String.valueOf(res_001.getFloat(1)));
			}
			String sql_003="select sum(体积_m3_) from "+schema+"."+table_name+" where 楼层" + " not like " + "'" + "_-%" + "'" + " and 楼层"
						+ "<>" + "'" + "基础层;";	//获取地上构造柱的工程量
			ResultSet res_002=statement.executeQuery(sql_003);
			while(res_002.next()){
				map_.put("地上构造柱工程量", String.valueOf(res_002.getFloat(1)));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//获取独立基础的工程量--地下工程量
	public void getC_9(ForWork forwork){
		initialize();
		String schema=forwork.getSchema();
		String table_name=sheet_name;
		try{
		String sql="select sum(模板面积_m2_) from "+schema+"."+table_name+" ;";
		ResultSet res=statement.executeQuery(sql);
			while(res.next()){
				map_.put("地下独立基础模板面积", String.valueOf(res.getFloat(1)));	//独立基础的模板面积
			}
		String sql_001="select sum(体积_m3_) from "+schema+"."+table_name+";";
		ResultSet res_001=statement.executeQuery(sql_001);
		while(res_001.next()){
			map_.put("地下独立基础工程量", String.valueOf(res_001.getFloat(1)));	//独立基础的混凝土工程量
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//获取筏板基础的工程量
	public void getC_10(ForWork forwork){
		initialize();
		String schema=forwork.getSchema();
		String table_name=sheet_name;
		try{
			String sql="select sum(模板面积_m2_) from "+schema+"."+table_name;
			ResultSet res=statement.executeQuery(sql);
			while(res.next()){
				map_.put("筏板基础模板",String.valueOf(res.getFloat(1)));
			}
			String sql_001="select sum(体积_m3_) from "+schema+"."+table_name;
			ResultSet res_001=statement.executeQuery(sql_001);
			while(res_001.next()){
				map_.put("筏板基础工程量", String.valueOf(res_001.getFloat(1)));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//获取装修工程量
	public void getC_11(ForWork forwork){
		initialize();
		
		
		
		
	}
	// 初始化数据容器
	private void initialize() {
		if (null == statement) {
			this.getStatement();
		}
		if (null == map_) {
			map_ = new HashMap<String, String>();
		}
	}
}