package com.example.doublei.fragment;


import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.doublei.R;

public class HomeFragment extends Fragment {

    ImageView imageView;
    boolean buttonOnOff =false;
    boolean Off = false;
    boolean On =true;
    Button btnFace;
    Button ToastImage;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        imageView=(ImageView) view.findViewById(R.id.faceimage);
        btnFace = (Button) view.findViewById(R.id.eye);
        ToastImage = (Button) view.findViewById(R.id.toast);
        btnFace.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(buttonOnOff==Off) {
                    imageView.setImageResource(R.drawable.trans_girlon);
                    ((TransitionDrawable) imageView.getDrawable()).startTransition(1000);
                    buttonOnOff=On;
                }
                else if(buttonOnOff==On){
                    imageView.setImageResource(R.drawable.trans_girloff);
                    ((TransitionDrawable) imageView.getDrawable()).startTransition(1000);
                    buttonOnOff=Off;
                }
            }
        });
        ToastImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast();
            }
        });
        return view;
    }
    private void showToast(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.toast_closeeye,null);
        Toast toast = new Toast(getActivity());
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL| Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();

    }
}
