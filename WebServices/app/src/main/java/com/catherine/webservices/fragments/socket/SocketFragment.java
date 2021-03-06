package com.catherine.webservices.fragments.socket;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.catherine.webservices.Constants;
import com.catherine.webservices.R;
import com.catherine.webservices.adapters.TextCardRVAdapter;
import com.catherine.webservices.components.DialogManager;
import com.catherine.webservices.entities.TextCard;
import com.catherine.webservices.fragments.LazyFragment;
import com.catherine.webservices.interfaces.BackKeyListener;
import com.catherine.webservices.interfaces.MainInterface;
import com.catherine.webservices.interfaces.OnItemClickListener;
import com.catherine.webservices.interfaces.OnRequestPermissionsListener;
import com.catherine.webservices.toolkits.CLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2017/9/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class SocketFragment extends LazyFragment {
    public final static String TAG = SocketFragment.class.getSimpleName();
    private List<TextCard> entities;
    private SwipeRefreshLayout srl_container;
    private MainInterface mainInterface;
    private TextCardRVAdapter adapter;

    public static SocketFragment newInstance(boolean isLazyLoad) {
        Bundle args = new Bundle();
        args.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, isLazyLoad);
        SocketFragment fragment = new SocketFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.f_socket);
        mainInterface = (MainInterface) getActivity();
        init();
    }

    private void init() {
        mainInterface.getPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                mainInterface.setBackKeyListener(new BackKeyListener() {
                    @Override
                    public void OnKeyDown() {
                        if (getChildFragmentManager().getBackStackEntryCount() > 0) {
                            getChildFragmentManager().popBackStack();
                            mainInterface.restoreBottomLayout();
                        } else {
                            mainInterface.removeBackKeyListener();
                            mainInterface.backToPreviousPage();
                        }
                    }
                });
                fillInData();
                initComponent();
            }

            @Override
            public void onDenied(@Nullable List<String> deniedPermissions) {
                StringBuilder context = new StringBuilder();
                if (deniedPermissions != null) {
                    for (String p : deniedPermissions) {
                        context.append(p);
                        context.append(", ");
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
        entities = new ArrayList<>();
        entities.add(new TextCard("TCP Socket", "TCP socket transmission on blocking thread.", null));
        entities.add(new TextCard("NIO Socket", "TCP socket transmission on non-blocking thread (channel).", null));
        entities.add(new TextCard("UDP Socket", "UDP socket transmission on non-blocking thread.", null));
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

        RecyclerView rv_main_list = (RecyclerView) findViewById(R.id.rv_main_list);
        rv_main_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TextCardRVAdapter(getActivity(), entities, new OnItemClickListener() {
            @Override
            public void onItemClick(@NotNull View view, int position) {
                switch (position) {
                    case 0:
                        callFragment(Constants.Fragments.F_BLOCKING_SOCKET);
                        break;
                    case 1:
                        callFragment(Constants.Fragments.F_NIO_SOCKET);
                        break;
                    case 2:
                        callFragment(Constants.Fragments.F_UDP_SOCKET);
                        break;
                }
            }

            @Override
            public void onItemLongClick(@NotNull View view, int position) {

            }
        });
        rv_main_list.setAdapter(adapter);
    }

    private void callFragment(int id) {
        CLog.d(TAG, "call " + id);
        Fragment fragment = null;
        String tag = Constants.Fragments.TAG(id);
        String title = Constants.Fragments.TITLE(id);
        switch (id) {
            case Constants.Fragments.F_BLOCKING_SOCKET:
                fragment = BlockingSocketFragment.newInstance(true);
                break;
            case Constants.Fragments.F_NIO_SOCKET:
                fragment = NIO_SocketFragment.newInstance(true);
                break;
            case Constants.Fragments.F_UDP_SOCKET:
                fragment = UDP_SocketFragment.newInstance(true);
                break;
        }
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fl_container, fragment, tag);
        transaction.addToBackStack(title);
        transaction.commitAllowingStateLoss();
    }

}
