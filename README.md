# chatInputLayoutProject
聊天输入框

## 参考的代码
[deltachat-android
](https://github.com/deltachat/deltachat-android/blob/master/src/org/thoughtcrime/securesms/components/KeyboardAwareLinearLayout.java)

### UI效果
![效果 ](./img/software.gif)

### 简要说明
demo采用2种实现方式

第一种不需要在清单文件配置任何信息，原理，弹窗/输入框出现的时候只需要修改背景图片大小即可。

第二种方式是需要在清单文件配置android:windowSoftInputMode="adjustNothing"，创建一个popuwindow监听viewTree，点击输入框显示宽度为0的popuwindow，监听到改变的时候，修改底部emptyView的高度
