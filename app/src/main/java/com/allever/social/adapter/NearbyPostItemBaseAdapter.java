package com.allever.social.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.allever.social.R;
import com.allever.social.activity.RecruitDataActivity;
import com.allever.social.pojo.NearByPostItem;
import com.allever.social.pojo.NearByRecruitItem;
import com.allever.social.utils.WebUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by XM on 2016/5/21.
 * 附近招聘列表项适配器
 */
public class NearbyPostItemBaseAdapter extends BaseAdapter {

    private List<NearByPostItem> list_nearbyPostItem;
    private Context context;
    private LayoutInflater inflater;

    public NearbyPostItemBaseAdapter(Context context,List<NearByPostItem> list_nearbyPostItem){
        this.context = context;
        this.list_nearbyPostItem = list_nearbyPostItem;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        final NearByPostItem nearByPostItem = list_nearbyPostItem.get(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null){
            view = inflater.inflate(R.layout.nearby_post_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tv_postname = (TextView)view.findViewById(R.id.id_near_by_post_item_tv_postname);
            viewHolder.iv_head = (CircleImageView)view.findViewById(R.id.id_near_by_post_item_iv_head);
            viewHolder.tv_distance = (TextView)view.findViewById(R.id.id_near_by_post_item_tv_distance);
            viewHolder.btn_dail = (ButtonRectangle)view.findViewById(R.id.id_near_by_post_item_btn_dail);
            viewHolder.tv_salary = (TextView)view.findViewById(R.id.id_near_by_post_item_tv_salary);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        Glide.with(context)
                .load(WebUtil.HTTP_ADDRESS + nearByPostItem.getUser_head_path())
                .into(viewHolder.iv_head);
        viewHolder.tv_postname.setText(nearByPostItem.getPostname());
        viewHolder.tv_salary.setText(nearByPostItem.getSalary());
        viewHolder.tv_distance.setText("距离：" + nearByPostItem.getDistance() + " km");

        viewHolder.iv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RecruitDataActivity.class);
                intent.putExtra("recruit_id", nearByPostItem.getRecruit_id());
                context.startActivity(intent);
                //Toast.makeText(this, "id = " + list_myRecruitItem.get(i).getId(), Toast.LENGTH_LONG).show();
            }
        });

        //viewHolder.btn_dail.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        if (nearByPostItem.getIs_owner()==0){
            viewHolder.btn_dail.setVisibility(View.VISIBLE);
        }else{
            viewHolder.btn_dail.setVisibility(View.INVISIBLE);
        }


        viewHolder.btn_dail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("RecruitItemBase", "phone = " + nearByPostItem.getPhone());
                Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + nearByPostItem.getPhone()));
                context.startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public int getCount() {
        return list_nearbyPostItem.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return list_nearbyPostItem.get(i);
    }

    class ViewHolder{
        CircleImageView iv_head;
        TextView tv_postname;
        TextView tv_salary;
        TextView tv_distance;
        ButtonRectangle btn_dail;
    }
}
