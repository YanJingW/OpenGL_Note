package com.yjing.openglnote;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

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

    //和simple_fragment_shader.glsl中的uniform vec4 u_Color;是对应的
    public static final String A_COLOR = "a_Color";
    //和simple_vertex_shader.glsl中的attribute vec4 a_Position;是对应的
    public static final String A_POSITION = "a_Position";
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    public static final int STRIDE = (POSITION_COMPONENT_COUNT+COLOR_COMPONENT_COUNT)*BYTES_PER_FLOAT;

    private int aColorLocation;
    private int aPositionLocation;

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
//                //三角形1
//                -0.5f,-0.5f,
//                0.5f,0.5f,
//                -0.5f,0.5f,
//
//                //三角形2
//                -0.5f,-0.5f,
//                0.5f,-0.5f,
//                0.5f,0.5f,
                //重新定义三角形的顶点
                0f,0f,1f,1f,1f,
                -0.5f,-0.5f,0.7f,0.7f,0.7f,
                0.5f,-0.5f,0.7f,0.7f,0.7f,
                0.5f,0.5f,0.7f,0.7f,0.7f,
                -0.5f,0.5f,0.7f,0.7f,0.7f,
                -0.5f,-0.5f,0.7f,0.7f,0.7f,


                //线1
                -0.5f,0f,1f,0f,0f,
                0.5f,0f,1f,0f,0f,

                //木槌
                0f,-0.25f,0f,0f,1f,
                0f,0.25f,1f,0f,0f
        };
        //openGL中坐标定义
//  (-1,1) ———————————————————————————————(1,1)
//        |                               |
//        |                               |
//        |         手机屏幕                |
//        |                               |
//        |                               |
//        |              .(0.0)           |
//        |                               |
//        |                               |
//        |                               |
//        |                               |
//        |                               |
//        |                               |
//  (-1,-1)————————————————————————————————(1,-1)


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
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        //1.加载OpenGL程序及着色器代码
        //读取glsl字符串
        String vertexShaderStr = RawResourceReader.readTextFileFromRawResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderStr = RawResourceReader.readTextFileFromRawResource(context, R.raw.simple_fragment_shader);

        //加载着色器代码
        int vertexShader = GlUtil.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderStr);
        int fragmentShader = GlUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderStr);
        //创建OpenGL程序对象，链接着色器代码
        int program = GlUtil.linkProgram(vertexShader, fragmentShader);

        GlUtil.validateProgram(program);
        GlUtil.checkGlError("validateProgram");

        //告诉openGL在绘制任何东西的时候要使用这里定义的程序
        GLES20.glUseProgram(program);
        GlUtil.checkGlError("glUseProgram");

        //获取uniform的位置, 并把这个位置存入uColorLocation
        aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
        GlUtil.checkGlError("glGetAttribLocation");

        //获取属性的位置,有了这个位置，就能告诉OpenGL去哪里找到这个属性对应的数据
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        GlUtil.checkGlError("glGetAttribLocation");

        //2.读取顶点数据
        vertexData.position(0);//确保从缓冲区的开头读取数据。每个缓冲区都有一个内部指针可以通过调用position()来移动它
        //告诉openGL可以在缓冲区vertexData找到属性a_Position对应的数据。
        //参数：1.属性位置；2.表示vertexData中几个分量表示一个点；3.表数据类型；5.告诉opengl每个位置之间有多少个字节；6.表数据源
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GlUtil.checkGlError("glVertexAttribPointer");
        //使能顶点数组
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        vertexData.position(POSITION_COMPONENT_COUNT);
        GLES20.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GlUtil.checkGlError("glVertexAttribPointer");
        GLES20.glEnableVertexAttribArray(aColorLocation);
        GlUtil.checkGlError("glEnableVertexAttribArray");
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
        GlUtil.checkGlError("glClear");

        //3.5在屏幕上绘制
        //更新着色器代码中的u_Color的值。与属性不同，uniform的分量没有默认值
//        GLES20.glUniform4f(aColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        GlUtil.checkGlError("glUniform4f");
        //开始绘制三角形，从第0个点取6个点，共画两个三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
        GlUtil.checkGlError("glDrawArrays");

//        GLES20.glUniform4f(aColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        GlUtil.checkGlError("glUniform4f");
        //开始绘制一条执行，从第0个点取2个点
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);
        GlUtil.checkGlError("glDrawArrays");

//        GLES20.glUniform4f(aColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        //开始绘制两个点，从第8个点开始取1个点
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);

//        GLES20.glUniform4f(aColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        //开始绘制两个点，从第9个点开始取1个点
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);

    }
}
