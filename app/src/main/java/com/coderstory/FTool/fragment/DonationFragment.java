package com.coderstory.FTool.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.coderstory.FTool.R;

import ren.solid.library.fragment.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class DonationFragment extends BaseFragment {
    public static String TAG = "DonationFragment";

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_donation;
    }

    @Override
    protected void setUpView() {
        super.setUpView();
        $(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlipay("aex06874ga0wkuhwuajgzb8");
            }
        });

        $(R.id.imageView1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlipay("FKX03149H8YOUWESHOCEC6");
            }
        });
    }

    /**
     * 打开支付宝
     *
     * @param qrcode
     */
    private void goToAlipay(String qrcode) {
        Log.i(TAG, "Thanks your support!");
        try {
            getActivity().getPackageManager().getPackageInfo("com.eg.android.AlipayGphone", PackageManager.GET_ACTIVITIES);
            goToUrl("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https://qr.alipay.com/" + qrcode + "%3F_s%3Dweb-other&_t=" + System.currentTimeMillis());
        } catch (PackageManager.NameNotFoundException e) {
            goToUrl("https://qr.alipay.com/" + qrcode);
        }
    }

    /**
     * 打开 Url
     *
     * @param url
     */
    private void goToUrl(String url) {
        Intent intent = new Intent();
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        try {
            getActivity().startActivity(intent);
        } catch (Exception e) {
            Log.e("DonationFragment", e.getMessage());
        }
    }
}
