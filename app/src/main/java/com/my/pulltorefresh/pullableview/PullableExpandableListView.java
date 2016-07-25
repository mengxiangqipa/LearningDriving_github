package com.my.pulltorefresh.pullableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

public class PullableExpandableListView extends ExpandableListView implements
		Pullable {
	boolean canPullUp = true;
	boolean canPullDown = true;
	public PullableExpandableListView(Context context) {
		super(context);
	}

	public PullableExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullableExpandableListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	/**
	 * 手动设置是否能上拉
	 *
	 * @param canPullUp
	 */
	public void setCanPullUp(boolean canPullUp) {
		this.canPullUp = canPullUp;
	}

	/**
	 * 手动设置是否能下拉
	 *
	 * @param canPullDown
	 */
	public void setCanPullDown(boolean canPullDown) {
		this.canPullDown = canPullDown;
	}
	@Override
	public boolean canPullDown() {
		if (!canPullDown) {
			return false;
		}
		try {
			if (getCount() == 0) {
				// 没有item的时候也可以下拉刷新
				return true;
			} else if (getFirstVisiblePosition() == 0
					&& getChildAt(0).getTop() >= 0) {
				// 滑到顶部了
				return true;
			} else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean canPullUp() {
		if (!canPullUp) {
			return false;
		}
		try {
			if (getCount() == 0) {
				// 没有item的时候也可以上拉加载
				return true;
			} else if (getLastVisiblePosition() == (getCount() - 1)) {
				// 滑到底部了
				if (getChildAt(getLastVisiblePosition()
						- getFirstVisiblePosition()) != null
						&& getChildAt(
								getLastVisiblePosition()
										- getFirstVisiblePosition())
								.getBottom() <= getMeasuredHeight())
					return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}
