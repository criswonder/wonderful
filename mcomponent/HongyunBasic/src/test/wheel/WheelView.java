
package test.wheel;

import java.util.LinkedList;
import java.util.List;

import test.wheel.adapter.WheelViewAdapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.example.hongyunbasic.R;


public class WheelView extends View {

	/** Top and bottom shadows colors */
	private static final int[] SHADOWS_COLORS = new int[] { 0xFF111111,
			0x00AAAAAA, 0x00AAAAAA };

	/** Top and bottom items offset (to hide that) */
	private static final int ITEM_OFFSET_PERCENT = 8;

	/** Left and right padding value */
	private static final int PADDING = 4;
	private int padding = PADDING;

	/** Default count of visible items */
	private static final int DEF_VISIBLE_ITEMS = 5;

	// Wheel Values
	private int currentItem = 0;
	
	// Count of visible items
	private int visibleItems = DEF_VISIBLE_ITEMS;
	
	// Item height
	//private int itemHeight = 0;
	// Item width
	private int itemWidth = 0;

	// Center Line
	private Drawable centerDrawable;

	// Shadows drawables
	//private GradientDrawable leftShadow;
	//private GradientDrawable rightShadow;

	// Scrolling
	private WheelScroller scroller;
    private boolean isScrollingPerformed; 
    private int scrollingOffset;

	// Cyclic
	boolean isCyclic = false;
	
	// Items layout
	private LinearLayout itemsLayout;
	
	// The number of first item in layout
	private int firstItem;

	// View adapter
	private WheelViewAdapter viewAdapter;
	
	// Recycle
	private WheelRecycle recycle = new WheelRecycle(this);

	// Listeners
	private List<OnWheelChangedListener> changingListeners = new LinkedList<OnWheelChangedListener>();
	private List<OnWheelScrollListener> scrollingListeners = new LinkedList<OnWheelScrollListener>();
    private List<OnWheelClickedListener> clickingListeners = new LinkedList<OnWheelClickedListener>();

	/**
	 * Constructor
	 */
	public WheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData(context);
	}

	/**
	 * Constructor
	 */
	public WheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context);
	}

	/**
	 * Constructor
	 */
	public WheelView(Context context) {
		super(context);
		initData(context);
	}
	
	/**
	 * Initializes class data
	 * @param context the context
	 */
	private void initData(Context context) {
	    scroller = new WheelScroller(getContext(), scrollingListener);
	    
	    float density = context.getResources().getDisplayMetrics().density;
	    padding = (int) (PADDING * density);
	}
	
	// Scrolling listener
	WheelScroller.ScrollingListener scrollingListener = new WheelScroller.ScrollingListener() {
        @Override
		public void onStarted() {
            isScrollingPerformed = true;
            notifyScrollingListenersAboutStart();
        }
        
        @Override
		public void onScroll(int distance) {
        	
        	Log.d("scr", "onScroll  " + "distance:" + distance);
            doScroll(distance);
            
//            int height = getHeight();
//            if (scrollingOffset > height) {
//                scrollingOffset = height;
//                scroller.stopScrolling();
//            } else if (scrollingOffset < -height) {
//                scrollingOffset = -height;
//                scroller.stopScrolling();
//            }
            int width = getWidth();
            if (scrollingOffset > width) {
                scrollingOffset = width;
                scroller.stopScrolling();
            } else if (scrollingOffset < -width) {
                scrollingOffset = -width;
                scroller.stopScrolling();
            }
        }
        
        @Override
		public void onFinished() {
            if (isScrollingPerformed) {
                notifyScrollingListenersAboutEnd();
                isScrollingPerformed = false;
            }
            
            scrollingOffset = 0;
            invalidate();
        }

        @Override
		public void onJustify() {
            if (Math.abs(scrollingOffset) > WheelScroller.MIN_DELTA_FOR_SCROLLING) {
                scroller.scroll(scrollingOffset, 0);
            }
        }
    };
	
	/**
	 * Set the the specified scrolling interpolator
	 * @param interpolator the interpolator
	 */
	public void setInterpolator(Interpolator interpolator) {
		scroller.setInterpolator(interpolator);
	}
	
	/**
	 * Gets count of visible items
	 * 
	 * @return the count of visible items
	 */
	public int getVisibleItems() {
		return visibleItems;
	}

	/**
	 * Sets the desired count of visible items.
	 * Actual amount of visible items depends on wheel layout parameters.
	 * To apply changes and rebuild view call measure(). 
	 * 
	 * @param count the desired count for visible items
	 */
	public void setVisibleItems(int count) {
		visibleItems = count;
	}

	/**
	 * Gets view adapter
	 * @return the view adapter
	 */
	public WheelViewAdapter getViewAdapter() {
		return viewAdapter;
	}

	// Adapter listener
    private DataSetObserver dataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            invalidateWheel(false);
        }

        @Override
        public void onInvalidated() {
            invalidateWheel(true);
        }
    };

	/**
	 * Sets view adapter. Usually new adapters contain different views, so
	 * it needs to rebuild view by calling measure().
	 *  
	 * @param viewAdapter the view adapter
	 */
	public void setViewAdapter(WheelViewAdapter viewAdapter) {
	    if (this.viewAdapter != null) {
	        this.viewAdapter.unregisterDataSetObserver(dataObserver);
	    }
        this.viewAdapter = viewAdapter;
        if (this.viewAdapter != null) {
            this.viewAdapter.registerDataSetObserver(dataObserver);
        }
        
        invalidateWheel(true);
	}
	
	/**
	 * Adds wheel changing listener
	 * @param listener the listener 
	 */
	public void addChangingListener(OnWheelChangedListener listener) {
		changingListeners.add(listener);
	}

	/**
	 * Removes wheel changing listener
	 * @param listener the listener
	 */
	public void removeChangingListener(OnWheelChangedListener listener) {
		changingListeners.remove(listener);
	}
	
	/**
	 * Notifies changing listeners
	 * @param oldValue the old wheel value
	 * @param newValue the new wheel value
	 */
	protected void notifyChangingListeners(int oldValue, int newValue) {
		for (OnWheelChangedListener listener : changingListeners) {
			listener.onChanged(this, oldValue, newValue);
		}
	}

	/**
	 * Adds wheel scrolling listener
	 * @param listener the listener 
	 */
	public void addScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.add(listener);
	}

	/**
	 * Removes wheel scrolling listener
	 * @param listener the listener
	 */
	public void removeScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.remove(listener);
	}
	
	/**
	 * Notifies listeners about starting scrolling
	 */
	protected void notifyScrollingListenersAboutStart() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingStarted(this);
		}
	}

	/**
	 * Notifies listeners about ending scrolling
	 */
	protected void notifyScrollingListenersAboutEnd() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingFinished(this);
		}
	}

    /**
     * Adds wheel clicking listener
     * @param listener the listener 
     */
    public void addClickingListener(OnWheelClickedListener listener) {
        clickingListeners.add(listener);
    }

    /**
     * Removes wheel clicking listener
     * @param listener the listener
     */
    public void removeClickingListener(OnWheelClickedListener listener) {
        clickingListeners.remove(listener);
    }
    
    /**
     * Notifies listeners about clicking
     */
    protected void notifyClickListenersAboutClick(int item) {
        for (OnWheelClickedListener listener : clickingListeners) {
            listener.onItemClicked(this, item);
        }
    }

	/**
	 * Gets current value
	 * 
	 * @return the current value
	 */
	public int getCurrentItem() {
		return currentItem;
	}

	/**
	 * Sets the current item. Does nothing when index is wrong.
	 * 
	 * @param index the item index
	 * @param animated the animation flag
	 */
	public void setCurrentItem(int index, boolean animated) {
		Log.d("scr", "setCurrentItem index=" + index);
		if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
			return; // throw?
		}
		
		int itemCount = viewAdapter.getItemsCount();
		if (index < 0 || index >= itemCount) {
			if (isCyclic) {
				while (index < 0) {
					index += itemCount;
				}
				index %= itemCount;
			} else{
				return; // throw?
			}
		}
		if (index != currentItem) {
			if (animated) {
			    int itemsToScroll = index - currentItem;
			    if (isCyclic) {
			        int scroll = itemCount + Math.min(index, currentItem) - Math.max(index, currentItem);
			        if (scroll < Math.abs(itemsToScroll)) {
			            itemsToScroll = itemsToScroll < 0 ? scroll : -scroll;
			        }
			    }
				scroll(itemsToScroll, 0);
			} else {
				scrollingOffset = 0;
			
				int old = currentItem;
				currentItem = index;
			
				notifyChangingListeners(old, currentItem);
			
				invalidate();
			}
		}
	}

	/**
	 * Sets the current item w/o animation. Does nothing when index is wrong.
	 * 
	 * @param index the item index
	 */
	public void setCurrentItem(int index) {
		setCurrentItem(index, false);
	}	
	
	/**
	 * Tests if wheel is cyclic. That means before the 1st item there is shown the last one
	 * @return true if wheel is cyclic
	 */
	public boolean isCyclic() {
		return isCyclic;
	}

	/**
	 * Set wheel cyclic flag
	 * @param isCyclic the flag to set
	 */
	public void setCyclic(boolean isCyclic) {
		this.isCyclic = isCyclic;
		invalidateWheel(false);
	}
	
	/**
	 * Invalidates wheel
	 * @param clearCaches if true then cached views will be clear
	 */
    public void invalidateWheel(boolean clearCaches) {
        if (clearCaches) {
            recycle.clearAll();
            if (itemsLayout != null) {
                itemsLayout.removeAllViews();
            }
            scrollingOffset = 0;
        } else if (itemsLayout != null) {
            // cache all items
	        recycle.recycleItems(itemsLayout, firstItem, new ItemsRange());         
        }
        
        invalidate();
	}

	/**
	 * Initializes resources
	 */
	private void initResourcesIfNecessary() {
		if (centerDrawable == null) {
			centerDrawable = getContext().getResources().getDrawable(R.drawable.cp_wheel_val);
		}

//		if (leftShadow == null) {
//			leftShadow = new GradientDrawable(Orientation.LEFT_RIGHT, SHADOWS_COLORS);
//		}
//
//		if (rightShadow == null) {
//			rightShadow = new GradientDrawable(Orientation.RIGHT_LEFT, SHADOWS_COLORS);
//		}

		setBackgroundResource(R.drawable.cp_wheel_bg);
	}
	
	/**
	 * Calculates desired height for layout
	 * 
	 * @param layout
	 *            the source layout
	 * @return the desired layout height
	 */
//	private int getDesiredHeight(LinearLayout layout) {
//		if (layout != null && layout.getChildAt(0) != null) {
//			itemHeight = layout.getChildAt(0).getMeasuredHeight();
//		}
//
//		int desired = itemHeight * visibleItems - itemHeight * ITEM_OFFSET_PERCENT / 50;
//
//		return Math.max(desired, getSuggestedMinimumHeight());
//	}

	
	private int getDesiredWidth(LinearLayout layout) {
		if (layout != null && layout.getChildAt(0) != null) {
			itemWidth = layout.getChildAt(0).getMeasuredWidth();
		}

		int desired = itemWidth * visibleItems - itemWidth * ITEM_OFFSET_PERCENT / 50;

	return Math.max(desired, getSuggestedMinimumWidth());
}
	
	/**
	 * Returns height of wheel item
	 * @return the item height
	 */
//	private int getItemHeight() {
//		if (itemHeight != 0) {
//			return itemHeight;
//		}
//		
//		if (itemsLayout != null && itemsLayout.getChildAt(0) != null) {
//			itemHeight = itemsLayout.getChildAt(0).getHeight();
//			return itemHeight;
//		}
//		
//		return getHeight() / visibleItems;
//	}
	//bylc
	private int getItemWidth() {
	if (itemWidth != 0) {
		return itemWidth;
	}
	
	if (itemsLayout != null && itemsLayout.getChildAt(0) != null) {
		itemWidth = itemsLayout.getChildAt(0).getWidth();
		return itemWidth;
	}
	
	return getWidth() / visibleItems;
}

	/**
	 * Calculates control width and creates text layouts
	 * @param widthSize the input layout width
	 * @param mode the layout mode
	 * @return the calculated control width
	 */
//	private int calculateLayoutWidth(int widthSize, int mode) {
//		initResourcesIfNecessary();
//
//		// TODO: make it static
//		itemsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//	    itemsLayout.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.UNSPECIFIED), 
//	                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//		int width = itemsLayout.getMeasuredWidth();
//
//		if (mode == MeasureSpec.EXACTLY) {
//			width = widthSize;
//		} else {
//			width += 2 * PADDING;
//
//			// Check against our minimum width
//			width = Math.max(width, getSuggestedMinimumWidth());
//
//			if (mode == MeasureSpec.AT_MOST && widthSize < width) {
//				width = widthSize;
//			}
//		}
//		
//        itemsLayout.measure(MeasureSpec.makeMeasureSpec(width - 2 * PADDING, MeasureSpec.EXACTLY), 
//                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//
//		return width;
//	}

	private int calculateLayoutHeight(int heightSize, int mode) {
		initResourcesIfNecessary();

		// TODO: make it static
		itemsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    itemsLayout.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 
	                MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.UNSPECIFIED));
		int height = itemsLayout.getMeasuredHeight();

		if (mode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height += 2 * padding;

			// Check against our minimum height
			height = Math.max(height, getSuggestedMinimumHeight());

			if (mode == MeasureSpec.AT_MOST && heightSize < height) {
				height = heightSize;
			}
		}
		
        itemsLayout.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 
        		MeasureSpec.makeMeasureSpec(height - 2 * padding, MeasureSpec.EXACTLY));

		return height;
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		buildViewForMeasuring();
		
		//int width = calculateLayoutWidth(widthSize, widthMode);
		//bylc
		int height = calculateLayoutHeight(heightSize, heightMode);
		
//		int height;
//		if (heightMode == MeasureSpec.EXACTLY) {
//			height = heightSize;
//		} else {
//			height = getDesiredHeight(itemsLayout);
//
//			if (heightMode == MeasureSpec.AT_MOST) {
//				height = Math.min(height, heightSize);
//			}
//		}
		int width;
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			width = getDesiredWidth(itemsLayout);

		if (widthMode == MeasureSpec.AT_MOST) {
			width = Math.min(width, widthSize);
		}
	}

		setMeasuredDimension(width, height);
	}
	
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	layout(r - l, b - t);
    }

    /**
     * Sets layouts width and height
     * @param width the layout width
     * @param height the layout height
     */
    private void layout(int width, int height) {
		//int itemsWidth = width - 2 * PADDING;
		//itemsLayout.layout(0, 0, itemsWidth, height);
    	
    	//bylc
    	int itemsHeight = height - 2 * padding;
		itemsLayout.layout(0, 0, width, itemsHeight);
    }

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (viewAdapter != null && viewAdapter.getItemsCount() > 0) {
	        updateView();
	        refreshItemLooks();
	        drawItems(canvas);
	        drawCenterRect(canvas);
		}
		
        drawShadows(canvas);
	}

	/**
	 * Draws shadows on top and bottom of control
	 * @param canvas the canvas for drawing
	 */
	private void drawShadows(Canvas canvas) {
//		int width = (int)(1.5 * getItemWidth());
//		leftShadow.setBounds(0, 0, width, getHeight());
//		leftShadow.draw(canvas);
//
//		rightShadow.setBounds(getWidth() - width, 0, getWidth(), getHeight());
//		rightShadow.draw(canvas);
	}

	/**
	 * Draws items
	 * @param canvas the canvas for drawing
	 */
	private void drawItems(Canvas canvas) {
		canvas.save();
		
//		int top = (currentItem - firstItem) * getItemHeight() + (getItemHeight() - getHeight()) / 2;
//		canvas.translate(PADDING, - top + scrollingOffset);
		//bylc
		int left = (currentItem - firstItem) * getItemWidth() + (getItemWidth() - getWidth()) / 2;
		canvas.translate( - left + scrollingOffset, padding);
		
		
		itemsLayout.draw(canvas);

		canvas.restore();
	}

	/**
	 * Draws rect for current value
	 * @param canvas the canvas for drawing
	 */
	private void drawCenterRect(Canvas canvas) {
		int center = getWidth() / 2;
		//int offset = (int) (getItemWidth() / 2 * 1.2);
		//centerDrawable.setBounds(0, center - offset, getWidth(), center + offset);
		//centerDrawable.setBounds(center - offset, 0, center + offset,getHeight());
		
		float density = getResources().getDisplayMetrics().density;
		float offset = centerDrawable.getIntrinsicWidth()/2 * density;
		centerDrawable.setBounds(center - (int)offset, 0, center + (int)offset,getHeight());
		
		centerDrawable.draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled() || getViewAdapter() == null) {
			return true;
		}
		
		switch (event.getAction()) {
		    case MotionEvent.ACTION_MOVE:
		        if (getParent() != null) {
		            getParent().requestDisallowInterceptTouchEvent(true);
		        }
		        break;
		        
		    case MotionEvent.ACTION_UP:
		        if (!isScrollingPerformed) {
		            int distance = (int) event.getX() - getWidth() / 2;
		            if (distance > 0) {
		                distance += getItemWidth() / 2;
		            } else {
                        distance -= getItemWidth() / 2;
		            }
		            int items = distance / getItemWidth();
		            if (items != 0 && isValidItemIndex(currentItem + items)) {
	                    notifyClickListenersAboutClick(currentItem + items);
		            }
		        }
		        break;
		}

		return scroller.onTouchEvent(event);
	}
	
	/**
	 * Scrolls the wheel
	 * @param delta the scrolling value
	 */
	private void doScroll(int delta) {
		
		Log.d("scr", "doScroll   " + "delta: " + delta);
		
		scrollingOffset += delta;
		
		int itemWidth = getItemWidth();
		int count = scrollingOffset / itemWidth;

		int pos = currentItem - count;
		int itemCount = viewAdapter.getItemsCount();
		
	    int fixPos = scrollingOffset % itemWidth;
	    if (Math.abs(fixPos) <= itemWidth / 2) {
	        fixPos = 0;
	    }
		if (isCyclic && itemCount > 0) {
		    if (fixPos > 0) {
		        pos--;
                count++;
		    } else if (fixPos < 0) {
		        pos++;
		        count--;
		    }
			// fix position by rotating
			while (pos < 0) {
				pos += itemCount;
			}
			pos %= itemCount;
		} else {
			// 
			if (pos < 0) {
				count = currentItem;
				pos = 0;
			} else if (pos >= itemCount) {
				count = currentItem - itemCount + 1;
				pos = itemCount - 1;
			} else if (pos > 0 && fixPos > 0) {
                pos--;
                count++;
            } else if (pos < itemCount - 1 && fixPos < 0) {
                pos++;
                count--;
            }
		}
		
		int offset = scrollingOffset;
		if (pos != currentItem) {
			setCurrentItem(pos, false);
		} else {
			invalidate();
		}
		
		// update offset
		scrollingOffset = offset - count * itemWidth;
		if (scrollingOffset > getWidth()) {
			scrollingOffset = scrollingOffset % getWidth() + getWidth();
		}
	}
		
	/**
	 * Scroll the wheel
	 * @param itemsToSkip items to scroll
	 * @param time scrolling duration
	 */
	public void scroll(int itemsToScroll, int time) {
		Log.d("scr", "scroll");
		
		int distance = itemsToScroll * getItemWidth() - scrollingOffset;
		Log.d("scr", "distance:"+distance);
		Log.d("scr", "itemsToScroll:"+itemsToScroll);
		Log.d("scr", "getItemWidth:"+getItemWidth());
		scroller.scroll(distance, time);
	}
	
	/**
	 * Calculates range for wheel items
	 * @return the items range
	 */
	private ItemsRange getItemsRange() {
        if (getItemWidth() == 0) {
            return null;
        }
        
		int first = currentItem;
		int count = 1;
		
		while (count * getItemWidth() < getWidth()) {
			first--;
			count += 2; // top + bottom items
		}
		
		if (scrollingOffset != 0) {
			if (scrollingOffset > 0) {
				first--;
			}
			count++;
			
			// process empty items above the first or below the second
			int emptyItems = scrollingOffset / getItemWidth();
			first -= emptyItems;
			count += Math.asin(emptyItems);
		}
		return new ItemsRange(first, count);
	}
	
	/**
	 * Rebuilds wheel items if necessary. Caches all unused items.
	 * 
	 * @return true if items are rebuilt
	 */
	private boolean rebuildItems() {
		Log.d("scr", "rebuildItems");
		Log.d("scr", "currentItem:" + currentItem);
		Log.d("scr", "firstItem:" + firstItem);
		boolean updated = false;
		ItemsRange range = getItemsRange();
		if (itemsLayout != null) {
			int first = recycle.recycleItems(itemsLayout, firstItem, range);
			updated = firstItem != first;
			firstItem = first;
		} else {
			createItemsLayout();
			updated = true;
		}
		
		if (!updated) {
			updated = firstItem != range.getFirst() || itemsLayout.getChildCount() != range.getCount();
		}
		
		if (firstItem > range.getFirst() && firstItem <= range.getLast()) {
			for (int i = firstItem - 1; i >= range.getFirst(); i--) {
				if (!addViewItem(i, true)) {
				    break;
				}
				firstItem = i;
			}			
		} else {
		    firstItem = range.getFirst();
		}
		
		int first = firstItem;
		for (int i = itemsLayout.getChildCount(); i < range.getCount(); i++) {
			if (!addViewItem(firstItem + i, false) && itemsLayout.getChildCount() == 0) {
			    first++;
			}
		}
		firstItem = first;
		
		return updated;
	}
	
	/**
	 * Updates view. Rebuilds items and label if necessary, recalculate items sizes.
	 */
	private void updateView() {
		if (rebuildItems()) {
			//bylc
			//calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
			calculateLayoutHeight(getHeight(), MeasureSpec.EXACTLY);
			layout(getWidth(), getHeight());
		}
	}

	/**
	 * Creates item layouts if necessary
	 */
	private void createItemsLayout() {
		if (itemsLayout == null) {
			itemsLayout = new LinearLayout(getContext());
			itemsLayout.setOrientation(LinearLayout.HORIZONTAL);
		}
	}

	/**
	 * Builds view for measuring
	 */
	private void buildViewForMeasuring() {
		// clear all items
		if (itemsLayout != null) {
			recycle.recycleItems(itemsLayout, firstItem, new ItemsRange());			
		} else {
			createItemsLayout();
		}
		
		// add views
		int addItems = visibleItems / 2;
		for (int i = currentItem + addItems; i >= currentItem - addItems; i--) {
			if (addViewItem(i, true)) {
			    firstItem = i;
			}
		}
	}

	/**
	 * Adds view for item to items layout
	 * @param index the item index
	 * @param first the flag indicates if view should be first
	 * @return true if corresponding item exists and is added
	 */
	private boolean addViewItem(int index, boolean first) {
		View view = getItemView(index);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		if (view != null) {
			if (first) {
				itemsLayout.addView(view, 0);
			} else {
				itemsLayout.addView(view,params);
			}
			itemsLayout.requestLayout();
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks whether intem index is valid
	 * @param index the item index
	 * @return true if item index is not out of bounds or the wheel is cyclic
	 */
	private boolean isValidItemIndex(int index) {
	    return viewAdapter != null && viewAdapter.getItemsCount() > 0 &&
	        (isCyclic || index >= 0 && index < viewAdapter.getItemsCount());
	}
	
	/**
	 * Returns view for specified item
	 * @param index the item index
	 * @return item view or empty view if index is out of bounds
	 */
    private View getItemView(int index) {
    	
    	Log.d("scr", "getItemView index:" + index);
		if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
			return null;
		}
		int count = viewAdapter.getItemsCount();
		if (!isValidItemIndex(index)) {
			return viewAdapter.getEmptyItem(recycle.getEmptyItem(), itemsLayout);
		} else {
			while (index < 0) {
				index = count + index;
			}
		}
		
		index %= count;
		return viewAdapter.getItem(index, recycle.getItem(), itemsLayout);
	}
	
	/**
	 * Stops scrolling
	 */
	public void stopScrolling() {
	    scroller.stopScrolling();
	}

	
	
	//bylc
	protected void refreshItemLooks()
	{
		int currentItemInLauout = currentItem - firstItem;
		for(int i=0; i< itemsLayout.getChildCount(); i++)
		{
			if(i==currentItemInLauout)
			{
				viewAdapter.chageItemAppearance( itemsLayout.getChildAt(i), true);
			}
			else
			{
				viewAdapter.chageItemAppearance( itemsLayout.getChildAt(i), false);
			}
		}
	}
}
