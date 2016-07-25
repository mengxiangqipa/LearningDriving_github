package com.my.pulltorefresh.fragment;

import java.util.ArrayList;
import java.util.List;

public class PackageOperateUtils {
	/**
	 * <pre>
	 * 数据封装
	 * 将一种list转化成另一种list
	 * @param data
	 * @return
	 * </pre>
	 */
	public static List<ListItemPackage> capsulationList(List data, int lenght) {
		List<ListItemPackage> list = new ArrayList<ListItemPackage>();// 箱子
		int len = data.size();
		int count = len / lenght;
		int i = 0;
		for (i = 0; i < count; i++) {
			ListItemPackage p = new ListItemPackage();
			p.packageList = data.subList(lenght * i, lenght * (i + 1));
			list.add(p);
		}
		if ((len % lenght == 0 ? 0 : 1) == 1) {
			ListItemPackage p = new ListItemPackage();
			p.packageList = data.subList(lenght * i, len);
			list.add(p);
		}
		return list;
	}

	/**
	 * <pre>
	 * 数据分割
	 * 将list分割成原始数据
	 * @param data
	 * @return
	 * </pre>
	 */
	public static List detachList(List<ListItemPackage> data) {
		int len = data.size();
		List allListitems = new ArrayList();
		for (int i = 0; i < len; i++) {
			allListitems.addAll(data.get(i).packageList);
		}
		return allListitems;
	}
}
