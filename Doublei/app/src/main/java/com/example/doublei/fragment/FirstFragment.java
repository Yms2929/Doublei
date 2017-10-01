package com.example.doublei.Fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doublei.Bar.BarView;
import com.example.doublei.Bar.LineView;
import com.example.doublei.MainActivity;
import com.example.doublei.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

import static com.example.doublei.R.id.age;

public class FirstFragment extends Fragment {
    View view, graph;
    TextView graphName, safeDangerMessage;
    RecyclerView recyclerView;
    class GraphInformation {
        String graphName;
        String safeDangerMessage;

        GraphInformation(String graphName, String safeDangerMessage) {
            this.graphName = graphName;
            this.safeDangerMessage = safeDangerMessage;
        }
        String getGraphName()
        {
            return this.graphName;
        }
        String getSafeDangerMessage()
        {
            return this.safeDangerMessage;
        }
    }

    private List<GraphInformation> Graphs;

    private void initializeData(){
        Graphs = new ArrayList<>();
        Graphs.add(new GraphInformation("사시 의심 횟수", "안전"));
        Graphs.add(new GraphInformation("스마티폰 사용 시간", "안전"));
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_first, container, false);

        RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        initializeData();

        recyclerView.setAdapter(new RecyclerAdapter(getActivity().getApplicationContext(),Graphs,R.layout.fragment_first));
        return view;
    }
    private void initLineView(LineView lineView) {
        ArrayList<String> test = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            test.add(String.valueOf(i + 1));
        }
        lineView.setBottomTextList(test);
        lineView.setColorArray(new int[] {
                Color.parseColor("#F44336"), Color.parseColor("#9C27B0"),
                Color.parseColor("#2196F3"), Color.parseColor("#009688")
        });
        lineView.setDrawDotLine(true);
        lineView.setShowPopup(LineView.SHOW_POPUPS_NONE);
    }
    private void randomSetBar(BarView barView) {
        int random = (int) (Math.random() * 20) + 6;
        ArrayList<String> test = new ArrayList<String>();
        for (int i = 0; i < 8; i++) {
            test.add("test");
            test.add("pqg");
            //            test.add(String.valueOf(i+1));
        }
        barView.setBottomTextList(test);

        ArrayList<Integer> barDataList = new ArrayList<Integer>();
        for (int i = 0; i < random * 2; i++) {
            barDataList.add((int) (Math.random() * 100));
        }
        barView.setDataList(barDataList, 100);
    }

    private void randomSetLine(LineView lineView) {
        ArrayList<Integer> dataList = new ArrayList<>();
        float random = (float) (Math.random() * 9 + 1);
        for (int i = 0; i < 10; i++) {
            dataList.add((int) (Math.random() * random));
        }

//        ArrayList<Integer> dataList2 = new ArrayList<>();
//        random = (int) (Math.random() * 9 + 1);
//        for (int i = 0; i < randomint; i++) {
//            dataList2.add((int) (Math.random() * random));
//        }
//
//        ArrayList<Integer> dataList3 = new ArrayList<>();
//        random = (int) (Math.random() * 9 + 1);
//        for (int i = 0; i < randomint; i++) {
//            dataList3.add((int) (Math.random() * random));
//        }

        ArrayList<ArrayList<Integer>> dataLists = new ArrayList<>();
        dataLists.add(dataList);
//        dataLists.add(dataList2);
//        dataLists.add(dataList3);

        lineView.setDataList(dataLists);
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    List<GraphInformation> items;
    int item_layout;

    public RecyclerAdapter(Context context, List<GraphInformation> items, int item_layout) {
        this.context=context;
        this.items=items;
        this.item_layout=item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_line,null);
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_usetimegraph,null);
        return new ViewHolder(v);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final GraphInformation item=items.get(position);
//        initLineView(holder.lineView);
//        randomSetLine(holder.lineView);
        randomSetBar(holder.graph);
        holder.graphName.setText(item.getGraphName());
        holder.safeDangerMessage.setText(item.getSafeDangerMessage());
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,item.getGraphName(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView graphName, safeDangerMessage;
//        LineView lineView;
        BarView graph;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            graphName=(TextView)itemView.findViewById(R.id.GraphName);
            safeDangerMessage=(TextView)itemView.findViewById(R.id.safeDanger);
            cardview=(CardView)itemView.findViewById(R.id.Cardview);
//            lineView=(LineView) itemView.findViewById(R.id.line_view);
            graph=(BarView) itemView.findViewById(R.id.bar_view);
        }
    }
}
}