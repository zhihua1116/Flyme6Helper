package com.coderstory.FTool.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.coderstory.FTool.R;

import java.util.List;

public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.MyViewHolder> {

    private Context context;
    private List<PackageInfo> packages;
    private List<AppInfo> appInfoList;

    public AppInfoAdapter(Context context, List<PackageInfo> packages, List<AppInfo> appInfoList){
        this.context = context;
        this.packages = packages;
        this.appInfoList = appInfoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_info_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        //将创建的View注册点击事件
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    //注意这里使用getTag方法获取position
                    mOnItemClickListener.onItemClick(view,(int)view.getTag());
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnItemLongClickListener != null) {
                    //注意这里使用getTag方法获取position
                    mOnItemLongClickListener.onItemLongClick(view,(int)view.getTag());
                }
                return true;
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);

        PackageInfo packageInfo = packages.get(position);

        if (packageInfo.applicationInfo.enabled) {
            AppInfo appInfo = new AppInfo(packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString(), packageInfo.applicationInfo.loadIcon(context.getPackageManager()), packageInfo.packageName, false, String.valueOf(packageInfo.versionName));
            appInfoList.add(appInfo);
        } else {
            AppInfo appInfo = new AppInfo(packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString(), packageInfo.applicationInfo.loadIcon(context.getPackageManager()), packageInfo.packageName, true, String.valueOf(packageInfo.versionName));
            appInfoList.add(appInfo);
        }
        AppInfo appInfo = appInfoList.get(position);

        if (appInfo.getDisable()) {
            //view.setBackgroundColor(Color.parseColor("#d0d7d7d7")); //冻结的颜色
            holder.view.setBackgroundColor(0xffc1e0f4); //冻结的颜色
            holder.divider.setBackgroundColor(0xffc1e0f4);
        } else {
            //view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary)); //正常的的颜色
            holder.view.setBackgroundColor(0xffffffff); //正常的的颜色
            holder.divider.setBackgroundColor(0xfff5f5f5);
        }

        holder.icon.setImageDrawable(appInfo != null ? appInfo.getImageId() : null);
        holder.name.setText(appInfo.getName());
        holder.packName.setText(appInfo.getPackageName());
    }

    @Override
    public int getItemCount() {
        return packages == null ? 0 : packages.size();
    }

    private OnItemClickListener mOnItemClickListener = null;
    private OnItemLongClickListener mOnItemLongClickListener = null;

    //define interface
    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view , int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView name;
        TextView packName;
        View divider;
        View view;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            icon = view.findViewById(R.id.app_icon);
            name = view.findViewById(R.id.app_name);
            packName = view.findViewById(R.id.app_package_name);
            divider = view.findViewById(R.id.divider);
        }
    }
}

/* ListView Adapter

public class AppInfoAdapter extends ArrayAdapter {
    private int resourceId;


    public AppInfoAdapter(Context context, int textViewResourceId, List<AppInfo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public
    @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        AppInfo appInfo = (AppInfo) getItem(position);

        View view;
        ViewHolder vh;
        if (convertView != null) { //查询布局是否已经缓存
            view = convertView;
            vh = (ViewHolder) view.getTag();//重新获取ViewHolder

        } else {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null); //读取items.xml文件并实例化
            vh = new ViewHolder();
            vh.myImage = (ImageView) view.findViewById(R.id.app_icon);//查找items实例中的myimage
            vh.myText = (TextView) view.findViewById(R.id.app_name);//查找items实例中的mytext
            view.setTag(vh); //保存到view中
        }

        vh.myText.setTag(appInfo != null ? appInfo.getPackageName() : null);
        vh.myImage.setImageDrawable(appInfo != null ? appInfo.getImageId() : null);
        //vh.myText.setText(" 应用名 : " + appInfo.getName() + "\r\n 版本号 : " + appInfo.getVersion());
        vh.myText.setText(appInfo.getName() + "\r\n" + appInfo.getPackageName());
        if (appInfo.getDisable()) {
            //view.setBackgroundColor(Color.parseColor("#d0d7d7d7")); //冻结的颜色
            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary)); //冻结的颜色
        } else {
            //view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary)); //正常的的颜色
            view.setBackgroundColor(0xffffffff); //正常的的颜色
        }
        return view;
    }

    private class ViewHolder {
        ImageView myImage;
        TextView myText;
    }
}
*/