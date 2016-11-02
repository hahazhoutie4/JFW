package com.zhoutong.jxl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;

public class MainInformation {
	/**
	 * @author hahazhoutie4
	 * @website cnblogs.com/hahazhoutie4-blogs/
	 */
	public MainInformation() {
	}
	private List<String> floor_information;// ¥���
	private List<String> names; // ����������
	private Map<Integer,Cell[]>	map=new HashMap<Integer,Cell[]>();	//���������
		/**
		 * @return	�������б�ͷ
		 */
	public List<String> getNames() {
		return names;
	}
	public Map<Integer, Cell[]> getMap() {
			return map;
		}

		public void setMap(Map<Integer, Cell[]> map) {
			this.map = map;
		}

	public void setNames(List<String> names) {
		this.names = names;
	}
	/**
	 * 
	 * @return ��������¥����Ϣ
	 */
	public List<String> getFloor_information() {
		return floor_information;
	}

	public void setFloor_information(List<String> floor_information) {
		this.floor_information = floor_information;
	}
}