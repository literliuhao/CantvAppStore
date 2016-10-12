package cn.can.tvlib.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * ================================================
 * 作    者：xzl
 * 版    本：1.0
 * 创建日期：2016.10.12
 * 描    述：主要解决RelativeLayout中childView放大时相互遮挡的问题
 * 修订历史：
 *
 * ================================================
 */
public class CanRelativeLayout extends RelativeLayout {
    private static final String TAG = "CustomRelativeLayout";
    public CanRelativeLayout(Context context) {
        super(context);
    }

    public CanRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int focusIndex=getFocusChildIndex();
        if(focusIndex<0){
            return i;
        }
        if (i==childCount-1){
            return focusIndex;
        }else if(i<focusIndex){
            return i;
        }else{
            return i+1;
        }
    }

    private int getFocusChildIndex(){
        int focusIndex=-1;
        for(int i=0;i<getChildCount();i++){
           if( getFocusedChild()==getChildAt(i)){
               focusIndex=i;
           }
        }
        return focusIndex;
    }
}
