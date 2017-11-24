package com.catherine.webservices.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.catherine.webservices.Commands;
import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.TextCardRVAdapter;
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.entities.ImageCard;
import com.catherine.webservices.entities.TestData;
import com.catherine.webservices.interfaces.BackKeyListener;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.network.HttpAsyncTask;
import com.catherine.webservices.network.HttpRequest;
import com.catherine.webservices.network.HttpResponse;
import com.catherine.webservices.network.HttpResponseListener;
import com.catherine.webservices.network.MyHttpURLConnection;
import com.catherine.webservices.network.NetworkHelper;
import com.catherine.webservices.security.ADID_AsyncTask;
import com.catherine.webservices.toolkits.CLog;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import catherine.messagecenter.Client;
import catherine.messagecenter.CustomReceiver;
import catherine.messagecenter.Result;

/**
 * Created by Catherine on 2017/9/15.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class P04_Cache extends LazyFragment {
    public final static String TAG = "P04_Cache";
    private List<String> features;
    private List<String> descriptions;
    private SwipeRefreshLayout srl_container;
    private MainInterface mainInterface;
    private TextCardRVAdapter adapter;
    private ProgressBar pb;
    private TextView tv_pb_info;
    private Client client;

    public static P04_Cache newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        P04_Cache fragment = new P04_Cache();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_04_cache);
        init();
    }

    private void init() {
        mainInterface = (MainInterface) getActivity();
        mainInterface.getPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                fillInData();
                initComponent();
            }

            @Override
            public void onDenied(@org.jetbrains.annotations.Nullable List<String> deniedPermissions) {
                StringBuilder context = new StringBuilder();
                if (deniedPermissions != null) {
                    for (String p : deniedPermissions) {
                        if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(p)) {
                            context.append("存储、");
                        }
                    }
                }

                context.deleteCharAt(context.length() - 1);
                DialogManager.showPermissionDialog(getActivity(), String.format(getActivity().getResources().getString(R.string.permission_request), context), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
            }

            @Override
            public void onRetry() {
                init();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void fillInData() {
        features = new ArrayList<>();
        features.add("ImageView + DiskLruCache");
        features.add("ImageView + DiskLruCache");
        features.add("(Fresco) SimpleDraweeView + Non-cached");
        features.add("(Fresco) SimpleDraweeView + Cache");
        features.add("(Fresco) SimpleDraweeView + Prefetch To Disk Cache");

        descriptions = new ArrayList<>();
        descriptions.add("Download images from the Internet and cache them.");
        descriptions.add("Download images from the Internet and cache them, or show cache when the network not works.");
        descriptions.add("Show images from the Internet.");
        descriptions.add("Download images from the Internet and cache them.");
        descriptions.add("Download and cache images then show caches.");
    }

    private void initComponent() {
        srl_container = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        srl_container.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorAccentDark);
        srl_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
                srl_container.setRefreshing(false);
            }
        });

        client = new Client(getActivity(), new CustomReceiver() {
            @Override
            public void onBroadcastReceive(@NotNull Result result) {
                if (result.getMBoolean() != null && result.getMBoolean()) {
                    updateView();
                }
            }
        });
        client.gotMessages(Commands.UPDATE_P04);

        mainInterface.setBackKeyListener(new BackKeyListener() {
            @Override
            public void OnKeyDown() {
                if (getChildFragmentManager().getBackStackEntryCount() > 0) {
                    getChildFragmentManager().popBackStack();
                    mainInterface.restoreBottomLayout();
                } else
                    mainInterface.backToPreviousPage();
            }
        });

        RecyclerView rv_main_list = (RecyclerView) findViewById(R.id.rv_main_list);
//        rv_main_list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.Companion.getVERTICAL_LIST()));
        rv_main_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TextCardRVAdapter(getActivity(), null, features, descriptions, new OnItemClickListener() {
            @Override
            public void onItemClick(@NotNull View view, int position) {
                mainInterface.restoreBottomLayout();
                switch (position) {
                    case 0:
                        Bundle b0 = new Bundle();
                        b0.putBoolean("show_pic_offline", false);
                        callFragment(Constants.P05_Gallery, b0);
                        break;
                    case 1:
                        Bundle b1 = new Bundle();
                        b1.putBoolean("show_pic_offline", true);
                        callFragment(Constants.P05_Gallery, b1);
                        break;
                    case 2:
                        Bundle b2 = new Bundle();
                        b2.putBoolean("cacheable", false);
                        callFragment(Constants.P11_FRESCO, b2);
                        break;
                    case 3:
                        Bundle b3 = new Bundle();
                        b3.putBoolean("cacheable", true);
                        callFragment(Constants.P11_FRESCO, b3);
                        break;
                    case 4:
                        Bundle b4 = new Bundle();
                        b4.putBoolean("cacheable", true);
                        b4.putParcelableArrayList("imageCards", (ArrayList<ImageCard>) imageCards);
                        callFragment(Constants.P11_FRESCO, b4);
                        break;
                }
            }

            @Override
            public void onItemLongClick(@NotNull View view, int position) {

            }
        });
        rv_main_list.setAdapter(adapter);
        updateView();
    }

    private void updateView() {
        mainInterface.restoreBottomLayout();
        mainInterface.addBottomLayout(R.layout.bottom_progressbar);
        View bottom = mainInterface.getBottomLayout();
        pb = bottom.findViewById(R.id.pb);
        tv_pb_info = bottom.findViewById(R.id.tv_pb_info);

        //restore bottom layout when back to this page.
        getChildFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getChildFragmentManager().getBackStackEntryCount() == 0) {
                    mainInterface.restoreBottomLayout();
                    mainInterface.addBottomLayout(R.layout.bottom_progressbar);
                    View bottom = mainInterface.getBottomLayout();
                    pb = bottom.findViewById(R.id.pb);
                    tv_pb_info = bottom.findViewById(R.id.tv_pb_info);

                    pb.setMax(len);
                    pb.setProgress(succeed);
                    pb.setSecondaryProgress(failed + succeed);
                    double p = succeed * 100.0 / len;
                    double e = failed * 100.0 / len;
                    tv_pb_info.setText(String.format(Locale.ENGLISH, "Fresco Cached: %.2f%%, failed: %.2f%%", p, e));
                    if (imageCards.size() == succeed) {
                        CLog.Companion.i(TAG, "All the images are cached!");
                    }
                }

            }
        });

        prefetchToDiskCache();
    }


    private void callFragment(int id, Bundle bundle) {
        CLog.Companion.d(TAG, "call " + id);
        Fragment fragment = null;
        String tag = "";
        String title = "";
        switch (id) {
            case Constants.P05_Gallery:
                title = "P05_Gallery";
                fragment = P05_Gallery.newInstance(true);
                fragment.setArguments(bundle);
                tag = "P05";
                break;
            case Constants.P11_FRESCO:
                title = "P11_Fresco";
                fragment = P11_Fresco.newInstance(true);
                fragment.setArguments(bundle);
                tag = "P11";
                break;

        }
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fl_container, fragment, tag);
        transaction.addToBackStack(title);
        transaction.commitAllowingStateLoss();
    }


    private List<ImageCard> imageCards;
    private PrefetchSubscriber subscriber;
    private int succeed, failed;
    private int len;

    private void prefetchToDiskCache() {
        len = TestData.IMAGES1.length;
        succeed = 0;
        failed = 0;
        pb.setProgress(0);
        tv_pb_info.setText("Wait to download...");
        imageCards = new ArrayList<>();
        subscriber = new PrefetchSubscriber();

        //Let's say those image links are downloaded successfully from an API
        pb.setMax(TestData.IMAGES1.length);
        for (int i = 0; i < TestData.IMAGES1.length; i++) {
            String url = TestData.IMAGES1[i];
            ImageCard ic = new ImageCard(NetworkHelper.Companion.getFileNameFromUrl(url), "fresh", url);
            imageCards.add(ic);
        }
        //cache
        try {
            for (int i = 0; i < imageCards.size(); i++) {
                String url = imageCards.get(i).image;
                ImageRequest imageRequest = ImageRequest.fromUri(url);
                CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest, null);
                if (!ImagePipelineFactory.getInstance().getMainFileCache().hasKey(cacheKey)) {
                    DataSource<Void> ds = Fresco.getImagePipeline().prefetchToDiskCache(ImageRequest.fromUri(url), null);
                    ds.subscribe(subscriber, new DefaultExecutorSupplier(3).forBackgroundTasks());
                } else {
                    succeed++;
                }
            }

            if (succeed == imageCards.size()) {
                double p = succeed * 100.0 / len;
                double e = failed * 100.0 / len;
                pb.setProgress(succeed);
                pb.setSecondaryProgress(failed + succeed);
                tv_pb_info.setText(String.format(Locale.ENGLISH, "Fresco Cached: %.2f%%, failed: %.2f%%", p, e));
            }
        } catch (Exception e) {
            CLog.Companion.e(TAG, "Cache error:" + e.getMessage());
        }
    }

    private class PrefetchSubscriber extends BaseDataSubscriber<Void> {

        @Override
        protected void onNewResultImpl(DataSource<Void> dataSource) {
            CLog.Companion.i(TAG, "observer, succeed:" + succeed);
            ++succeed;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pb.setProgress(succeed);
                    pb.setSecondaryProgress(failed + succeed);
                    double p = succeed * 100.0 / len;
                    double e = failed * 100.0 / len;
                    tv_pb_info.setText(String.format(Locale.ENGLISH, "Fresco Cached: %.2f%%, failed: %.2f%%", p, e));
                    if (imageCards.size() == succeed) {
                        CLog.Companion.i(TAG, "All the images are cached!");
                    }
                }
            });

        }

        @Override
        protected void onFailureImpl(DataSource<Void> dataSource) {
            CLog.Companion.e(TAG, "observer, failed:" + failed);
            ++failed;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    double p = succeed * 100.0 / len;
                    double e = failed * 100.0 / len;
                    pb.setProgress(succeed);
                    pb.setSecondaryProgress(failed + succeed);
                    tv_pb_info.setText(String.format(Locale.ENGLISH, "Fresco Cached: %.2f%%, failed: %.2f%%", p, e));
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        client.release();
        super.onDestroy();
    }
}
