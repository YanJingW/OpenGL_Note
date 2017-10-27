//片段着色器的主要目的就是告诉GPU每个片段的最终颜色应该是什么？
//对于基本图元的每个片段，片段着色器都会被调用一次。


attribute vec4 a_Position;

void main() {
    gl_Position = a_Position;
    gl_PointSize = 10.0;//屏幕上所显示的点的大小，否则画点会出不来
}