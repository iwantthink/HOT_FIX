package com.ryan.hotfix;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by renbo on 2018/8/2.
 */

public class HMTTest {

    //控件的路径
    String mViewPath;

    public static void action(View targetView, String className) {
        String resourceIDName = getResourceName(targetView);


        String viewPath = getViewPath(targetView);
        String viewPaht2 = getViewPath2(targetView);
        Log.e("HMTTest", "resource name = " + resourceIDName);
        Log.e("HMTTest", "viewPaht1 = " + viewPath);
        Log.e("HMTTest", "viewPaht2 = " + viewPaht2);
        Log.e("HMTTest", "targetView.getId():" + targetView.getId());
        Log.e("HMTTest", "activity   = " + getActivityName(targetView));
        Log.e("HMTTest", "classname = " + className);
    }

    public static void onLongClick(View targetView, String className) {
        Log.e("HMTTest", "onLongClick");
        Log.e("HMTTest", "targetView.getId():" + targetView.getId());
    }

    @Nullable
    private static String getResourceName(View targetView) {
        String resourceIDName = null;
        int targetId = targetView.getId();
        if (targetId == View.NO_ID) {
            resourceIDName = "null";
        } else {
            try {
                Resources resources = targetView.getContext().getApplicationContext().getResources();

                resourceIDName = resources.
                        getResourceName(targetId);

                String packageName = resources.getResourcePackageName(targetId);
                if (!packageName.equals("android")) {
                    resourceIDName = resourceIDName.replace(packageName + ":", "");
                }

                resourceIDName = resourceIDName.replace("/", "=");

            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
        }

        resourceIDName = "[" + resourceIDName + "]";
        return resourceIDName;
    }

    /**
     * 将[index] 表示成 同一个父类下 相同类型的控件的序列
     * <p>
     * [index]可以通过 view.setTag()进行缓存,这样就不需要每次都去 计算index
     *
     * @param targetView
     * @return
     */
    private static String getViewPath2(View targetView) {
        //记录Viewpath
        StringBuilder viewPath = new StringBuilder();
        View parentView = null;
        ViewGroup parentViewGroup = null;
        Object parentObject = null;
        String simpleName = "";


        while (targetView instanceof View) {
            simpleName = targetView.getClass().getSimpleName();
            Log.e("HMTTest", "current modified class is  [" + simpleName + "]");
            parentObject = targetView.getParent();
            if (parentObject instanceof ViewGroup) {

                //父类只有在为ViewGroup,才可以计算当前view在ViewGroup中的位置
                parentView = (View) parentObject;
                parentViewGroup = (ViewGroup) parentView;
                int index = calculateIndex(targetView, parentViewGroup);
                Log.d("HMTTest", "index:" + index);
                viewPath.append(simpleName + "[" + index + "]-" + getResourceName(targetView) + "/");
                if (checkIsSystemView(targetView)) {
                    return viewPath.toString() + "Window";
                } else {
                    //下一层遍历
                    targetView = (View) targetView.getParent();
                }

            } else if (parentObject instanceof View) {
                //父类为View 那么无法计算index ,
                viewPath.append(simpleName);
                if (checkIsSystemView(targetView)) {
                    return viewPath.toString();
                } else {
                    //下一层遍历
                    targetView = (View) targetView.getParent();
                }
            } else {
                //父类不是View , 退出计算
                viewPath.append(simpleName);
                break;
            }

        }


        return viewPath.toString();
    }

    private static boolean checkIsSystemView(View targetView) {
        int viewID = targetView.getId();
        //屏蔽掉系统控件
        if (!(viewID == View.NO_ID)) {
            if (targetView.getContext().getApplicationContext().
                    getResources().getResourcePackageName(viewID).
                    startsWith("android")) {
                return true;
            }
        }
        return false;
    }

    private static int calculateIndex(View targetView, ViewGroup parentViewGroup) {
        int index = -1;

        int childCount = parentViewGroup.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View childItem = parentViewGroup.getChildAt(i);
            if (childItem.getClass() == targetView.getClass()) {
                index++;

                if (childItem == targetView) {
                    break;
                }
            }
        }


        return index;
    }

    /**
     * AppCompatButton/ConstraintLayout[0]/ContentFrameLayout[0]/ActionBarOverlayLayout[0]/FrameLayout[0]/LinearLayout[1]/DecorView[0]
     * <p>
     * [index] 中的index表示 前一个控件在当前控件中的位置
     *
     * @param targetView
     * @return
     */
    private static String getViewPath(View targetView) {
        StringBuilder viewPath = new StringBuilder();
        //用来和父View进行匹配
        View lastView = targetView;
        do {
            String classSimpleName = targetView.getClass().getSimpleName();
            if (targetView instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) targetView;
                //上一个子类在当前ViewGroup中的位置
                int childIndex = viewGroup.indexOfChild(lastView);

                if (classSimpleName.equals("DecorView")) {
                    viewPath.append(classSimpleName + "[" + childIndex + "]");
                } else {
                    viewPath.append(classSimpleName + "[" + childIndex + "]/");
                }
            } else {
                viewPath.append(classSimpleName + "/");
            }

            lastView = targetView;

            if (classSimpleName.equals("DecorView")) {
                break;
            }
        } while ((targetView = (View) targetView.getParent()) instanceof View);

        return viewPath.toString();
    }

    //从View中利用context获取所属Activity的名字
    public static String getActivityName(View view) {
        Context context = view.getContext();
        if (context instanceof Activity) {
            //context本身是Activity的实例
            return context.getClass().getSimpleName();
        } else if (context instanceof ContextWrapper) {
            //Activity有可能被系统＂装饰＂，看看context.base是不是Activity
            Activity activity = getActivityFromContextWrapper((ContextWrapper) context);
            if (activity != null) {
                return activity.getClass().getSimpleName();
            } else {
                //如果从view.getContext()拿不到Activity的信息（比如view的context是Application）,则返回当前栈顶Activity的名字
                return "get activity name from current top stack";
            }
        }
        return "";
    }

    private static Activity getActivityFromContextWrapper(ContextWrapper context) {
        Context base = context.getBaseContext();
        if (base instanceof Activity) {
            return (Activity) base;
        } else {
            return null;
        }
    }

}
