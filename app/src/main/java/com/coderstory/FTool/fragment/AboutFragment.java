package com.coderstory.FTool.fragment;

import android.graphics.Paint;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import com.coderstory.FTool.BuildConfig;
import com.coderstory.FTool.R;
import com.coderstory.FTool.utils.licensesdialog.LicensesDialog;
import com.coderstory.FTool.utils.licensesdialog.licenses.ApacheSoftwareLicense20;
import com.coderstory.FTool.utils.licensesdialog.licenses.GnuGeneralPublicLicense20;
import com.coderstory.FTool.utils.licensesdialog.model.Notice;
import com.coderstory.FTool.utils.licensesdialog.model.Notices;

import ren.solid.library.fragment.base.BaseFragment;

/**
 * Created by _SOLID
 * Date:2016/3/30
 * Time:20:03
 */
public class AboutFragment extends BaseFragment {

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_about;
    }

    @Override
    protected void setUpView() {

        TextView version = $(R.id.version_info);
        version.setText("版本号:" + BuildConfig.VERSION_NAME);

        TextView tv_content2 = $(R.id.tv_content_2);
        tv_content2.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        TextView tv_content3 = $(R.id.tv_content_3);
        tv_content3.setAutoLinkMask(Linkify.WEB_URLS);
        tv_content3.setMovementMethod(LinkMovementMethod
                .getInstance());


        $(R.id.os).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Notices notices = new Notices();
                notices.addNotice(new Notice("ApacheSoftwareLicense", "", "", new ApacheSoftwareLicense20()));
                notices.addNotice(new Notice("GnuGeneralPublicLicense", "", "", new GnuGeneralPublicLicense20()));

                new LicensesDialog.Builder(getMContext())
                        .setNotices(notices)
                        //.setIncludeOwnLicense(true)
                        .build()
                        .show();
            }
        });
    }
}
