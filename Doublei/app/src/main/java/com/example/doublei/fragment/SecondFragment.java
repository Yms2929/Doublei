package com.example.doublei.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.doublei.Setting.Alarm;
import com.example.doublei.Setting.ChildPicture;
import com.example.doublei.R;
import com.example.doublei.Setting.Hospital;
import com.example.doublei.Setting.PhoneDistance;
import com.example.doublei.Setting.PreviousRecord;
import com.example.doublei.Setting.Profile;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends Fragment {
    ListView list;
    String[] titles = {"아이 프로필", "눈 사진", "기록 보기", "병원 찾기", "알람 설정", "핸드폰 거리 메세지", "기타"};
///    String[] titles = {"My profile","Eye Pictures","Record","Hospital","Alarm","Distane Message","Settings"};
    int[] imgs = {R.drawable.ic_iconbaby,
            R.drawable.ic_iconeye,
            R.drawable.ic_calender,
            R.drawable.hospital,
            R.drawable.ic_alarmclock,
            R.drawable.ic_smartphone,
            R.drawable.settings};

    public SecondFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second,container,false);
        list = (ListView)view.findViewById(R.id.settingMenu);

        MyAdapter myAdapter = new MyAdapter(getActivity(), titles, imgs);
        list.setAdapter(myAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        startActivity(new Intent(getActivity(), Profile.class));
                        break;
                    case 1:
                        startActivity(new Intent(getActivity(), ChildPicture.class));
                        break;
                    case 2:
                        startActivity(new Intent(getActivity(), PreviousRecord.class));
                        break;
                    case 3:
                        startActivity(new Intent(getActivity(), Hospital.class));
                        break;
                    case 4:
                        startActivity(new Intent(getActivity(), Alarm.class));
                        break;
                    case 5:
                        startActivity(new Intent(getActivity(), PhoneDistance.class));
                        break;
                    default:
                        break;
                }
            }
        });

        return view;
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        int[] imgs;
        String myTitles[];

        MyAdapter(Context c, String[] titles, int[] imgs){
            super(c, R.layout.list, R.id.text, titles);
            this.context = c;
            this.imgs = imgs;
            this.myTitles = titles;
        }

        @Override
        public View getView(int posistion, View concertView, ViewGroup parent){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            concertView = layoutInflater.inflate(R.layout.list, null);
            ImageView imageView = (ImageView) concertView.findViewById(R.id.imageIcon);
            TextView myTitle = (TextView) concertView.findViewById(R.id.settingTitle);
            imageView.setImageResource(imgs[posistion]);
            myTitle.setText(titles[posistion]);

            return concertView;
        }
    }
}