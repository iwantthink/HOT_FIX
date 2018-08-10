package com.ryan.hotfix;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SampleActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnLongClickListener {

    private String TAG = SampleActivity.class.getSimpleName();

    Button mButton;

    LinearLayout mLlSeconed;

    AppCompatButton mBtnModify;

    ListView mLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        mLv = findViewById(R.id.listview);

        mLv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    TextView view = new TextView(SampleActivity.this);
                    view.setText(position + "");
                    convertView = view;

                } else {

                    TextView view = (TextView) convertView;
                    view.setText(position + "");
                }

                return convertView;
            }
        });

        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        mLlSeconed = findViewById(R.id.ll_seconed);

        mBtnModify = new AppCompatButton(SampleActivity.this);
        mBtnModify.setText("i am be added");
        mBtnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        final TextView textView = findViewById(R.id.tv_title);

        textView.setOnClickListener(new ClickListener());

        mButton = findViewById(R.id.btn_test);

        mButton.setOnClickListener(this);

        mButton.setOnLongClickListener(this);

    }


    public void btnadd(View view) {

        mLlSeconed.addView(mBtnModify, 0);
    }

    public void btndelete(View view) {

        mLlSeconed.removeView(mBtnModify);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }
}
