package test.sim_fb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import test.sim_fb.PullToRefreshListView.OnLoadMoreListener;
import test.sim_fb.PullToRefreshListView.OnRefreshListener;

import com.example.sim_facebook_app.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;

public class MainActivity extends Activity implements AnimationListener {
    private View sideBar;
    private View contentListLayout;
    public boolean sideBarOut = false;
    private contentListEdges clEdges = new contentListEdges();
    private String logtag1 = "side bar";

    private PullToRefreshListView contentList;
    private ArrayAdapter<String> contentListAdapter;
    private List<String> items;
    private List<String> newItems;
    private List<String> oldItems;
    private String logtag2 = "refresh list";

    private int time1;
    private int time2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sim_fb_main);

        sideBar = findViewById(R.id.side_bar);
        contentListLayout = findViewById(R.id.content_layout);
        sideBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideSideBar(view);
            }
        });
//        contentListLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (sideBarOut) {
//                    slideSideBar(view);
//                }
//            }
//        });

        initContentList();
    }

    private void initContentList() {
        setItemList();
        setNewItemList();
        setOldItemList();
        time1 = Calendar.getInstance().get(Calendar.SECOND); // used in rnGen(int)
        contentList = (PullToRefreshListView) findViewById(R.id.content_list);
        contentList.activity = this;

        contentList.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                /** on refreshing, add one city for 10 times to the content list **/
                Log.d(logtag2, "onRefresh");
                String newItem = getNewItem();
                for (int i = 0; i < 10; i++) {
                    items.add(0, newItem);
                }
                contentList.onRefreshComplete();
                contentListAdapter.notifyDataSetChanged();
            }

        });

        contentList.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                /** on loading, add one city of US for 10 times to the content list **/
                Log.d(logtag2, "Main onLoadMore");
                String oldItem = getOldItem();
                for (int i = 0; i < 10; i++) {
                    items.add(oldItem);
                }
                contentList.onLoadComplete();
                contentListAdapter.notifyDataSetChanged();
            }

        });

        contentListAdapter = new ArrayAdapter<String>(this, R.layout.list_item, items);
        contentList.setAdapter(contentListAdapter);
    }

    private void setOldItemList() {
        oldItems = new ArrayList<String>();
        oldItems.add("The USA - New York");
        oldItems.add("The USA - Los Angeles");
        oldItems.add("The USA - Chicago");
        oldItems.add("The USA - Houston");
        oldItems.add("The USA - Miami");
        oldItems.add("The USA - Washington DC");
        oldItems.add("The USA - San Francisco");
    }

    private void setNewItemList() {
        newItems = new ArrayList<String>();
        newItems.add("France - Paris");
        newItems.add("The UK - London");
        newItems.add("Japan - Tokyo");
        newItems.add("Australia - Sydney");
        newItems.add("China - Beijing");
        newItems.add("Germany - Berlin");
        newItems.add("Brazil - Rio");
        newItems.add("Spain - Madrid");
        newItems.add("India - New Delhi");
    }

    private void setItemList() {
        items = new ArrayList<String>();

        items.add("Taiwan - Taipei");
        items.add("Taiwan - Kaohsiung");
        items.add("Taiwan - Taichung");
        items.add("Taiwan - Taoyuan");
        items.add("Taiwan - Hsinchu");
        items.add("Taiwan - Chiayi");
        items.add("Taiwan - Tainan");
        items.add("Taiwan - Nantou");
        items.add("Taiwan - Yilan");
        items.add("Taiwan - Hualien");
    }

    private String getNewItem() {
        return newItems.get(rnGen(newItems.size()));
    }

    private String getOldItem() {
        return oldItems.get(rnGen(oldItems.size()));
    }

    private int rnGen(int range) {
        time2 = Calendar.getInstance().get(Calendar.SECOND);
        int diff = time2 - time1;
        return (diff > 0) ? diff % range : -diff % range;
    }

    /**
     * called when "slide" button is clicked (set in sim_fb_main.xml) *
     */
    public void slideSideBar(View view) {
        Log.d(logtag1, "slide button onClick");
        System.out.println("cl orig layout [" + clEdges.top + "," + clEdges.bottom + "," + clEdges.left + ","
                + clEdges.right + "]");

        Animation anim;

        /** content list's position after animation **/
        int width = contentListLayout.getMeasuredWidth();
        int height = contentListLayout.getMeasuredHeight();
        int leftEdge = (int) (width * 0.5);

        if (!sideBarOut) {
            /** side bar slides out **/
            anim = new TranslateAnimation(0, leftEdge, 0, 0);
            sideBar.setVisibility(View.VISIBLE);
            clEdges.setEdges(0, height, leftEdge, leftEdge + width);
        } else {
            /** side bar slides back **/
            anim = new TranslateAnimation(0, -leftEdge, 0, 0);
            clEdges.setEdges(0, height, 0, width);
        }

        anim.setDuration(500);
        anim.setAnimationListener(MainActivity.this);
        anim.setFillAfter(true);

        contentListLayout.startAnimation(anim);
    }

    /**
     * after animation ends, re-layout the content list *
     */
    public void setContentListLayout() {
        Log.d(logtag1, "set content list layout");
        //System.out.println("layout [" + clEdges.top + "," + clEdges.bottom + "," + clEdges.left + ","
        //        + clEdges.right + "]");
        contentListLayout.layout(clEdges.left, clEdges.top, clEdges.right, clEdges.bottom);
        contentListLayout.clearAnimation();
    }


    @Override
    public void onAnimationEnd(Animation animation) {
        Log.d(logtag1, "anim ends");

        sideBarOut = !sideBarOut;
        if (!sideBarOut) sideBar.setVisibility(View.INVISIBLE);

        setContentListLayout();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        Log.d(logtag1, "anim repeats");
    }

    @Override
    public void onAnimationStart(Animation animation) {
        Log.d(logtag1, "anim starts");
    }

    class contentListEdges {
        int top, bottom, left, right;

        public void setEdges(int t, int b, int l, int r) {
            this.top = t;
            this.bottom = b;
            this.left = l;
            this.right = r;
        }
    }

}
