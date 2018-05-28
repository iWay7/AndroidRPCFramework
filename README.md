# AndroidRPCFramework
Android 远程过程调用框架，属于业务层框架，底层基于 URLConnection 实现，易实现高并发，多系统兼容，完全自定义等。

### 本应用的示例

![image](https://github.com/iWay7/AndroidRPCFramework/blob/master/sample.gif)   

### 本示例基于 AndroidHelpers 库，访问 https://github.com/iWay7/AndroidHelpers 添加依赖。

其实 AndroidHelpers 库提供的仅仅是执行引擎以及一个基础请求类。事实上，您可以基于此实现任何形式的业务层框架。</br>
多数公司后台业务系统可能包含多个实现，在绝对意义上，我们并没有办法在客户端上使用同一套业务层框架。</br>
因此，在抽象上，我们只能抽象出一个执行引擎统一管理请求以及一个最基础的请求类来定义执行引擎的处理过程。</br>
至于每个业务系统具体怎么做事，我们完全可以基于此，对每个业务系统做单独的实现。这样做的好处在于：</br>
1、业务系统独立。对于一个业务系统的修改，不会牵扯到其他系统。</br>
2、应用层统一。在应用层使用，有着统一的实现，可以大幅度减少新员工的学习成本。</br>
3、单独调试。可以调试单个接口，例如打断点，而不阻断其他接口，大幅度提升调试效率。</br>
4、可完全自定义。如果想要，可以在任何一个地方实现自定义的请求，然后把请求交给执行引擎处理。</br>

#### 开始使用（事实上您可以直接拷贝本示例中的相关代码到您的项目中修改以减少时间）：
##### 首先在 Application 的 onCreate 方法中初始化执行引擎：
```
@Override
public void onCreate() {
    super.onCreate();
    RPCEngine.initialize(2);
}
```

##### 建立基础的请求类及其回调：
```
详见 site.iway.androidrpcframework.network.RPCBaseReq
详见 site.iway.androidrpcframework.network.RPCCallback
```

##### 建立针对业务的基础请求类及返回类数据结构：
```
详见 site.iway.androidrpcframework.network.mymusic.MyMusicReq
详见 site.iway.androidrpcframework.network.mymusic.MyMusicRes
```

##### 声明一个业务层请求：
```
public class ListSongsReq extends MyMusicReq {

    public ListSongsReq() {
        url += "ListSongs";
        responseClass = ListSongsRes.class;
    }

    @Expose
    public String filter;

}
```

##### 声明一个业务层响应：
```
public class ListSongsRes extends MyMusicRes {

    public List<String> fileNames;
    public List<String> playList;

}
```

##### 请求调用示例：
```
ListSongsReq listSongsReq = new ListSongsReq();
listSongsReq.filter = "周杰伦";
listSongsReq.start(MainActivity.this);
```

##### 处理回调示例：
```
@Override
public void onRequestOK(RPCBaseReq req) {
    ListSongsRes listSongsRes = (ListSongsRes) req.response;
    TextView textView = findViewById(R.id.textView);
    textView.setText(Arrays.toString(listSongsRes.fileNames.toArray()));
}

@Override
public void onRequestER(RPCBaseReq req) {
    Toast.makeText(this, req.error.toString(), Toast.LENGTH_SHORT).show();
}
```

##### 关于基础请求类 RPCReq 的介绍：
```
/**
 * 远程过程调用执行引擎需要的请求基类，基于此扩展出其他形式的请求。
 */
public abstract class RPCReq {

    /**
     * 请求地址。对于已经发起的请求，修改此地址是无效的。
     */
    public String url;

    /**
     * 最低延迟。定义从请求开始到通知 UI 线程最少的时间间隔，内部是通过 Handler 处理，无需担心阻塞。
     */
    public long minDelayTime;

    /**
     * 将本请求交给执行引擎处理。
     */
    public final void start();

    /**
     * 取消本请求。
     */
    public final void cancel();

    /**
     * 在这里编写准备阶段做的事情，例如计算签名，准备要传输的数据等。
     *
     * @throws Exception 处理过程如果出现异常，您可以抛出异常，在 onError 阶段可以统一处理。
     */
    protected void onPrepare() throws Exception;

    /**
     * 在这里编写需要对 HttpURLConnection 进行的更改，例如设置请求头，设定 Cookie 等。
     *
     * @param connection 执行引擎创建的 HttpURLConnection 实例。
     * @throws Exception 处理过程如果出现异常，您可以抛出异常，在 onError 阶段可以统一处理。
     */
    protected void onConnect(HttpURLConnection connection) throws Exception;

    /**
     * 在这里编写数据的交互逻辑，可以获取 OutputStream 发送数据，获取 InputStream 读取数据等。
     *
     * @param connection 执行引擎创建的 HttpURLConnection 实例。
     * @throws Exception 处理过程如果出现异常，您可以抛出异常，在 onError 阶段可以统一处理。
     */
    protected void onConnected(HttpURLConnection connection) throws Exception;

    /**
     * 在这里编写交互完成之后的操作。
     */
    protected void onFinish();

    /**
     * 在这里编写交互完成之后在 UI 线程上的操作。
     */
    protected void onFinishUI();

    /**
     * 在这里编写交互过程出错之后的操作。您可以在这里保存异常，并在之后交给回调方法。
     *
     * @param e 代表连接失败、服务器返回 HTTP 错误或在之前阶段抛出的其他异常。
     */
    protected void onError(Exception e);

    /**
     * 在这里编写交互过程出错之后在 UI 线程上的操作。
     */
    protected void onErrorUI();

    /**
     * 在这里编写所有操作完成之后的处理，例如释放 IO 资源。
     */
    protected void onFinally();

}
```

##### 关于执行引擎 RPCEngine 的介绍：
```
/**
 * 远程过程调用执行引擎，最好在 Application 的 onCreate() 方法中进行。
 */
public class RPCEngine {

    /**
     * 初始化执行引擎。
     *
     * @param processorCount 代表处理器数量，加大此数量可增加并发。
     */
    public static void initialize(int processorCount);

}
```

##### 基于以上方法构建一套多系统业务层框架的方法可以详细阅读本示例源码。
