package view;

import android.content.Context;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.example.health_community.R;

import util.MConfig;

/**
 * Created by ein on 2016/4/21.
 */
public class ChartMenuProvider extends ActionProvider {
    private Context context;
    private OnSelectedListener listener;

    public ChartMenuProvider(Context context) {
        super(context);
        this.context = context;
    }

    public void setOnSelectedListener(OnSelectedListener selectedListener){
        listener = selectedListener;
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        subMenu.clear();
        subMenu.add(R.string.xueyang).setIcon(R.drawable.a0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                listener.onSubMenuItemSelected(item, MConfig.XueTang);
                return true;
            }
        });

        subMenu.add("心电").setIcon(R.drawable.a1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                listener.onSubMenuItemSelected(item, MConfig.XinDian);
                return true;
            }
        });

        subMenu.add("血糖").setIcon(R.drawable.a2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                listener.onSubMenuItemSelected(item, MConfig.XueTang);
                return true;
            }
        });

        subMenu.add("体温").setIcon(R.drawable.a3)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        listener.onSubMenuItemSelected(item, MConfig.TiWen);
                        return true;
                    }
                });
        subMenu.add("空气质量").setIcon(R.drawable.a4).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                listener.onSubMenuItemSelected(item, MConfig.FenChen);
                return true;
            }
        });
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }
    public static interface OnSelectedListener{
        public void onSubMenuItemSelected(MenuItem item, int fileType);
    }
}
