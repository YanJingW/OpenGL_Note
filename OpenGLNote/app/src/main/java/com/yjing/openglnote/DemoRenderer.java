package com.yjing.openglnote;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.qihoo.ai.openglnote.GlUtil;
import com.qihoo.ai.openglnote.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by wangyanjing on 2017/10/26.
 */

class DemoRenderer implements GLSurfaceView.Renderer {
    
    private static final int BYTES_PER_FLOAT = 4;

    private final FloatBuffer vertexData;
    private final Context context;

    public DemoRenderer(Context context) {
        super();
        this.context = context;
        
        //顶点数据,两个三角形描述一个长方形
        //注：以逆时针的顺序排列顶点，称卷曲顺序
        // TODO: 2017/10/26 使用一致的卷曲顺序，可以优化性能：
        // todo 使用卷曲顺序可以指出一个三角形属于任何给定物体的前面或者后面，OpenGL可以忽略那些无论如何都无法被看到的后面的三角形
        float[] tableVerticesWithTriangle = {
                //三角形1
                0f,0f,
                9f,14f,
                0f,14,

                //三角形2
                0f,0f,
                9f,0f,
                9f,14f,
        };


        //把内存从java堆复制到本地堆
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangle.length * BYTES_PER_FLOAT)//分配一块本地内存，不会被垃圾回收机制管理
                .order(ByteOrder.nativeOrder())//告诉字节缓冲区按照本地字节序组织它的内容
                .asFloatBuffer();

        vertexData.put(tableVerticesWithTriangle);

    }

    /**
     * 发生在应用程序第一次运行时
     * 当设备被唤醒或者从其他activity切换回来时，这个方法也可能被调用
     * @param gl
     * @param config
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置清空屏幕用的颜色
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        //读取glsl字符串
        String vertexShaderStr = RawResourceReader.readTextFileFromRawResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderStr = RawResourceReader.readTextFileFromRawResource(context, R.raw.simple_fragment_shader);

        //加载着色器代码
        int vertexShader = GlUtil.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderStr);
        int fragmentShader = GlUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderStr);
        //创建OpenGL程序对象，链接着色器代码
        int program = GlUtil.linkProgram(vertexShader, fragmentShader);

        GlUtil.validateProgram(program);

        //告诉openGL在绘制任何东西的时候要使用这里定义的程序
        GLES20.glUseProgram(program);

    }

    /**
     * 在surface被创建后，每次surface尺寸变化时，这个方法都会被GLSurfaceView调用到。eg.横竖屏切换
     * @param gl
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视口大小，告诉OpenGL可以用来渲染的surface的大小
        GLES20.glViewport(0,0,width,height);

    }

    /**
     * 每绘制一帧时，这个方法都会被调用。
     * 在这个方法中，一定要绘制一些东西，即使只是清空屏幕
     * todo test.因为：在这个方法返回后，渲染缓冲区会被交换并显示到屏幕上，如果什么都没有画，可能会看到糟糕的闪烁效果
     * @param gl
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        //清空屏幕，擦除屏幕上的所有颜色，并用之前glClearColor()调用定义的颜色充满整个屏幕
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

    }
}