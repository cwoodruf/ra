package ca.researchassistant;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;


// see http://stackoverflow.com/questions/12569109/android-double-expandable-listview-hides-items-if-list-is-too-big
// implements a 3 level tree adapter by dynamically creating the 2nd and third levels

public class TreeAdapter extends BaseExpandableListAdapter {
    
	final static String TAG = TreeAdapter.class.getSimpleName();
	final int ROWHEIGHT = 48;
	final int TEXTHEIGHT = 18;
	static public Integer selectedClass = null;
	static int CHILD_PADDING;
	static float TEXT_SIZE;
	private Tree tree;
	private OnChildClickListener lst;
	final private Context context;
	
	class Entry {
		final CustExpListview cls;
		final SecondLevelAdapter sadtp;
		public Entry(CustExpListview cls, SecondLevelAdapter sadtp) {
			this.cls = cls;
			this.sadtp = sadtp;
		}
	}
	
	Entry[] lsfirst;
		
	public TreeAdapter(Activity ctx, Tree tree, 
			OnChildClickListener lst, OnItemLongClickListener contextMenu) {
		this.context = ctx;
		this.tree = tree;       
		this.setLst(lst);
		TEXT_SIZE = 35;
		CHILD_PADDING = 40;
		
		lsfirst = new Entry[tree.getChildren().size()];
		
		for(int i=0; i<tree.getChildren().size();i++){
			CustExpListview SecondLevelexplv = new CustExpListview(context);
			SecondLevelAdapter adp = new SecondLevelAdapter(tree.getChildren().get(i));
			SecondLevelexplv.setAdapter(adp);
			SecondLevelexplv.setGroupIndicator(null);
			// add listeners
			SecondLevelexplv.setOnChildClickListener(lst);
			SecondLevelexplv.setOnItemLongClickListener(contextMenu);
			
			lsfirst[i] = new Entry(SecondLevelexplv, adp);
		
		}
	}

	@Override
	public Object getChild(int arg0, int arg1){             
		return arg1;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition){
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		//LISTA DI second level           
		return lsfirst[groupPosition].cls;
	}

	@Override
	public int getChildrenCount(int groupPosition){   
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupPosition;
	}

	@Override
	public int getGroupCount() {   
		return tree.getChildren().size();
	}

	@Override
	public long getGroupId(int groupPosition) {   
		return tree.getChildren().get(groupPosition).getId();
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
		//first level
		TextView tv = new TextView(context); 
		tv.setText(tree.getChildren().get(groupPosition).getName());
        tv.setTextSize(TEXTHEIGHT);
        tv.setHeight(ROWHEIGHT);
		tv.setPadding(5, 8, 8, 8); 
		return tv;
	}
	
	@Override
	public boolean hasStableIds(){
		return true;
	}
	
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}     
	
	/////////////////////////////////////////////////////////////////////////////
	
	//_____________-------------________----------________---------_______-----//
	
	/////////////////////////////////////////////////////////////////////////////

	public OnChildClickListener getLst() {
		return lst;
	}

	public void setLst(OnChildClickListener lst) {
		this.lst = lst;
	}

	public class CustExpListview extends ExpandableListView {

	    int intGroupPosition, intChildPosition, intGroupid;
	    
	    public CustExpListview(Context context) 
	    {
	        super(context);
	        this.setSelector(android.R.drawable.divider_horizontal_dark);
	    }
	    
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	    {
	    	// TODO: consider doing this dynamically to handle arbitrary sized lists
	        widthMeasureSpec = MeasureSpec.makeMeasureSpec(960, MeasureSpec.AT_MOST);
	        heightMeasureSpec = MeasureSpec.makeMeasureSpec(6000, MeasureSpec.AT_MOST);   
	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    }  
    }
    
    public class SecondLevelAdapter extends BaseExpandableListAdapter {
    
	    Tree tree2;
	    
	    public SecondLevelAdapter(Tree tr) {
	    	this.tree2 = tr;
	    }
	    
	    // Returns the name of the selected class
	    @Override
	    public Object getChild(int groupPosition, int childPosition) {                
	    	return tree2.getChildren().get(groupPosition).getChildren().get(childPosition).getName();
	    }
	    
	    // Returns the integer id of the selected class
	    @Override
	    public long getChildId(int groupPosition, int childPosition) {   
	    	return tree2.getChildren().get(groupPosition).getChildren().get(childPosition).getId();
	    }
	    
	    @Override
	    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
	    	//third level
	        TextView tv = new TextView(context); 
	        tv.setText(tree2.getChildren().get(groupPosition).getChildren().get(childPosition).getName());	    
	        tv.setPadding(2*CHILD_PADDING, 10, tv.getPaddingRight(), tv.getPaddingBottom());
	        tv.setTextSize(TEXTHEIGHT);
	        tv.setHeight(ROWHEIGHT);
	        // needed for long press implementation to get parent information
	        tv.setTag((Object) new Tag(
	        			tree2.getId(),
	        			tree2.getChildren().get(groupPosition).getId(),
	        			tree2.getChildren().get(groupPosition).getChildren().get(childPosition).getId()
	        		));
	        return tv;
	    }
	    
	    @Override
	    public int getChildrenCount(int groupPosition){
	    	return tree2.getChildren().get(groupPosition).getChildren().size();
	    }
	    
	    @Override
	    public Object getGroup(int groupPosition) {   
	        return groupPosition;
	    }
	    
	    @Override
	    public int getGroupCount() {
	    	return tree2.getChildren().size();
	    }
	    
	    @Override
	    public long getGroupId(int groupPosition) {
	    	return groupPosition;
	    }
	    
	    @Override
	    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
	    	// second level
	        TextView tv = new TextView(context); 
	        tv.setText(tree2.getChildren().get(groupPosition).getName());
	        tv.setPadding(CHILD_PADDING, 10, tv.getPaddingRight(), tv.getPaddingBottom());
	        tv.setTextSize(TEXTHEIGHT);
	        tv.setHeight(ROWHEIGHT);
	        return tv;
	    }
	    
	    @Override
	    public boolean hasStableIds() {
	        return true;
	    }
	    
	    @Override
	    public boolean isChildSelectable(int groupPosition, int childPosition) {
	        return true;
	    }
    }
}