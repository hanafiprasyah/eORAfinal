package com.example.eorafinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listTitle;
    private HashMap<String, Contact> expandableListDetails;

    public CustomExpandableListAdapter(Context context, List<String> listTitle, HashMap<String, Contact> expandableListDetails) {
        this.context = context;
        this.listTitle = listTitle;
        this.expandableListDetails = expandableListDetails;
    }

    @Override
    public int getGroupCount() {
        return this.listTitle.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listTitle.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.expandableListDetails.get(this.listTitle.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String name = (String)getGroup(groupPosition);
        Contact contact = (Contact)getChild(groupPosition,0);
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group,null);
        }

        TextView txtName = convertView.findViewById(R.id.txtGroupName);
        TextView txtNumber = convertView.findViewById(R.id.txtGroupNumber);

        txtName.setText(name);
        txtNumber.setText(contact.getNumero());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        Contact contact = (Contact)getChild(groupPosition,childPosition);
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item,null);
        }

        CircleImageView circleImageView = convertView.findViewById(R.id.circle_image);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), contact.getImg());
        circleImageView.setImageBitmap(bitmap);

        LinearLayout layoutEmail = convertView.findViewById(R.id.LL_email);
        LinearLayout layoutNotel = convertView.findViewById(R.id.LL_wa);

        layoutEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                String[] recipients = {"prasyah1998@gmail.com"};
                i.putExtra(Intent.EXTRA_EMAIL, recipients);
                i.setType("text/html");
                i.setPackage("com.google.android.gm");
                context.startActivity(Intent.createChooser(i,"Send Mail"));

            }
        });

        layoutNotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.whatsapp.com/send?phone=" + "+6282174651666";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

        Animation animation = AnimationUtils.loadAnimation(context,R.anim.fade_in);
        convertView.startAnimation(animation);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
