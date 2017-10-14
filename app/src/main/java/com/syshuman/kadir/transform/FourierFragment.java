package com.syshuman.kadir.transform;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;


public class FourierFragment extends Fragment {

    private GLSurfaceView mGLView;

    public FourierFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLView = new GLSurfaceView(this.getActivity());
        mGLView.setRenderer(new MyGLRenderer());
        this.getActivity().setContentView(mGLView);
    }


    public static FourierFragment newInstance(String param1, String param2) {
        FourierFragment fragment = new FourierFragment();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_fourier, container, false);
    }
}
