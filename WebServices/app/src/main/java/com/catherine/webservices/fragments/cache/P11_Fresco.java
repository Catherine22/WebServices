package com.catherine.webservices.fragments.cache;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.catherine.webservices.R;
import com.catherine.webservices.adapters.FrescoRVAdapter;
import com.catherine.webservices.entities.ImageCard;
import com.catherine.webservices.entities.TestData;
import com.catherine.webservices.fragments.LazyFragment;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.toolkits.CLog;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.DefaultExecutorSupplier;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Catherine on 2017/10/13.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P11_Fresco extends LazyFragment {
    public final static String TAG = "P11_Fresco";
    private SwipeRefreshLayout srl_container;
    private List<ImageCard> entities;
    private FrescoRVAdapter adapter;
    private ProgressBar pb;
    private TextView tv_offline;
    private boolean cacheable;
    private PrefetchSubscriber subscriber;
    private int succeed, failed;

    public static P11_Fresco newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P11_Fresco fragment = new P11_Fresco();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_11_fresco);

        if (getArguments() != null)
            cacheable = getArguments().getBoolean("cacheable", false);
        initComponent();
        fillInData();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void fillInData() {
        tv_offline.setVisibility(View.GONE);
        entities = new ArrayList<>();
        if (getArguments() != null && getArguments().getParcelableArrayList("imageCards") != null)
            entities = getArguments().getParcelableArrayList("imageCards");
        adapter.updateData(entities);
        adapter.notifyDataSetChanged();
        if (entities.size() == 0) {
            getPicList();
        } else {
            pb.setVisibility(View.INVISIBLE);
        }
    }

    private void getPicList() {
        try {
            pb.setVisibility(View.INVISIBLE);
            //Let's say those image links are downloaded successfully from an API
            JSONArray ja = new JSONArray();
            for (int i = 0; i < TestData.IMAGES2.length; i++) {
                ja.put(TestData.IMAGES2[i]);
            }
            JSONObject jo = new JSONObject();
            jo.put("pics", ja);
            JSONArray pics = jo.getJSONArray("pics");
            for (int i = 0; i < pics.length(); i++) {
                ImageCard imageCard = new ImageCard();
                imageCard.image = pics.getString(i);
                imageCard.title = NetworkHelper.getFileNameFromUrl(pics.getString(i));
                imageCard.subtitle = "fresh";//not cache
                entities.add(imageCard);
            }
            adapter.updateData(entities);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            pb.setVisibility(View.INVISIBLE);
            e.printStackTrace();
            return;
        }
        //cache
        if (cacheable) {
            try {
                for (int i = 0; i < entities.size(); i++) {
//                                    File file = new File(Constants.ROOT_PATH + Constants.FRESCO_DIR + "/");
                    String url = entities.get(i).image;
                    ImageRequest imageRequest = ImageRequest.fromUri(url);
                    CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest, null);
                    BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
                    if (resource == null || resource.size() == 0) {
                        DataSource<Void> ds = Fresco.getImagePipeline().prefetchToDiskCache(ImageRequest.fromUri(url), null);
                        ds.subscribe(subscriber, new DefaultExecutorSupplier(3).forBackgroundTasks());
                    }
                }
            } catch (Exception e) {
                pb.setVisibility(View.INVISIBLE);
                CLog.e(TAG, "Cache error:" + e.getMessage());
                tv_offline.setText(String.format(Locale.ENGLISH, "connectFailure JSON error:%s", e.getMessage()));
                tv_offline.setVisibility(View.VISIBLE);
            }
        }
    }


    private void initComponent() {
        srl_container = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark);
        srl_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CLog.d(TAG, "refresh");
                pb.setVisibility(View.VISIBLE);
                succeed = 0;
                failed = 0;
                fillInData();
                srl_container.setRefreshing(false);
            }
        });

        RecyclerView rv_main_list = (RecyclerView) findViewById(R.id.rv_main_list);
        rv_main_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FrescoRVAdapter(getActivity(), entities, cacheable, new OnItemClickListener() {
            @Override
            public void onItemLongClick(@NotNull View view, int position) {
            }

            @Override
            public void onItemClick(@NotNull View view, int position) {
            }
        });
        rv_main_list.setAdapter(adapter);

        pb = (ProgressBar) findViewById(R.id.pb);
        tv_offline = (TextView) findViewById(R.id.tv_offline);

        FloatingActionButton fab_delete = (FloatingActionButton) findViewById(R.id.fab_delete);
        fab_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //delete cache
//                Fresco.getImagePipeline().clearMemoryCaches();
//                Fresco.getImagePipeline().clearDiskCaches();

                //combines above two lines
                Fresco.getImagePipeline().clearCaches();
            }
        });

        if (cacheable) {
            subscriber = new PrefetchSubscriber();
        }
    }

    private class PrefetchSubscriber extends BaseDataSubscriber<Void> {

        @Override
        protected void onNewResultImpl(DataSource<Void> dataSource) {
            ++succeed;
            CLog.i(TAG, "observer, succeed:" + succeed);
            if (entities.size() == succeed) {
                CLog.w(TAG, "All the images are cached!");
            }
        }

        @Override
        protected void onFailureImpl(DataSource<Void> dataSource) {
            ++failed;
            CLog.e(TAG, "observer, failed:" + failed);
        }
    }
}
