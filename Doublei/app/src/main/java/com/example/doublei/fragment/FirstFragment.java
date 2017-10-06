package com.example.doublei.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.doublei.R;
import com.example.doublei.GraphLibrary.CharterBar;
import com.example.doublei.GraphLibrary.CharterLine;
import com.example.doublei.GraphLibrary.CharterXLabels;
import com.example.doublei.GraphLibrary.CharterYLabels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class FirstFragment extends Fragment {
    View view;
    private static final int TYPE_Line = 1;
    private static final int TYPE_Bar = 2;

    private static final int DEFAULT_ITEMS_COUNT = 7;
    private static final int DEFAULT_RANDOM_VALUE_MIN = 10;
    private static final int DEFAULT_RANDOM_VALUE_MAX = 100;

    private float[] lineYValues;
    private float[] lineValues;
    private float[] barYValues;
    private float[] barValues;
    private String[] barandlineXValues;
    int[] barColors;
    String strDate = "";
    private String strDateForline = "";

    int strabismusCount;

    class GraphInformation {
        String graphName;
        String safeDangerMessage;

        GraphInformation(String graphName, String safeDangerMessage) {
            this.graphName = graphName;
            this.safeDangerMessage = safeDangerMessage;
        }

        String getGraphName() {
            return this.graphName;
        }

        String getSafeDangerMessage() {
            return this.safeDangerMessage;
        }
    }

    private List<GraphInformation> Graphs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_first, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        initializeData();

        barandlineXValues = barandlineFillXValues(DEFAULT_ITEMS_COUNT, DEFAULT_RANDOM_VALUE_MAX, DEFAULT_RANDOM_VALUE_MIN); // line Value
        lineYValues = lineFillYValues(DEFAULT_ITEMS_COUNT, DEFAULT_RANDOM_VALUE_MAX, DEFAULT_RANDOM_VALUE_MIN); // line Value
        lineValues = lineFillYValues(DEFAULT_ITEMS_COUNT, DEFAULT_RANDOM_VALUE_MAX, DEFAULT_RANDOM_VALUE_MIN); // line Value
        barYValues = barFillYValues(DEFAULT_ITEMS_COUNT, DEFAULT_RANDOM_VALUE_MAX, DEFAULT_RANDOM_VALUE_MIN); // bar Value
        barValues = barFillYValues(DEFAULT_ITEMS_COUNT, DEFAULT_RANDOM_VALUE_MAX, DEFAULT_RANDOM_VALUE_MIN); // bar Value
        Resources res = getResources();
        barColors = new int[]{
                res.getColor(R.color.lightBlue500), res.getColor(R.color.lightBlue400),
                res.getColor(R.color.lightBlue300)
        };
        recyclerView.setAdapter(new RecyclerAdapter(getActivity().getApplicationContext(), Graphs, R.layout.fragment_first));
        return view;
    }

    private void initializeData() { //Textview 초기화
        Graphs = new ArrayList<>();
        Graphs.add(new GraphInformation("사시 의심 횟수", "안전"));
        Graphs.add(new GraphInformation("스마트폰 사용 시간", "안전"));
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        Context context;
        List<GraphInformation> items;
        int item_layout;

        public RecyclerAdapter(Context context, List<GraphInformation> items, int item_layout) {
            this.context = context;
            this.items = items;
            this.item_layout = item_layout;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View lineView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_uselinegraph, null);
            View barView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_usetimegraph, null);
            switch (viewType) {
                case TYPE_Line:
                    return new ViewHolderLine(lineView);
                case TYPE_Bar:
                    return new ViewHolderBar(barView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final GraphInformation item = items.get(position);
            if (holder.getItemViewType() == TYPE_Bar) {
                ViewHolderBar mholder = (ViewHolderBar) holder;
                mholder.charterBarLabelX.setStickyEdges(false);
                mholder.charterBarLabelX.setValues(barandlineXValues);

                mholder.charterBarYLabel.setValues(barYValues, true);

                mholder.charterBarWithLabel.setValues(barValues);
                mholder.charterBarWithLabel.setColors(barColors);

                mholder.graphName.setText(item.getGraphName());
                mholder.safeDangerMessage.setText(item.getSafeDangerMessage());
            } else if (holder.getItemViewType() == TYPE_Line) {
                ViewHolderLine mholder = (ViewHolderLine) holder;
                mholder.charterLineLabelX.setStickyEdges(true);
                mholder.charterLineLabelX.setValues(barandlineXValues);

                mholder.charterLineYLabel.setValues(lineYValues, true); // false로 규정

                mholder.charterLineWithLabel.setValues(lineValues);

                mholder.graphName.setText(item.getGraphName());
                mholder.safeDangerMessage.setText(item.getSafeDangerMessage());
            }
        }

        @Override
        public int getItemCount() {
            return this.items.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? TYPE_Line : TYPE_Bar;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View v) {
                super(v);
            }
        }

        public class ViewHolderBar extends ViewHolder {
            TextView graphName, safeDangerMessage;
            CardView cardview;
            CharterBar charterBarWithLabel;
            CharterXLabels charterBarLabelX;
            CharterYLabels charterBarYLabel;

            public ViewHolderBar(View itemView) {
                super(itemView);
                graphName = (TextView) itemView.findViewById(R.id.GraphName);
                safeDangerMessage = (TextView) itemView.findViewById(R.id.safeDanger);
                cardview = (CardView) itemView.findViewById(R.id.Cardview);
                charterBarWithLabel = (CharterBar) itemView.findViewById(R.id.charter_bar_with_XLabel);
                charterBarLabelX = (CharterXLabels) itemView.findViewById(R.id.charter_bar_XLabel);
                charterBarYLabel = (CharterYLabels) itemView.findViewById(R.id.charter_bar_YLabel);
            }
        }

        public class ViewHolderLine extends ViewHolder {
            TextView graphName, safeDangerMessage;
            CardView cardview;
            CharterLine charterLineWithLabel;
            CharterXLabels charterLineLabelX;
            CharterYLabels charterLineYLabel;

            public ViewHolderLine(View itemView) {
                super(itemView);
                graphName = (TextView) itemView.findViewById(R.id.GraphName);
                safeDangerMessage = (TextView) itemView.findViewById(R.id.safeDanger);
                cardview = (CardView) itemView.findViewById(R.id.Cardview);
                charterLineWithLabel = (CharterLine) itemView.findViewById(R.id.charter_line_with_XLabel);
                charterLineLabelX = (CharterXLabels) itemView.findViewById(R.id.charter_line_XLabel);
                charterLineYLabel = (CharterYLabels) itemView.findViewById(R.id.charter_line_YLabel);
            }
        }
    }

    private float[] lineFillYValues(int length, int max, int min) {
        float[] newlineValues = new float[length];

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일"); // 월, 일 받아오기
        Calendar calendar = new GregorianCalendar();

        for (int i = -6; i < 1; i++) {
            calendar.setTime(date); // 오늘 날짜로 다시 Calendar 변경
            calendar.add(Calendar.DAY_OF_MONTH, i);
            Date calculatedDate = calendar.getTime();
            strDateForline = sdf.format(calculatedDate);

            SharedPreferences strabismusCountPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            SharedPreferences.Editor strabismusCountEditor = strabismusCountPref.edit();
            strabismusCount = strabismusCountPref.getInt(strDateForline + "strabismusCount", 0);

            // if 문으로 strabismusCount / 6 해서 프레임수로 나눠줘야 됨.

            newlineValues[i + 6] = Float.valueOf(strabismusCount);
        }
        return newlineValues;
    }

    private String[] barandlineFillXValues(int length, int max, int min) {
        String[] newBarVlaues = new String[length];

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Calendar calendar = new GregorianCalendar();

        for (int i = -6; i < 1; i++) {
            calendar.setTime(date); // 오늘 날짜로 다시 Calendar 변경
            calendar.add(Calendar.DAY_OF_MONTH, i);
            Date calculatedDate = calendar.getTime();
            strDate = sdf.format(calculatedDate);

            newBarVlaues[i + 6] = String.valueOf(Integer.parseInt(strDate)) + "일";
//            newBarValues[i + 6] = Float.valueOf(strDate);
        }
        return newBarVlaues;
    }

    private float[] barFillYValues(int length, int max, int min) {
        float[] newBarValues = new float[length]; // bar 값
        String[] newBarStringValues = new String[length];
        int time = 0;

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일"); // 월, 일 받아오기
        Calendar calendar = new GregorianCalendar();

        for (int i = -6; i < 1; i++) {
            calendar.setTime(date); // 오늘 날짜로 다시 Calendar 변경
            calendar.add(Calendar.DAY_OF_MONTH, i);
            Date calculatedDate = calendar.getTime();
            strDate = sdf.format(calculatedDate);

            newBarStringValues[i + 6] = strDate;
            SharedPreferences timePref = this.getActivity().getSharedPreferences(strDate, Context.MODE_PRIVATE);
            SharedPreferences.Editor timeEditor = timePref.edit();
            String checkDate = timePref.getString(strDate, "0"); // key 값으로 dateTemp를 주고 그에 해당하는 Value를 가져옴. 없으면 공백("")
            time = Integer.valueOf(checkDate);
            if (time > 60) {
                time = time / 60;
                newBarValues[i + 6] = Float.valueOf(time);
            } else {
                time = 0;
                newBarValues[i + 6] = Float.valueOf(time);
            }
        }
        return newBarValues;
    }
}