package fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.health_community.R;

import java.util.ArrayList;
import java.util.List;

import activity.AirQualityAty;
import activity.BodyTemperaAty;
import activity.HealthActivity2;
import activity.LoginAty;
import activity.XinDianAty;
import activity.XueTangAty;
import activity.XueYangAty;
import adapter.MyBaseAdapter;
import bean.HealthItemBean;

/**
 * A simple {@link Fragment} subclass.
 */
public class HealthFragment extends Fragment {
    private static final String TAG = "HealthFragment";
    private ListView lv_item;
    private MyBaseAdapter myAdaper;
    private ArrayList<Class> attachClass;

    public HealthFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_health, null);
        init(view);
        Intent intent = new Intent(getActivity(), XueTangAty.class);
        return view;
    }

    private void init(View view) {
        attachClass = new ArrayList<Class>();
        attachClass.add(XueYangAty.class);
        attachClass.add(XinDianAty.class);
        attachClass.add(XueTangAty.class);
        attachClass.add(BodyTemperaAty.class);
        attachClass.add(AirQualityAty.class);


        lv_item = (ListView) view.findViewById(R.id.lv_item);
        ArrayList<HealthItemBean> items = inflateBean();
        myAdaper = new MyBaseAdapter(getActivity(), items);
        lv_item.setAdapter(myAdaper);
        lv_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= attachClass.size()) {
                    return;
                }
                Intent intent = new Intent(getActivity(), attachClass.get(position));
                startActivity(intent);
            }
        });
    }

    private ArrayList<HealthItemBean> inflateBean() {
        String[] itemNames = getResources().getStringArray(R.array.health_item);
        int[] ivResId = new int[]{R.drawable.a0, R.drawable.a1, R.drawable.a2,
                R.drawable.a3, R.drawable.a4, R.drawable.a5, R.drawable.a6};
        ArrayList<HealthItemBean> itemBeans = new ArrayList<HealthItemBean>();
        for (int i = 0; i < itemNames.length; i++) {
            HealthItemBean item = new HealthItemBean(ivResId[i], itemNames[i]);
            itemBeans.add(item);
        }
        return itemBeans;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }

}
