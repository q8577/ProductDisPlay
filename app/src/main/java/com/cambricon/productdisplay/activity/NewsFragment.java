package com.cambricon.productdisplay.activity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.adapter.NewsAdapter;
import com.cambricon.productdisplay.bean.News;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 18-2-3.
 */

public class NewsFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private NewsAdapter adapter;
    private List<News> newsList = new ArrayList<>();
    public static final int AUTOBANNER_CODE = 0x1001;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.news_fragment, null);
        initNews();
        return view;
    }

    /**
     * 加载资讯列表
     */
    private LinearLayoutManager mLinearLayoutManager;

    public void initNews() {
        initNewsData();

        recyclerView = view.findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        adapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(adapter);

        swipeRefresh = view.findViewById(R.id.news_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //加载界面数据
                                addNews();
                                adapter.notifyDataSetChanged();
                                //刷新结束，隐藏刷新进度条
                                swipeRefresh.setRefreshing(false);
                            }
                        });

                    }
                }).start();
            }
        });

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.addOnScrollListener(new EndLessOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                loadMoreData();
            }
        });


    }

    /**
     * 资讯测试集合
     */

    List<News> testList = new ArrayList<>();

    public void test() {
        News news1 = new News(1, R.drawable.news1, "CB lnsights 最新发布全球AI 100榜单,寒武纪首度入选", "2017年12月19日",
                "http://mp.weixin.qq.com/s?__biz=MzIwOTM3NDcxNQ==&mid=2247484072&idx=1&sn=1f59bc3d0e00f017c4d1462275e07810&chksm=97759e3ca002172aacbeb80f0d8240cd77a728d0676b3c08da872b595d68d90577a76d4d5938&mpshare=1&scene=23&srcid=0201yeAOaOULwORmRuKWM8gY#rd\n");
        News news2 = new News(2, R.drawable.news2, "寒武纪成功举办第22届国际体系结构...", "2017年4月15日",
                "http://mp.weixin.qq.com/s?__biz=MzIwOTM3NDcxNQ==&mid=2247483879&idx=1&sn=eb46f7cabe394f387f5657e060ff429d&chksm=97759d73a0021465cc7388598fb422566e1abf4b73b2846f64f9665c159e123b5e151f4ae293&mpshare=1&scene=23&srcid=0201wq0DSNP5Adx2Z1nknJDF#rd\n");
        News news3 = new News(3, R.drawable.news3, "重磅|寒武纪捷报频传--再获两项荣誉", "2017年12月5日",
                "http://mp.weixin.qq.com/s?__biz=MzIwOTM3NDcxNQ==&mid=2247484061&idx=1&sn=a5426e92d04556edbe50bf9faf3d5646&chksm=97759e09a002171fa68b0281cc7c3da7cab7a28e74ca1251a6a4f630f586baf8df9db2d1c1f7&mpshare=1&scene=23&srcid=02013br22Ve8RSzwmauPTH0i#rd\n");
        News news4 = new News(4, R.drawable.news4, "寒武纪科技收场发布会回顾", "2017年11月8日",
                "http://mp.weixin.qq.com/s?__biz=MzIwOTM3NDcxNQ==&mid=2247484044&idx=1&sn=16140175ff1b2d848983ee862d8c720c&chksm=97759e18a002170e357197aa5e342910f8404ed5006b9b75d8b3fcbfb85c0702cb75afc8d176&mpshare=1&scene=23&srcid=0201AblJ1T1xUDPzwEz3wBjA#rd\n");
        News news5 = new News(5, R.drawable.news5, "央视年末盘点2017黑科技，寒武纪芯片主力智能时代", "2017年12月19日",
                "http://mp.weixin.qq.com/s?__biz=MzIwOTM3NDcxNQ==&mid=2247484083&idx=1&sn=ac8fac2ae228c8ce328d1a20d925f618&chksm=97759e27a0021731fc5016f5bc7e14c223d7037e155ea3f3462fba34ee60de7d5fc0cf253bca&mpshare=1&scene=23&srcid=0201h9a9R87nRbNZOc8T20mx#rd\n");
        testList.add(news1);
        testList.add(news2);
        testList.add(news3);
        testList.add(news4);
        testList.add(news5);
    }

    int max = 40;
    boolean start = true;

    /**
     * 加载初始页面资讯
     */
    public void initNewsData() {
        test();
        for (int i = 0; i < 10; i++) {
            newsList.add(testList.get((int) (Math.random() * 4 + 1)));
        }
    }

    /**
     * 刷新添加新资讯
     */
    public void addNews() {
        if (start == true) {
            newsList.add(0, testList.get((int) (Math.random() * 4 + 1)));
            start = false;
        }

    }

    /**
     * 上划加载更多数据
     */
    public void loadMoreData() {
        if (adapter.getItemCount() < max) {
            for (int i = 0; i < 5; i++) {
                newsList.add(testList.get((int) (Math.random() * 4 + 1)));
                adapter.notifyDataSetChanged();
            }
        }
    }
}

/**
 * 监听实现上拉加载更多的功能
 */

abstract class EndLessOnScrollListener extends RecyclerView.OnScrollListener {

    //声明一个LinearLayoutManager
    private LinearLayoutManager mLinearLayoutManager;
    //当前页
    private int currentPage = 0;
    //已经加载出来的Item的数量
    private int totalItemCount;
    //主要用来存储上一个totalItemCount
    private int previousTotal = 0;
    //在屏幕上可见的item数量
    private int visibleItemCount;
    //在屏幕可见的Item中的第一个
    private int firstVisibleItem;
    //是否正在上拉数据
    private boolean loading = true;

    public EndLessOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        if (loading) {
            if (totalItemCount > previousTotal) {
                //说明数据已经加载结束
                loading = false;
                previousTotal = totalItemCount;
            }
        }

        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem) {
            currentPage++;
            onLoadMore(currentPage);
            loading = true;
        }
    }

    /**
     * 提供一个抽闲方法，在Activity中监听到这个EndLessOnScrollListener
     * 并且实现这个方法
     */
    public abstract void onLoadMore(int currentPage);
}
