package com.my.other;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import java.util.ArrayList;

public class GuideFragmentPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> listFragments;

	public GuideFragmentPagerAdapter(FragmentManager fm,
			ArrayList<Fragment> listFragments) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.listFragments = listFragments;
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		return null == listFragments ? null : listFragments.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return null == listFragments ? 0 : listFragments.size();
	}

	@Override
	public void setPrimaryItem(View container, int position, Object object) {
		// TODO Auto-generated method stub
		super.setPrimaryItem(container, position, object);
	}
}
