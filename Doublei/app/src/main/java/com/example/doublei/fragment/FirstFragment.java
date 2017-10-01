package com.example.doublei.Fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doublei.R;
import com.hrules.charter.CharterBar;
import com.hrules.charter.CharterLine;
import com.hrules.charter.CharterXLabels;
import com.hrules.charter.CharterYLabels;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FirstFragment extends Fragment {
    View view;
    private static final int TYPE_Line = 1;
    private static final int TYPE_Bar = 2;

    private static final int DEFAULT_ITEMS_COUNT = 15;
    private static final int DEFAULT_RANDOM_VALUE_MIN = 10;
    private static final int DEFAULT_RANDOM_VALUE_MAX = 100;

    private float[] values;
    int[] barColors;

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

        values =fillRandomValues(DEFAULT_ITEMS_COUNT, DEFAULT_RANDOM_VALUE_MAX, DEFAULT_RANDOM_VALUE_MIN);
        Resources res = getResources();
        barColors = new int[] {
                res.getColor(R.color.lightBlue500), res.getColor(R.color.lightBlue400),
                res.getColor(R.color.lightBlue300)
        };
        recyclerView.setAdapter(new RecyclerAdapter(getActivity().getApplicationContext(), Graphs, R.layout.fragment_first));
        return view;
    }

    private void initializeData() {//Textview 초기화
        Graphs = new ArrayList<>();
        Graphs.add(new GraphInformation("사시 의심 횟수", "안전"));
        Graphs.add(new GraphInformation("스마티폰 사용 시간", "안전"));
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
                mholder.charterBarLabelX.setVisibilityPattern(new boolean[] { true, false });
                mholder.charterBarLabelX.setValues(values);

                mholder.charterBarYLabel.setVisibilityPattern(new boolean[] { true, false });
                mholder.charterBarYLabel.setValues(values, true);

                mholder.charterBarWithLabel.setValues(values);
                mholder.charterBarWithLabel.setColors(barColors);

                mholder.graphName.setText(item.getGraphName());
                mholder.safeDangerMessage.setText(item.getSafeDangerMessage());
                mholder.cardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, item.getGraphName(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (holder.getItemViewType() == TYPE_Line) {
                ViewHolderLine mholder = (ViewHolderLine) holder;
                mholder.charterLineLabelX.setStickyEdges(true);
                mholder.charterLineLabelX.setValues(values);
                mholder.charterLineYLabel.setValues(values, true);
                mholder.charterLineWithLabel.setValues(values);

                mholder.graphName.setText(item.getGraphName());
                mholder.safeDangerMessage.setText(item.getSafeDangerMessage());
                mholder.cardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, item.getGraphName(), Toast.LENGTH_SHORT).show();
                    }
                });
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
                charterBarWithLabel = (CharterBar)itemView.findViewById(R.id.charter_bar_with_XLabel);
                charterBarLabelX = (CharterXLabels)itemView.findViewById(R.id.charter_bar_XLabel);
                charterBarYLabel = (CharterYLabels)itemView.findViewById(R.id.charter_bar_YLabel);
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
                charterLineWithLabel = (CharterLine)itemView.findViewById(R.id.charter_line_with_XLabel);
                charterLineLabelX = (CharterXLabels)itemView.findViewById(R.id.charter_line_XLabel);
                charterLineYLabel = (CharterYLabels)itemView.findViewById(R.id.charter_line_YLabel);
            }
        }
    }
    private float[] fillRandomValues(int length, int max, int min) {
        Random random = new Random();

        float[] newRandomValues = new float[length];
        for (int i = 0; i < newRandomValues.length; i++) {
            newRandomValues[i] = random.nextInt(max - min + 1) - min;
        }
        return newRandomValues;
    }
}