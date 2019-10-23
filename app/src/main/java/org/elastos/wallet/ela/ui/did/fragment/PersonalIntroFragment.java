package org.elastos.wallet.ela.ui.did.fragment;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.elastos.wallet.R;
import org.elastos.wallet.ela.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class PersonalIntroFragment extends BaseFragment {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.et_intro)
    EditText etIntro;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_personalintro;
    }

    @Override
    protected void initInjector() {

    }

    @Override
    protected void initView(View view) {
        tvTitle.setText(getString(R.string.addpersonalintro));
        tvTitleRight.setVisibility(View.VISIBLE);
    }


    @OnClick({R.id.tv_next,R.id.tv_title_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_title_right:
            case R.id.tv_next:
                start(SocialAccountFragment.class);
                break;


        }
    }


}
