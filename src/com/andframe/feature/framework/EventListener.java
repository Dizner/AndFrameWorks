package com.andframe.feature.framework;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.andframe.application.AfExceptionHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventListener implements OnClickListener, OnLongClickListener, OnItemClickListener, OnItemLongClickListener, OnCheckedChangeListener {

	private Object handler;

	private Method clickMethod;
	private Method longClickMethod;
	private Method itemClickMethod;
	private Method itemLongClickMehtod;
	private Method checkedChangedMehtod;

	public EventListener(Object handler) {
		this.handler = handler;
	}

	public OnClickListener click(Method method) {
		clickMethod = method;
		return this;
	}

	public OnLongClickListener longClick(Method method) {
		this.longClickMethod = method;
		return this;
	}

	public OnItemClickListener itemClick(Method method) {
		this.itemClickMethod = method;
		return this;
	}

	public OnItemLongClickListener itemLongClick(Method method) {
		this.itemLongClickMehtod = method;
		return this;
	}

	public OnCheckedChangeListener checkedChange(Method method) {
		this.checkedChangedMehtod = method;
		return this;
	}

	public void onClick(View v) {
		invokeMethod(handler, clickMethod, v);
	}

	public boolean onLongClick(View v) {
		return Boolean.valueOf(true).equals(invokeMethod(handler, longClickMethod, v));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		invokeMethod(handler, itemClickMethod, parent, view, position, id);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		return Boolean.valueOf(true).equals(invokeMethod(handler, itemLongClickMehtod, parent, view, position, id));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		invokeMethod(handler, checkedChangedMehtod, buttonView, isChecked);
	}

	private Object invokeMethod(Object handler, Method method, Object... params) {
		if (handler != null && method != null) {
			try {
				method.setAccessible(true);
				return method.invoke(handler, paramAllot(method, params));
			} catch (Throwable e) {
				e.printStackTrace();
				AfExceptionHandler.handler(e, "EventListener.invokeMethod");
			}
		}
		return null;
	}
	/**
	 * 智能参数分配
	 */
	private Object[] paramAllot(Method method, Object... params) {
		Set<Integer> set = new HashSet<>();
		List<Object> list = new ArrayList<>();
		Class<?>[] types = method.getParameterTypes();
		if (types.length > 0) {
			for (int i = 0; i < types.length; i++) {
				Object obj = null;
				if (params.length > i && params[i] != null && isInstance(types[i], params[i])) {
					set.add(i);
					obj = params[i];
				} else {
					for (int j = 0; j < params.length; j++) {
						if (params[j] != null && !set.contains(j) && isInstance(types[i], params[j])) {
							set.add(j);
							obj = params[j];
						}
					}
				}
				list.add(obj);
			}
		}
		return paramAllot(method,list.toArray(new Object[list.size()]),params);
	}

	/**
	 * 特定参数分配
	 */
	private Object[] paramAllot(Method method, Object[] args, Object... params) {
		if (params.length == 0) {
			return args;
		}
		Class<?>[] types = null;
		//View 智能获取tag中的值
		if (params[0] instanceof View) {
			Object tag = null;
			for (int i = 0; i < args.length && i < params.length; i++) {
				if (args[i] == null) {
					if (tag == null) {
						tag = ((View) params[0]).getTag();
						if (tag == null) {
							break;
						}
						types = method.getParameterTypes();
					}
					if (types[i].isAssignableFrom(tag.getClass())) {
						args[i] = tag;
					}
				}
			}
		}
		//ListView 智能获取列表 中的元素
		if (params[0] instanceof AdapterView && params[2] instanceof Integer) {
			Adapter adapter = ((AdapterView) params[0]).getAdapter();
			if (adapter != null && adapter.getCount() > 0) {
				int index = ((Integer) params[2]);
				if (params[0] instanceof ListView) {
					int count = ((ListView) params[0]).getHeaderViewsCount();
					index = index >= count ? (index - count) : index;
				}
				Object value = null;
				for (int i = 0; i < args.length && i < params.length; i++) {
					if (args[i] == null) {
						if (value == null) {
							value = adapter.getItem(index);
							if (value == null) {
								break;
							}
							types = method.getParameterTypes();
						}
						if (types[i].isAssignableFrom(value.getClass())) {
							args[i] = value;
						}
					}
				}
			}
		}
		return args;
	}

	private boolean isInstance(Class<?> t1, Object object) {
		if (t1.isPrimitive()) {
			if (t1.equals(int.class)) {
				t1 = Integer.class;
			} else if (t1.equals(short.class)) {
				t1 = Short.class;
			} else if (t1.equals(long.class)) {
				t1 = Long.class;
			} else if (t1.equals(float.class)) {
				t1 = Float.class;
			} else if (t1.equals(double.class)) {
				t1 = Double.class;
			} else if (t1.equals(char.class)) {
				t1 = Character.class;
			} else if (t1.equals(byte.class)) {
				t1 = Byte.class;
			} else if (t1.equals(boolean.class)) {
				t1 = Boolean.class;
			}
		}
		return t1.isInstance(object);
	}

}

