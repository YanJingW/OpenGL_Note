//片段着色器的主要目的就是告诉GPU每个片段的最终颜色应该是什么？
//对于基本图元的每个片段，片段着色器都会被调用一次。

precision mediump float;//定义了所有浮点数据类型的默认精度，可选lowp\mediump\highp

//我们要使用这个uniform设置将要绘制的东西的颜色
uniform vec4 u_Color;

void main() {
    gl_FragColor = u_Color;
}
