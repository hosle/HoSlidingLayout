# HoSlidingLayout 侧滑栏

Author ： hosle

Created in 3rd May 2018 

## 主要特点

Android v4控件包提供了两种侧滑栏：

* ```android.support.v4.widget.DrawerLayout``` ： 侧滑栏滑出时，覆盖在主界面之上，不影响主界面的位置与布局；

* ```android.support.v4.widget.SlidingPaneLayout``` ： 滑动手指，主界面随手势移动。侧栏出现在主界面之下，位置与布局固定不动。

区别于以上两者，HoSlidingLayout具有以下主要特点：

* 一指拖动，从屏幕左**边缘拉出**侧滑栏；
* 侧滑栏与主界面**同步右移**；
* 主界面随移动增加**渐隐效果**；
* 具备**回弹**与**跟进**效果。

## 使用方法


### 1. 在布局XML中直接使用
* 与布局内，设定第一个子View为侧滑栏，第二个子View为主页面的根布局

```
<com.hosle.slidinglayout.HoSlidingLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

	<!-- 侧滑栏根布局 -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    </LinearLayout>
	
	<!-- 主界面根布局 -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    </LinearLayout>

</com.hosle.slidinglayout.HoSlidingLayout>

```


### 2. 常用属性

属性名称|类型|描述
---|--- |---
slidable | Boolean | 是否允许侧滑
defaultMaxOffsetX | Float | 设置侧滑栏宽度，通过调用公有方法修改 ```fun setSlidingWidth(width:Int)```
edgeThreshold | Int | 侧滑边缘热区阈值
slideCoverAlpha | Float | 侧滑栏展开后，主页面的覆盖蒙层透明度
slideCoverColor | Int | 侧滑栏展开后，主页面的蒙层颜色

### 3. 添加回调
* 宿主外部可以获取侧滑栏不同状态的回调

```
sliding_layout.addOnSlidingListener(object : HoSlidingLayout.OnSlidingListener {
            override fun onStart(isOpenNow: Boolean) {
                //todo Layout获取焦点，进入滑动状态
            }
            override fun onSliding() {
                //todo 正在滑动
            }
            override fun onOpen() {
                //todo 侧滑栏已经完全展开
            }
            override fun onClose() {
                //todo 侧滑栏已经完全隐藏
            }
        })
```

### 4. 直接开闭侧栏

* 展开侧栏
```
fun openSlideBoard()
```
* 关闭侧栏
```
fun closeSlideBoard()
```


## 关键拆解

### 手势事件TouchEvent的拦截策略
* 与是否可滑动```slidable```、是否已打开```isSlideOpen```有关


### View的滑动处理

1. requestLayout()与scrollTo()的选择
2. 解决步长、总偏移量与默认偏移量的关系
3. 利用Scroller实现回弹与跟进效果


### 不能忽略onMeasure与onLayout
* 测量SlidingLayout下的主元素

### 渐隐处理
* 利用 drawChild()方法

 ```override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean```

## Todo
* 直接在XML中添加自定义属性
* 拓展上右下其余三个方向的侧边栏滑出
* 新增修改回弹与跟进的动画时间（Duration） API
* 针对宽屏，新增修改回弹临界线位置的API

## License

Copyright (C) 2018. Henry Tam (hosle)

Contact: hosle@163.com

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

