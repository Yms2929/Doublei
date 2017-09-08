package com.example.doublei.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.doublei.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.util.ArrayList;

public class FirstFragment extends Fragment {

    GraphView graph1,graph2,graph3,graph4;
    public FirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_first,container,false);
        ListView listView = (ListView) view.findViewById(R.id.listView);

        ArrayList<String> items = new ArrayList<>();
        items.add("count");
        items.add("distance");
        items.add("time");
        items.add("blink");

        CustomAdapter adapter = new CustomAdapter(getActivity(), 0, items);
        listView.setAdapter(adapter);
//        return inflater.inflate(R.layout.fragment_first, container, false);
        return view;
    }
    private class CustomAdapter extends ArrayAdapter<String> {
        private ArrayList<String> items;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
            this.items = objects;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.graphs, null);
            }

            // graph 인스턴스
            graph1 = (GraphView) v.findViewById(R.id.graph1);

            // 리스트뷰의 아이템에 이미지를 변경한다.
            if("count".equals(items.get(position)))
            {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                        new DataPoint(0, 1),
                        new DataPoint(1, 5),
                        new DataPoint(2, 3),
                        new DataPoint(3, 2),
                        new DataPoint(4, 6)
                });
                graph1.addSeries(series);
            }
            else if("distance".equals(items.get(position)))
            {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                        new DataPoint(0, 3),
                        new DataPoint(1, 3),
                        new DataPoint(2, 3),
                        new DataPoint(3, 3),
                        new DataPoint(4, 3)
                });
                graph1.addSeries(series);
            }
            else if("time".equals(items.get(position)))
            {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                        new DataPoint(0, 1),
                        new DataPoint(1, 1),
                        new DataPoint(2, 1),
                        new DataPoint(3, 1),
                        new DataPoint(4, 1)
                });
                graph1.addSeries(series);
            }
            else if("blink".equals(items.get(position)))
            {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                        new DataPoint(0, 2),
                        new DataPoint(1, 2),
                        new DataPoint(2, 2),
                        new DataPoint(3, 2),
                        new DataPoint(4, 2)
                });
                graph1.addSeries(series);
            }
            return v;
        }
    }
}