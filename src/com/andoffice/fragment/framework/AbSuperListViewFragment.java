package com.andoffice.fragment.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;

import com.andframe.adapter.AfListAdapter;
import com.andframe.bean.Page;
import com.andframe.feature.AfBundle;
import com.andframe.model.framework.AfModel;
import com.andframe.thread.AfListViewTask;
import com.andframe.thread.AfTask;
import com.andframe.view.multichoice.AfMultiChoiceAdapter;
import com.andframe.view.multichoice.AfMultiChoiceAdapter.MultiChoiceListener;
import com.andframe.view.multichoice.AfMultiChoiceItem;
import com.andframe.view.multichoice.AfMultiChoiceListView;
import com.andframe.widget.popupmenu.OnMenuItemClickListener;
import com.andoffice.layoutbind.ModuleBottombar;
import com.andoffice.layoutbind.ModuleTitlebar;

/**
 * AbSuperListViewFragment
 * @author 树朾
 * 	在 AbListViewFragment 的基础上 添加多选功能
 * @param <T>
 */
public abstract class AbSuperListViewFragment<T extends AfModel> extends AbListViewFragment<T> implements MultiChoiceListener<T>, OnMenuItemClickListener{

	private static final int REQUEST_SELECT = 1;
	
	protected AfMultiChoiceAdapter<T> mMultiChoiceAdapter = null;
	
	protected abstract List<T> onTaskListFromDomain(int task,Page page)throws Exception;

	protected abstract AfMultiChoiceItem<T> getItemLayout(T data);

	@Override
	protected void onCreate(AfBundle bundle, AfMultiChoiceListView listview)
			throws Exception {
		super.onCreate(bundle, listview);
		mBottombar.setSelectListener(this);
		mBottombar.setFunction(ModuleBottombar.ID_SELECT, true);
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("选择", REQUEST_SELECT);
		mTitlebar.addMeuns(map);
		mTitlebar.setMenuItemListener(this);
		mTitlebar.setFunction(ModuleTitlebar.FUNCTION_MENU);
	}
	
	@Override
	public boolean onBackPressed() {
		if(mMultiChoiceAdapter!=null&&
				mMultiChoiceAdapter.isMultiChoiceMode()){
			mMultiChoiceAdapter.closeMultiChoice();
			return true;
		}else{
			return super.onBackPressed();
		}
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		if(item.getItemId() == REQUEST_SELECT){
			if(mMultiChoiceAdapter != null){
				mMultiChoiceAdapter.beginMultiChoice();
			}else{
				makeToastShort("还没有数据喔~");
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == ModuleBottombar.ID_SELECT){
			if(mMultiChoiceAdapter != null){
				mMultiChoiceAdapter.beginMultiChoice();
			}else{
				makeToastShort("还没有数据喔~");
			}
		}else{
			super.onClick(v);
		}
	}
	
	@Override
	public void onMultiChoiceChanged(AfMultiChoiceAdapter<T> adapter,
			int number, int total) {

	}
	
	@Override
	public void onMultiChoiceChanged(AfMultiChoiceAdapter<T> adapter, T tag,
			boolean selected, int number) {

	}
	
	@Override
	public void onMultiChoiceClosed(AfMultiChoiceAdapter<T> adapter,
			List<T> list) {
		mBottombar.show();
	}
	@Override
	public void onMultiChoiceStarted(AfMultiChoiceAdapter<T> adapter, int number) {
		mBottombar.hide();
	}
	
	@Override
	protected AfListViewTask<T> getTask(Handler handler, int task) {
		return new AbListViewTask(handler,task);
	}

	@Override
	protected AfListAdapter<T> getAdapter(List<T> ltdata){
		mMultiChoiceAdapter = getMultiChoiceAdapter(ltdata);
		mMultiChoiceAdapter.addListener(this);
		return mMultiChoiceAdapter;
	}

	protected AfMultiChoiceAdapter<T> getMultiChoiceAdapter(List<T> ltdata){
		return new AbListViewAdapter(ltdata);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		AfListViewTask<?> task = AfListViewTask.getTask(msg);
		if(task != null && this.onTaskTerminate(task)){
			return true;
		}
		return super.handleMessage(msg);
	}
	
	protected class AbListViewAdapter extends AfMultiChoiceAdapter<T>{

		public AbListViewAdapter(List<T> ltdata) {
			super(getActivity(), ltdata);
		}
		
		@Override
		protected AfMultiChoiceItem<T> getMultiChoiceItem(T data) {
			return AbSuperListViewFragment.this.getItemLayout(data);
		}
	}
	
	protected class AbListViewTask extends AfListViewTask<T>{

		public AbListViewTask(Handler handler,int task) {
			super(handler, task);
		}
		
		@Override
		public boolean onPrepare() {
			return onTaskPrepare(mTask);
		}

		@Override
		protected List<T> onLoad() {
			return onTaskLoad();
		}

		@Override
		protected List<T> onListByPage(Page page, int task) throws Exception {
			return onTaskListFromDomain(mTask,page);
		}
		
		@Override
		protected boolean onWorking(int task) throws Exception {
			return onTaskWorking(task);
		}

		@Override
		protected boolean onRefreshed(boolean isfinish, List<T> ltdata) {
			return false;
		}

		@Override
		protected boolean onMored(boolean isfinish, List<T> ltdata,
				boolean ended) {
			return false;
		}
	}

	protected boolean onTaskPrepare(int task) {
		return true;
	}
	
	protected List<T> onTaskLoad(){
		return new ArrayList<T>();
	}

	protected List<T> onTaskMore(Page page) throws Exception{
		return new ArrayList<T>();
	}

	protected boolean onTaskWorking(int task) throws Exception{
		return false;
	}

	protected boolean onTaskTerminate(AfListViewTask<?> task) {
		return super.handleMessage(AfTask.putTask(task));
	}
}
