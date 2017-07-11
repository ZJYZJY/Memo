package com.donutcn.memo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.donutcn.memo.R;
import com.donutcn.memo.adapter.SearchListAdapter;
import com.donutcn.memo.listener.OnItemClickListener;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.memo.view.ListViewDecoration;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements OnItemClickListener {

    private RecyclerView mRecyclerView;
    private EditText mSearch_et;
    private SearchListAdapter mAdapter;

    private String mKeyWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSearch_et = (EditText) findViewById(R.id.toolbar_search);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new ListViewDecoration(this, R.dimen.item_decoration, 8, 8));

        mSearch_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    mKeyWords = mSearch_et.getText().toString().trim();
                    Toast.makeText(SearchActivity.this, mKeyWords, Toast.LENGTH_SHORT).show();
                    startSearch();
                    return true;
                }
                return false;
            }
        });
    }

    public void startSearch(){
        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            dataList.add("我是第" + i + "个。");
        }
        mAdapter = new SearchListAdapter(dataList);
        mAdapter.setOnItemClickListener(this);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int position) {

    }

    public void onBack(View view){
        finish();
    }

    public void onCancel(View view){
        mSearch_et.getText().clear();
    }
}
