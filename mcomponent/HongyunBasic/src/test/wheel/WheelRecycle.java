package test.wheel;

import java.util.LinkedList;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Recycle stores wheel items to reuse.
 */
public class WheelRecycle {
	private final String TAG = this.getClass().getSimpleName();
	// Cached items
	private List<View> items;

	// Cached empty items
	private List<View> emptyItems;

	// Wheel view
	private WheelView wheel;

	/**
	 * Constructor
	 * 
	 * @param wheel
	 *            the wheel view
	 */
	public WheelRecycle(WheelView wheel) {
		this.wheel = wheel;
	}

	/**
	 * Recycles items from specified layout. There are saved only items not
	 * included to specified range. All the cached items are removed from
	 * original layout.
	 * 
	 * @param layout
	 *            the layout containing items to be cached
	 * @param firstItem
	 *            the number of first item in layout
	 * @param range
	 *            the range of current wheel items
	 * @return the new value of first item number
	 */
	public int recycleItems(LinearLayout layout, int firstItem, ItemsRange range) {
		Log.d(TAG, "recycleItems	" + "firstItem=" + firstItem + ",range="
				+ range + ", layout.getChildCount()=" + layout.getChildCount());
		int index = firstItem;
		int forloop = 0;
		for (int i = 0; i < layout.getChildCount();) {
			Log.d(TAG, "recycleItems	for loop" + forloop + ":index=" + index
					+ ",i=" + i + ",firstItem=" + firstItem);
			if (!range.contains(index)) {
				Log.d(TAG, "recycleItems	!range.contains(index)");
				recycleView(layout.getChildAt(i), index);
				layout.removeViewAt(i);
				if (i == 0) { // first item
					firstItem++;
					Log.d(TAG, "recycleItems	!!!! firstItem++ then firstItem="+firstItem);
				}
			} else {
				i++; // go to next item
			}
			index++;
			forloop++;
		}
		return firstItem;
	}

	/**
	 * Adds view to cache. Determines view type (item view or empty one) by
	 * index.
	 * 
	 * @param view
	 *            the view to be cached
	 * @param index
	 *            the index of view
	 */
	private void recycleView(View view, int index) {
		int count = wheel.getViewAdapter().getItemsCount();

		if ((index < 0 || index >= count) && !wheel.isCyclic()) {
			// empty view
			if(view instanceof TextView){
				Log.d(TAG, "recycleView	" + "view.getText()=" +((TextView)view).getText()+
						"index=" + index + ",add to emptyItems");
			}
			emptyItems = addView(view, emptyItems);
		} else {
			while (index < 0) {
				index = count + index;
			}
			index %= count;
			
			if(view instanceof TextView){
				Log.d(TAG, "recycleView	" + "view.getText()=" +((TextView)view).getText()+
						",index=" + index + ",add to cache");
			}
			items = addView(view, items);
		}
	}

	/**
	 * Gets item view
	 * 
	 * @return the cached view
	 */
	public View getItem() {
		return getCachedView(items);
	}

	/**
	 * Gets empty item view
	 * 
	 * @return the cached empty view
	 */
	public View getEmptyItem() {
		return getCachedView(emptyItems);
	}

	/**
	 * Clears all views
	 */
	public void clearAll() {
		if (items != null) {
			items.clear();
		}
		if (emptyItems != null) {
			emptyItems.clear();
		}
	}

	/**
	 * Adds view to specified cache. Creates a cache list if it is null.
	 * 
	 * @param view
	 *            the view to be cached
	 * @param cache
	 *            the cache list
	 * @return the cache list
	 */
	private List<View> addView(View view, List<View> cache) {
		if (cache == null) {
			cache = new LinkedList<View>();
		}

		cache.add(view);
		return cache;
	}

	/**
	 * Gets view from specified cache.
	 * 
	 * @param cache
	 *            the cache
	 * @return the first view from cache.
	 */
	private View getCachedView(List<View> cache) {
		if (cache != null && cache.size() > 0) {
			View view = cache.get(0);
			cache.remove(0);
			return view;
		}
		return null;
	}

}
