package com.yjing.openglnote;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.qihoo.ai.openglnote.R;

public class MainActivity extends AppCompatActivity {

    /**
     * GLSurfaceView线程问题：
     * GLSurfaceView会在一个单独的线程中调用渲染器的方法，默认情况下GLSurfaceView会以显示器的刷新频率不断地渲染
     * 既然GLSurfaceView在后台线程中执行渲染，要注意，只能在渲染线程调用OpenGL，在Android主线程中使用UI相关的调用；
     * 两个线程之间的通讯方式如下：
     * 1.在主线程GLSurfaceView实例可以调用queueEvent()方法传递一个Runnable给后台渲染线程；
     * 2.渲染线程可以调用Activity的runOnUiThread()来传递事件给主线程。
     */
    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        glSurfaceView = new GLSurfaceView(this);

        glSurfaceView.setRenderer(new DemoRenderer(this));

        //把GLSurfaceView加入到屏幕上
        setContentView(glSurfaceView);

    }


    //*********************************************

    /**
     * 将GLSurfaceView的生命周期绑定到activity的生命周期
     * 这样surface视图才能正确暂停并继续后台渲染线程，同时释放和续用OpenGL上下文
     */
    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }
    //*************************************************
}
