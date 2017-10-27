package com.qihoo.ai.openglnote;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by wangyanjing on 2017/10/26.
 */

public class GlUtil {

    private static final String TAG = GlUtil.class.getName();

    /**
     * 把着色器链接进OpenGL的程序
     *
     * 理解OpenGL程序：一个OpenGL程序就是把一个顶点着色器和一个片段着色器连接在一起变成单个对象
     */
    public static int linkProgram(int vertexShader, int pixelShader) {

        //1.新建程序对象，programId为程序对象的ID;id为0表示对象创建失败
        int programId = GLES20.glCreateProgram();
        checkGlError("glCreateProgram");
        if (programId == 0) {
            Log.e(TAG, "Could not create programId");
        }
        //2.附上着色器：把顶点着色器和片段着色器附到程序对象上
        GLES20.glAttachShader(programId, vertexShader);
        checkGlError("glAttachShader");
        GLES20.glAttachShader(programId, pixelShader);
        checkGlError("glAttachShader");
        //3.链接程序
        GLES20.glLinkProgram(programId);
        //检查链接成功还是失败
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0);
        //4.验证链接状态并返回程序对象ID
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Could not link programId: ");
            Log.e(TAG, GLES20.glGetProgramInfoLog(programId));
            GLES20.glDeleteProgram(programId);
            programId = 0;
        }
        return programId;
    }


    /**
     * Checks to see if a GLES error has been raised.
     */
    public static void checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            Log.e(TAG, msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * Compiles the provided shader source.
     *
     * @return A handle to the shader, or 0 on failure.
     */
    public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        checkGlError("glCreateShader type=" + shaderType);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader " + shaderType + ":");
            Log.e(TAG, " " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    public static boolean validateProgram(int program) {
        GLES20.glValidateProgram(program);

        int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);
        return validateStatus[0]!=0;
    }
}
