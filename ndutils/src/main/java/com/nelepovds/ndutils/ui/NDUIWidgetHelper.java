package com.nelepovds.ndutils.ui;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by dmitrynelepov on 26.09.14.
 */
public class NDUIWidgetHelper {

    public static void initWidgets(Object target){
        Class[] types = { int.class };

        Field[] fields = target.getClass().getDeclaredFields();
        for (Field oneField : fields) {
            if (oneField.isAnnotationPresent(NDUIWidget.class)) {
                Boolean isAccessible = oneField.isAccessible();
                oneField.setAccessible(true);
                NDUIWidget uiWidget = oneField.getAnnotation(NDUIWidget.class);
                int valueId = uiWidget.value();
                try {
                    Method method = target.getClass().getMethod("findViewById", types);
                    if (method != null) {
                        try {
                            Object widgetValue = method.invoke(target,Integer.valueOf(valueId));
                            oneField.set(target, widgetValue);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                oneField.setAccessible(isAccessible);
            }
        }
    }
}
