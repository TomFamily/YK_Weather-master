# 代码解析：	

**1、Gson库：**
（又称Google Gson）是Google公司发布的一个开放源代码的Java库，主要用途为序列化Java对象为JSON字符串，或反序列化JSON字符串成Java对象。
* Gson的两个基础方法：
```
public String toJson(Object src)
public <T> T fromJson(String json, Class<T> classOfT)
```
* [Android 库 Gson - 简书 (jianshu.com)](https://www.jianshu.com/p/cb37ce4bbb6d)


**2、xUtils框架：包含了很多实用的android工具。主要有四个模块的功能：**

* HttpUtils模块：
>* 支持同步，异步方式的请求；
>* 支持大文件上传，上传大文件不会oom；
>* 支持GET，POST，PUT，MOVE，COPY，DELETE，HEAD，OPTIONS，TRACE，CONNECT请求；
>* 下载支持301/302重定向，支持设置是否根据Content-Disposition重命名下载的文件；
>* 返回文本内容的请求(默认只启用了GET请求)，支持缓存，可设置默认过期时间和针对当前请求的过期时间。

* BitmapUtils模块：
>* 加载bitmap的时候无需考虑bitmap加载过程中出现的oom和android容器快速滑动时候出现的图片错位等现象；
>* 支持加载网络图片和本地图片；
>* 内存管理使用lru算法，更好的管理bitmap内存；
>* 可配置线程加载数量，缓存大小，缓存路径，加载显示动画等...

*  ViewUtils模块：
>* android中的ioc框架，完全注解方式就可以进行UI，资源和事件绑定；
>* 新的事件绑定方式，使用混淆工具混淆后仍可正常工作；
>* 目前支持常用的20种事件绑定，参见ViewCommonEventListener类和包com.lidroid.xutils.view.annotation.event。

 * DbUtils模块：
>* android中的orm框架，一行代码就可以进行增删改查；
>* 支持事务，默认关闭；
>* 可通过注解自定义表名，列名，外键，唯一性约束，NOT NULL约束，CHECK约束等（需要混淆的时候请注解表名和列名）；
>* 支持绑定外键，保存实体时外键关联实体自动保存或更新；
>* 自动加载外键关联实体，支持延时加载；
>* 支持链式表达查询，更直观的查询语义，参考下面的介绍或sample中的例子。

* [Android开源库:xUtils框架](https://blog.csdn.net/nishigesb123/article/details/90344837)


2、用过定位获取手机位置： [android经纬度转城市](https://blog.csdn.net/iblade/article/details/85267522)

## 代码结构：
#### 数据库文件夹： 
* BuildDataBase类：
 BuildDataBase继承于SQLiteOpenHelper；在其构造方法中调用父类方法创建数据库：
```
    public BuildDataBase(Context context){
        super(context,"forecast.db",null,1);
    }
```
继承于SQLiteOpenHelper默认有两个需要实现的方法：
```
    @Override
    public void onCreate(SQLiteDatabase db) {
//        创建表的操作: 城市id，城市名，城市描述
        String sql = "create table info(_id integer primary key autoincrement,city varchar(20) unique not null,content text not null)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  }
```
* DataBaseManager类：
在这个类的initDB方法中调用了BuildDataBase类，初始化数据库：
```
    public static SQLiteDatabase database;
    /* 初始化数据库信息*/
    public static void initDB(Context context){
        BuildDataBase buildDataBase= new BuildDataBase(context);
        database = buildDataBase.getWritableDatabase();
    }
```
这个类中主要是封装了对数据库表的操作：
查找数据库中的城市列表、替换对应的城市信息、新增（删除）一条城市记录、根据城市名获取其在数据库中的消息、获取数据库中的城市个数、获取数据库中的全部信息（城市id，城市名，城市描述）、删除表中的所有数据
* DatabaseBean类：
封装城市数据：城市id，城市名，城市描述

#### 网络数据文件夹：
* HttpUtils类：
封装了一个方法：传入路径，通过传入的路径获取网络数据，将数据以string的形式返回：
```
public class HttpUtils {
    public static String getJsonData(String path) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            int read = 0;
            byte[] arr = new byte[1024];
            while ((read = inputStream.read(arr)) != -1) {
                byteArrayOutputStream.write(arr, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }
}
```
* URLUtils类：
里面有获取温度和生活指数的连接，也封装了两个方法：传入城市名，返回城市温度和城市生活指数 
* TempBean类：
封装温度数据，三层封装：
result: { 城市名，实时温度：{ 天气信息 } ，未来五天温度：{ {某一天的天气情况} ，{某一天的天气情况} } }
* IndexBean类：
* 封装生活指数数据，三层封装：
result: { 城市，life：{ 空调：{ 描述，描述 }，下雨：{描述，描述} } }

#### Activity文件夹：
* BaseActivity类：
封装了一个方法：
```
public void loadData(String url){
       //  xutils库中的类
        RequestParams params = new RequestParams(url);
        x.http().get(params,this);
    }
````
继承Callback.CommonCallback<String>接口，实现它的方法，监听网络请求的状态

* CityManagerActivity类，城市管理界面：
使用listView展示以添加的城市信息，给它设置CityManagerAdapter类的adapter，监听数据变化。
在onResume方法中监听数据的变化：
```
    @Override
    protected void onResume() {
        super.onResume();
        List<DatabaseBean> list = DBManager.queryAllInfo();
        mDatas.clear();
        mDatas.addAll(list);
        adapter.notifyDataSetChanged();
    }
```

* DeleteCityActivity类，删除页面：
用listView展示城市，用一个list保存数据，用一个deleteList保存被选中的要删除的数据。给listView设置DeleteCityAdapter的adapter。
在getView方法中给holder设置点击事件：当某一个holder被点击后，默认为是要删除这条数据的，删除这条数据在list中的记录，用list重新渲染listView。
```
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_deletecity,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final String city = mDatas.get(position);
        holder.tv.setText(city);
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatas.remove(city);
                deleteCitys.add(city);
                notifyDataSetChanged();  //删除了提示适配器更新
            }
        });
        return convertView;
    }
```
* SearchCityActivity类，搜索界面，继承于BaseActivity（封装了请求数据的方法）：
其中有一个GridView，需要给它设置adapter（arrayAdapter）：
```
GridView.setAdapter(new ArrayAdapter<>(context, 子view的xml文件, 需要展示的数据的数组));
```
当GridView被点击了，就用当前被点击的城市去URLUtils获取该城市的温度URL，再用这个URL到baseActivity的loadData方法中通过xutil库获取数据。
重写baseActivity的onSuccess方法：
```
    @Override
    public void onSuccess(String result) {
        JHTempBean weatherBean = new Gson().fromJson(result, JHTempBean.class);
        if (weatherBean.getError_code()==0) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("city",city);
            startActivity(intent);
        }else{
            Toast.makeText(this,"暂时未收入此城市天气信息...",Toast.LENGTH_SHORT).show();
        }
    }
```
* moreActivity：
 获取当前包的版本：
> PackageManager：包管理者，  可以获取应用图标和应用名称以及包名、应用的权限、sever、activity等等。
```
        /* 获取应用的版本名称*/
        PackageManager manager = getPackageManager();
        String versionName = null;
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
```
 设置APP的背景图片：
>*  RadioGroup 为单项选择按钮组，其中可以包含多个 RadioButton，即单选按钮，它们共同为用户提供一种多选一的选择方式。在多个 RadioButton 被同一个 RadioGroup 包含的情况下，多个 RadioButton 之间自动形成互斥关系，仅有一个可以被选择。单选按钮的使用方法和 CheckBox 的使用方法高度相似，其事件监听接口使用的是 RadioGroup.OnCheckedChangeListener()，使用 setOnCheckedChangeListener() 方法将监听器设置到单选按钮上。
>* 用SharedPreferences保存背景图片的名字，在mainActivity中获取后添加上去
>* SharedPreferences本质是用map进行实现的
>利用Android原生api实现分享功能：
```
    private void shareSoftwareMsg(String s) {
        /* 分享软件的函数*/
        Intent intent = new Intent(Intent.ACTION_SEND);
        //  指定发送数据类型
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,s);
        startActivity(Intent.createChooser(intent,"说天气"));
    }
```
* [利用 Android 系统原生 API 实现分享功能 ](https://www.jianshu.com/p/1d4bd2c5ef69)

* 设置提示语句监听选项：
```
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("确定要删除所有缓存么？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
        // 监听事件
        }).setNegativeButton("取消",null);
```

#### Fragment类文件夹：
* BaseFragment类：
封装了一个方法：
```
public void loadData(String url){
       //  xutils库中的类
        RequestParams params = new RequestParams(url);
        x.http().get(params,this);
    }
````
继承Callback.CommonCallback<String>接口，实现它的方法，监听网络请求的状态
* CityFragmentPagerAdapter类：
  继承于FragmentStatePagerAdapter，实现它的抽象方法：
```
public class CityFragmentPagerAdapter extends FragmentStatePagerAdapter{
    List<Fragment>fragmentList;
    public CityFragmentPagerAdapter(FragmentManager fm,List<Fragment>fragmentLis) {
        super(fm);
        this.fragmentList = fragmentLis;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    int childCount = 0;   //表示ViewPager包含的页数
//    当ViewPager的页数发生改变时，必须要重写两个函数
    @Override
    public void notifyDataSetChanged() {
        this.childCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (childCount>0) {
            childCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
```
*  CityWeatherFragment类中（继承BaseFragment类）：
  通过bundle获取MainActivity中传过来的城市名，用城市名到URLUtils类中获取温度接口，调用BaseFragment的loadData方法传入接口获取数据，实现BaseFragment的监听方法，查看数据是否获取成功
 起一个新的线程去获取天气指数信息（本类自己写的方法）：
```
    private void loadIndexData(final String index_url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String json = HttpUtils.getJsonContent(index_url);
                JHIndexBean jhIndexBean = new Gson().fromJson(json, JHIndexBean.class);
                if (jhIndexBean.getResult()!=null) {
                    lifeBean = jhIndexBean.getResult().getLife();
                }
            }
        }).start();
    }
```
 监听温度获取的onSuccess方法:当数据获取成功了，解析当前数据并显示到fragment上面，同时更新数据库中该城市的消息。如果数据库更新失败，说明该城市在数据库中还没有记录，将该城市加入数据库。
监听onError方法：当温度数据获取失败，改为从数据库获取数据（数据库中的数据是json格式的），然后渲染到fragment上面。

* 数据库只保存了城市未来五天的天气情况，没有保存生活指数


#### MainActivity类：
* 在oncreate方法中：
获取手机位置，然后通过接口获取该位置的天气信息，作为第一个fragment显示。
用一个cityList保存要显示城市的列表，数据来自数据库。
用cityList创建CityWeatherFragment对象，将其保存到fragmentList中。
给ViewPager设置adapter监听器：CityFragmentPagerAdapter
给ViewPager设置监听事件：某一个fragment被选中后，改变下方小圆点的位置
* 在onRestart方法中：
重新去数据库获取城市列表，重新创建fragment序列。
* 在mainActivity中向相应的fragment换入城市名，fragment用这个城市名去获取温度和天气指数数据。