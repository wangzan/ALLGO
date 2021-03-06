package cn.edu.njupt.allgo.fragment;

import java.util.ArrayList;

import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import cn.edu.njupt.allgo.R;
import cn.edu.njupt.allgo.activity.EventPageACTIVITY;
import cn.edu.njupt.allgo.activity.HomeACTIVITY;
import cn.edu.njupt.allgo.activity.LoadEventACTIVITY;
import cn.edu.njupt.allgo.activity.UnreadACTIVITY;
import cn.edu.njupt.allgo.adapter.UnreadCardsAdapter;
import cn.edu.njupt.allgo.logic.RefreshInterFace;
import cn.edu.njupt.allgo.logic.UnreadLogic;
import cn.edu.njupt.allgo.logicImpl.UnreadLogicImpl;
import cn.edu.njupt.allgo.util.ArrayListUtil;
import cn.edu.njupt.allgo.vo.UnreadVo;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;  
import android.view.View; 
import android.view.ViewGroup; 
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;


public class UnreadFRAGMENT extends BaseFRAGMENT implements PullToRefreshAttacher.OnRefreshListener , RefreshInterFace{

	private ListView listView;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private UnreadCardsAdapter UnreadcardsAdapter;
	private ArrayList<UnreadVo> unreadDate = new ArrayList<UnreadVo>() ;
	private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
	private UnreadLogic unreadLogic ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		 
		super.onCreate(savedInstanceState);
		setFragmentFlag("UnreadFRAGMENT");
		setHasOptionsMenu(true);
		unreadLogic = new UnreadLogicImpl(getActivity() , UnreadFRAGMENT.this);
		unreadLogic.initUnread();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_unread, null);
		return view;
	}

	@Override 
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setView();
	}
	
	
	private void setView() {
		listView = (ListView)getView().findViewById(R.id.listView_unread);
		mPullToRefreshAttacher = ((UnreadACTIVITY)getActivity())
                .getPullToRefreshAttacher();
        mPullToRefreshAttacher.addRefreshableView(listView, this);
        
        UnreadcardsAdapter = new UnreadCardsAdapter(getActivity(),unreadDate);
		swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(UnreadcardsAdapter);
		swingBottomInAnimationAdapter.setInitialDelayMillis(300);
		swingBottomInAnimationAdapter.setAbsListView(listView);
		listView.setAdapter(swingBottomInAnimationAdapter);
        
		listView.setOnItemClickListener(new OnItemClickListener() {   
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO 点击方法
				unreadLogic.setRead(UnreadcardsAdapter.getItem(arg2));
				arg1.setBackgroundResource(R.color.transparent_yellow);
				doAction(UnreadcardsAdapter.getItem(arg2));

			}
        });
	}

	//进入相应界面
	private void doAction(UnreadVo unreadVo) {
		switch(unreadVo.getAction()){
		case 0:		//进入活动主页
			Intent intent = new Intent(getActivity(),LoadEventACTIVITY.class);
			intent.putExtra("eid", unreadVo.getId());
			startActivity(intent);
			break;
		case 1:		//无操作
			
			break;
		case 2:		//进入添加好友界面
			
			break;
		case 3:		//进入聊天界面
			
			break;
		}
		
	}
	
	@Override
	public void initActionBar() {
		// TODO 初始化actionbar
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);  
	}

	@Override
	public void onRefreshStarted(View view) {
		// TODO 下拉刷新的动作
		unreadLogic.getUnread();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(Object result, int kind) {
		switch(kind){
		case 1:
			if(result != null){
				unreadDate.clear();
				unreadDate.addAll(0,(ArrayList<UnreadVo>)result);
				ArrayListUtil.removeDuplicate(unreadDate);
				ArrayListUtil.sortUnreadVo(unreadDate);
				UnreadcardsAdapter.notifyDataSetChanged();
				}
			break;
		case 2:
			mPullToRefreshAttacher.setRefreshComplete();
            unreadDate.addAll(0,(ArrayList<UnreadVo>)result); 
            ArrayListUtil.removeDuplicate(unreadDate);
			ArrayListUtil.sortUnreadVo(unreadDate);
            UnreadcardsAdapter.notifyDataSetChanged();
			break;
		case 3:			//从数据库更新内容
			unreadLogic.saveUnread(unreadDate);
			unreadLogic.initUnread();
		case -1:
			//Toast.makeText(getActivity(),(String)result,Toast.LENGTH_SHORT).show();
			if(mPullToRefreshAttacher.isRefreshing()){
				mPullToRefreshAttacher.setRefreshComplete();
			}
			break;
		}
	}

	@Override
	public void saveData() {
		unreadLogic.saveUnread(unreadDate);
	}
	
}
