//片段着色器的主要目的就是告诉GPU每个片段的最终颜色应该是什么？
//对于基本图元的每个片段，片段着色器都会被调用一次。


attribute vec4 a_Position;
attribute vec4 a_Color;

//varying是一个特殊的变量类型，它把给它的那些值进行混合，并把这些混合后的值发送给片段着色器
varying vec4 v_Color;

void main() {
    v_Color = a_Color;
    gl_Position = a_Position;
    gl_PointSize = 10.0;//屏幕上所显示的点的大小，否则画点会出不来
}