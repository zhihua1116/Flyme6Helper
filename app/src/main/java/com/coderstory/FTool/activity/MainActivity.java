package com.coderstory.FTool.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.coderstory.FTool.R;
import com.coderstory.FTool.fragment.BlogFragment;
import com.coderstory.FTool.fragment.CleanFragment;
import com.coderstory.FTool.fragment.DisbaleAppFragment;
import com.coderstory.FTool.fragment.DonationFragment;
import com.coderstory.FTool.fragment.HostsFragment;
import com.coderstory.FTool.fragment.LogFragment;
import com.coderstory.FTool.fragment.ThemePatchFragment;
import com.coderstory.FTool.utils.MyConfig;
import com.coderstory.FTool.utils.root.SuHelper;

import ren.solid.library.fragment.WebViewFragment;
import ren.solid.library.utils.SnackBarUtils;
import ren.solid.library.utils.ViewUtils;

import static com.coderstory.FTool.R.id.navigation_view;
import static com.coderstory.FTool.utils.MyConfig.kIsRoot;
import static com.coderstory.FTool.utils.MyConfig.kIsSupport;
import static com.coderstory.FTool.utils.app.AppUtils.isSupport;
import static com.coderstory.FTool.utils.file.FileHelper.setReadable;
import static com.coderstory.FTool.utils.root.SuHelper.canRunRootCommands;

public class MainActivity extends BaseActivity {

    public static final long MAX_DOUBLE_BACK_DURATION = 1600;
    public static String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;//侧边菜单视图
    private Toolbar mToolbar;
    private NavigationView mNavigationView;//侧边菜单项
    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;
    private MenuItem mPreMenuItem;
    private long lastBackKeyDownTick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        //mFragmentManager = getSupportFragmentManager();
        mFragmentManager = getFragmentManager();
    }

    private void check(){
        new Thread() {
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                if(sp.getBoolean(kIsRoot, false) && sp.getBoolean(kIsSupport, false)){
                    return;
                }
                sp.edit().putBoolean(kIsRoot, canRunRootCommands()).apply();
                sp.edit().putBoolean(kIsSupport, isSupport(MainActivity.this)).apply();
            }
        }.start();
    }

    private void performCodeWithPermission(){
        performCodeWithPermission("请求获取读写文件权限", new BaseActivity.PermissionCallback(){
            @Override
            public void hasPermission() {
                //Log.i(TAG, "have permission");
            }

            @Override
            public void noPermission() {
                SnackBarUtils.makeLong(mNavigationView, "未获取到权限！").show();
                Log.i(TAG, "no permission");
            }
        },Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void setUpView() {
        mToolbar = $(R.id.toolbar);
        mDrawerLayout = $(R.id.drawer_layout);
        mNavigationView = $(navigation_view);

        if (!isEnable()) {
            SnackBarUtils.makeLong(mNavigationView, "Xposed 插件尚未激活，Xposed 功能将不可用！").show();
        }

        mToolbar.setTitle("主题和谐");

        //这句一定要在下面几句之前调用，不然就会出现点击无反应
        setSupportActionBar(mToolbar);
        setNavigationViewItemClickListener();
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mToolbar.setNavigationIcon(R.drawable.ic_drawer_home);
        initDefaultFragment();
        //取消滚动条
        NavigationView v = (NavigationView) findViewById(R.id.navigation_view);
        v.setEnabled(false);
        v.setClickable(false);
        if (v != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) v.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
        check();
        performCodeWithPermission();
    }

    //init the default checked fragment
    private void initDefaultFragment() {
        mCurrentFragment = ViewUtils.createFragment(ThemePatchFragment.class);
        mFragmentManager.beginTransaction().add(R.id.frame_content, mCurrentFragment).commit();

    }

    public boolean isEnable() {
        return false;
    }

    private void setNavigationViewItemClickListener() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (null != mPreMenuItem) {
                    mPreMenuItem.setChecked(false);
                }

                if (MyConfig.isProcessing) {
                    SnackBarUtils.makeShort(mDrawerLayout, getString(R.string.isWorkingTips)).danger();
                    return false;
                }

                switch (item.getItemId()) {
                    case R.id.navigation_item_blockads:
                        mToolbar.setTitle("主题和谐");
                        switchFragment(ThemePatchFragment.class);
                        break;
                    case R.id.navigation_item_hosts:
                        mToolbar.setTitle("Hosts设置");
                        switchFragment(HostsFragment.class);
                        break;
                    case R.id.navigation_item_Clean:
                        mToolbar.setTitle("应用清理");
                        switchFragment(CleanFragment.class);
                        break;
                    case R.id.navigation_item_disableapps:
                        mToolbar.setTitle("冻结应用");
                        switchFragment(DisbaleAppFragment.class);
                        break;
                        /*
                    case R.id.navigation_item_ManagerApp:
                        mToolbar.setTitle("应用管理");
                        switchFragment(ManagerAppFragment.class);
                        break;
                    case R.id.navigation_item_settings:
                        mToolbar.setTitle("其他设置");
                        switchFragment(SettingsFragment.class);
                        break;
                        */
                    case R.id.navigation_item_log:
                        mToolbar.setTitle("日志");
                        switchFragment(LogFragment.class);
                        break;
                    case R.id.navigation_item_donation:
                        mToolbar.setTitle("捐赠");
                        switchFragment(DonationFragment.class);
                        break;
                    default:
                        break;
                }
                item.setChecked(true);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                mPreMenuItem = item;
                return false;
            }
        });
    }

    //切换Fragment
    private void switchFragment(Class<?> clazz) {
        Fragment to = ViewUtils.createFragment(clazz);
        if (to.isAdded()) {
            //Log.i(TAG, "Added");
            //mFragmentManager.beginTransaction().hide(mCurrentFragment).show(to).commitAllowingStateLoss();
            mFragmentManager.beginTransaction().replace(mCurrentFragment.getId(), to).commit();
        } else {
            //Log.i(TAG, "Not Added");
            //mFragmentManager.beginTransaction().hide(mCurrentFragment).add(R.id.frame_content, to).commitAllowingStateLoss();
            mFragmentManager.beginTransaction().replace(mCurrentFragment.getId(), to).commit();
        }
        mCurrentFragment = to;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivityWithoutExtras(AboutActivity.class);
                break;
            case R.id.action_reboot:
                SuHelper.showTips("busybox killall system_server", getString(R.string.Tips_HotBoot), this);
                break;
            case R.id.action_blog:
                mToolbar.setTitle("CoderStory的博客");
                switchFragment(BlogFragment.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setReadable(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setReadable(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        setReadable(this);
    }

    @Override
    public void onBackPressed() {
        setReadable(this);
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {//当前抽屉是打开的，则关闭
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }

        if (mCurrentFragment instanceof WebViewFragment) {//如果当前的Fragment是WebViewFragment 则监听返回事件
            WebViewFragment webViewFragment = (WebViewFragment) mCurrentFragment;
            if (webViewFragment.canGoBack()) {
                webViewFragment.goBack();
                return;
            }
        }

        long currentTick = System.currentTimeMillis();
        if (currentTick - lastBackKeyDownTick > MAX_DOUBLE_BACK_DURATION) {
            SnackBarUtils.makeShort(mDrawerLayout, "再按一次退出！").info();
            lastBackKeyDownTick = currentTick;
        } else {
            finish();
            System.exit(0);
        }
    }
}
