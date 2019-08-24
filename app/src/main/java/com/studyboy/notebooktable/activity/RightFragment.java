package com.studyboy.notebooktable.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.studyboy.notebooktable.R;

public class RightFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container,
                             Bundle saveInstanceState){
        View view=inflater.inflate(R.layout.right_fragment,container,false);
        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
