package adapter;

import android.content.Context;
import android.text.style.IconMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.health_community.R;

import java.util.ArrayList;

import bean.HealthItemBean;

/**
 * Created by ein on 2016/3/26.
 */
public class MyBaseAdapter extends BaseAdapter {
    private ArrayList<HealthItemBean> items;
    private Context context;
    private ViewHolder holder;

    public MyBaseAdapter(Context context, ArrayList<HealthItemBean> items) {
        this.context = context;
        this.items = items;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //这里不会产生convertView因为视图是在太小，view不会被回收
        //holder也没有产生作用，只有在listView超过一屏幕，才有优化必要
        View view = LayoutInflater.from(context).inflate(R.layout.custom_health_item, null);
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null){
            TextView name = (TextView) view.findViewById(R.id.tv_health_item_name);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_health_item);
            holder = new ViewHolder();
            holder.title = name;
            holder.icon = iv;
            view.setTag(holder);
        }
        holder.icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        HealthItemBean item = (HealthItemBean) getItem(position);
        holder.title.setText(item.getName());
        holder.icon.setImageResource(item.getIvResId());
        return view;
    }


    static class ViewHolder{
        private TextView title;
        private ImageView icon;
    }
}

