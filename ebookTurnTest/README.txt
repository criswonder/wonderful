参考网址:
我的理解:
这个翻页的控件,使用了贝叶斯曲线, 来画翻页的时候卷起来的曲线部分. 要画一条贝叶斯曲线,需要
三个点, 起始点,控制点,结束点.有了这三个点就可以绘制出一条弯的线.
path有专门的函数来处理的mPath0.quadTo 函数说明(Add a quadratic bezier from the last point...)

canvas在clip path的时候和原有的path之间的关系如(Region.Op.XOR, Region.Op.INTERSECT..), 可以参考
ActivityLifeCircle中的clipping例子,实际上也是API种的例子.

在绘制阴影的时候 rotate很重要. 可以理解成旋转画布?

翻了一页的时候为什么touch点的x,y值会不断的变动(然后根据这个点算出新的贝叶斯曲线,然后绘制出新的
区域), 是因为有了scroll这个object. 指定一个起始点和距离,它就可以再规定时间内滚动到这个地方.然后再这个过程中
你可以获得不断变化的(x,Y)

Android 实现书籍翻页效果----原理篇
http://blog.csdn.net/hmg25/article/details/6306479

http://blog.csdn.net/hmg25/article/details/6342539
Android 实现书籍翻页效果----完结篇

http://www.ibm.com/developerworks/cn/opensource/os-cn-android-anmt2/index.html?ca=drs-
Android 动画框架详解，第 2 部分