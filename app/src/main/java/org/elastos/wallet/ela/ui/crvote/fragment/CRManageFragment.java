package org.elastos.wallet.ela.ui.crvote.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allen.library.SuperButton;

import org.elastos.did.DIDDocument;
import org.elastos.wallet.R;
import org.elastos.wallet.ela.ElaWallet.MyWallet;
import org.elastos.wallet.ela.base.BaseFragment;
import org.elastos.wallet.ela.bean.BusEvent;
import org.elastos.wallet.ela.db.table.Wallet;
import org.elastos.wallet.ela.rxjavahelp.BaseEntity;
import org.elastos.wallet.ela.rxjavahelp.NewBaseViewData;
import org.elastos.wallet.ela.ui.Assets.activity.TransferActivity;
import org.elastos.wallet.ela.ui.Assets.presenter.WalletManagePresenter;
import org.elastos.wallet.ela.ui.common.bean.CommmonLongEntity;
import org.elastos.wallet.ela.ui.common.bean.CommmonObjEntity;
import org.elastos.wallet.ela.ui.common.bean.CommmonStringEntity;
import org.elastos.wallet.ela.ui.crvote.bean.CRDePositcoinBean;
import org.elastos.wallet.ela.ui.crvote.bean.CRListBean;
import org.elastos.wallet.ela.ui.crvote.bean.CrStatusBean;
import org.elastos.wallet.ela.ui.crvote.presenter.CRManagePresenter;
import org.elastos.wallet.ela.ui.crvote.presenter.CRSignUpPresenter;
import org.elastos.wallet.ela.ui.vote.SuperNodeList.NodeDotJsonViewData;
import org.elastos.wallet.ela.ui.vote.SuperNodeList.NodeInfoBean;
import org.elastos.wallet.ela.ui.vote.SuperNodeList.SuperNodeListPresenter;
import org.elastos.wallet.ela.ui.vote.activity.VoteTransferActivity;
import org.elastos.wallet.ela.utils.AppUtlis;
import org.elastos.wallet.ela.utils.Arith;
import org.elastos.wallet.ela.utils.ClipboardUtil;
import org.elastos.wallet.ela.utils.Constant;
import org.elastos.wallet.ela.utils.DialogUtil;
import org.elastos.wallet.ela.utils.RxEnum;
import org.elastos.wallet.ela.utils.SPUtil;
import org.elastos.wallet.ela.utils.listener.WarmPromptListener;
import org.elastos.wallet.ela.utils.svg.GlideApp;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 选举管理  getRegisteredProducerInfo
 */
public class CRManageFragment extends BaseFragment implements NewBaseViewData {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_url)
    TextView tvUrl;
    DialogUtil dialogUtil = new DialogUtil();
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_did)
    TextView tvDid;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.ll_xggl)
    LinearLayout ll_xggl;
    @BindView(R.id.ll_tq)
    LinearLayout ll_tq;
    @BindView(R.id.sb_tq)
    SuperButton sbtq;
    @BindView(R.id.tv_zb)
    TextView tv_zb;
    @BindView(R.id.sb_up)
    SuperButton sb_up;
    @BindView(R.id.iv_icon)
    AppCompatImageView ivIcon;
    @BindView(R.id.iv_icon1)
    AppCompatImageView ivIcon1;
    @BindView(R.id.line_info)
    View lineInfo;
    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.ll_info)
    LinearLayout llInfo;
    @BindView(R.id.line_intro)
    View lineIntro;
    @BindView(R.id.tv_intro)
    TextView tvIntro;
    @BindView(R.id.tv_quit)
    TextView tvQuit;
    @BindView(R.id.ll_intro)
    LinearLayout llIntro;
    @BindView(R.id.ll_tab)
    LinearLayout llTab;
    @BindView(R.id.tv_intro_detail)
    TextView tvIntroDetail;
    @BindView(R.id.ll_infodetail)
    LinearLayout llInfodetail;

    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    private Wallet wallet;
    CRManagePresenter presenter;
    String status;
    private String CID, DID;
    private String ownerPublicKey;
    private String name, url;
    private long code;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cr_manage;
    }

    @Override
    protected void initInjector() {

    }


    @Override
    protected void initView(View view) {
        setToobar(toolbar, toolbarTitle, getString(R.string.electoral_affairs));

        tvTitleRight.setText(getString(R.string.quitcr));
        registReceiver();
    }


    @Override
    protected void setExtraData(Bundle data) {
        ownerPublicKey = data.getString("publickey");
        wallet = data.getParcelable("wallet");
        CID = data.getString("CID", "");
        status = data.getString("status", "Canceled");
        CrStatusBean.InfoBean info = data.getParcelable("info");
        DID = info.getDID();
        CRListBean.DataBean.ResultBean.CrcandidatesinfoBean curentNode = (CRListBean.DataBean.ResultBean.CrcandidatesinfoBean) data.getSerializable("curentNode");
        if (curentNode == null) {
            return;
        }

        presenter = new CRManagePresenter();
        //这里只会有 "Registered", "Canceled"分别代表, 已注册过, 已注销(不知道可不可提取)
        if (status.equals("Canceled")) {
            //已经注销了
            setToobar(toolbar, toolbarTitle, getString(R.string.electoral_affairs));
            ll_xggl.setVisibility(View.GONE);
            ll_tq.setVisibility(View.VISIBLE);
            tvQuit.setText(curentNode.getNickname() + getString(R.string.hasquit));
            long height = info.getConfirms();
            if (height >= 2160) {
                //获取赎回金额
                presenter.getCRDepositcoin(CID, this);
            }
        } else {
            //Registered 未注销展示选举信息
            onJustRegistered(info, curentNode);
        }
        super.setExtraData(data);
    }

    @OnClick({R.id.tv_url, R.id.sb_zx, R.id.sb_tq, R.id.sb_up, R.id.ll_info, R.id.ll_intro, R.id.tv_title_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sb_zx:
                //更新did
                if (!TextUtils.isEmpty(DID)) {
                    //更新凭证到中心化服务器 todo
                    return;
                }
                //先绑定did  再更新到服务器
                String didString = wallet.getDid();
                if (TextUtils.isEmpty(didString)) {
                    showToast(getString(R.string.notcreatedid));
                    return;
                }
                new WalletManagePresenter().DIDResolveWithTip(didString, this);
                break;
            case R.id.ll_info:
                lineInfo.setVisibility(View.VISIBLE);
                lineIntro.setVisibility(View.GONE);
                tvInfo.setTextColor(getResources().getColor(R.color.whiter));
                tvIntro.setTextColor(getResources().getColor(R.color.whiter50));
                llInfodetail.setVisibility(View.VISIBLE);
                tvIntroDetail.setVisibility(View.GONE);
                break;
            case R.id.ll_intro:
                lineInfo.setVisibility(View.GONE);
                lineIntro.setVisibility(View.VISIBLE);
                tvInfo.setTextColor(getResources().getColor(R.color.whiter50));
                tvIntro.setTextColor(getResources().getColor(R.color.whiter));
                llInfodetail.setVisibility(View.GONE);
                tvIntroDetail.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_url:
                //复制
                ClipboardUtil.copyClipboar(getBaseActivity(), tvUrl.getText().toString());
                break;

            case R.id.tv_title_right:
                dialogUtil.showWarmPrompt2(getBaseActivity(), getString(R.string.quitcrornot), new WarmPromptListener() {
                            @Override
                            public void affireBtnClick(View view) {
                                showWarmPromptInput(Constant.UNREGISTERCR);
                            }
                        }
                );
                break;
            case R.id.sb_tq:
                presenter.createRetrieveCRDepositTransaction(wallet.getWalletId(), MyWallet.ELA, ownerPublicKey,
                        Arith.mulRemoveZero(available, MyWallet.RATE_S).toPlainString(), "", this);

                break;

            case R.id.sb_up:
                start(UpdateCRInformationFragment.class, getArguments());
                break;
        }
    }


    private void showWarmPromptInput(String type) {
        new CRSignUpPresenter().getFee(wallet.getWalletId(), MyWallet.ELA, "", "8USqenwzA5bSAvj1mG4SGTABykE9n5RzJQ", "0", type, this);


    }


    private void onJustRegistered(CrStatusBean.InfoBean bean, CRListBean.DataBean.ResultBean.CrcandidatesinfoBean curentNode) {

        name = bean.getNickName();
        url = bean.getURL();
        code = bean.getLocation();
        tvTitleRight.setVisibility(View.VISIBLE);
        tvName.setText(bean.getNickName());
        tvAddress.setText(AppUtlis.getLoc(getContext(), bean.getLocation() + ""));
        String url = bean.getURL();
        new SuperNodeListPresenter().getCRUrlJson(url, this, new NodeDotJsonViewData() {
            @Override
            public void onGetNodeDotJsonData(NodeInfoBean t, String url) {
                //获取icon

                try {
                    String imgUrl = t.getOrg().getBranding().getLogo_256();
                    GlideApp.with(CRManageFragment.this).load(imgUrl)
                            .error(R.mipmap.found_vote_initial_circle).circleCrop().into(ivIcon);
                    GlideApp.with(CRManageFragment.this).load(imgUrl)
                            .error(R.mipmap.found_vote_initial_circle).circleCrop().into(ivIcon1);
                } catch (Exception e) {
                }
                try {
                    //获取节点简介
                    NodeInfoBean.OrgBean.CandidateInfoBean infoBean = t.getOrg().getCandidate_info();

                    String info = new SPUtil(CRManageFragment.this.getContext()).getLanguage() == 0 ? infoBean.getZh() : infoBean.getEn();
                    if (!TextUtils.isEmpty(info)) {
                        llTab.setVisibility(View.VISIBLE);
                        tvIntroDetail.setText(info);
                    }

                } catch (Exception e) {
                }
            }
        });
        tvUrl.setText(url);
        if (!TextUtils.isEmpty(DID))
            tvDid.setText("did:ela:" +DID);
        if (curentNode != null) {
            tvNum.setText(curentNode.getVotes() + getString(R.string.ticket));
            tv_zb.setText(curentNode.getVoterate() + "%");
        }
    }


    String available;


    @Override
    public void onGetData(String methodName, BaseEntity baseEntity, Object o) {
        Intent intent;
        switch (methodName) {
            case "createRetrieveCRDepositTransaction":
                intent = new Intent(getActivity(), TransferActivity.class);
                intent.putExtra("wallet", wallet);
                intent.putExtra("type", Constant.WITHDRAWCR);
                intent.putExtra("amount", available);
                intent.putExtra("chainId", MyWallet.ELA);
                intent.putExtra("transType", 36);
                intent.putExtra("attributes", ((CommmonStringEntity) baseEntity).getData());
                startActivity(intent);
                break;
            case "getFee":
                String type = (String) o;
                intent = new Intent(getActivity(), VoteTransferActivity.class);
                intent.putExtra("wallet", wallet);
                intent.putExtra("type", type);
                intent.putExtra("chainId", MyWallet.ELA);
                intent.putExtra("CID", CID);

                intent.putExtra("fee", ((CommmonLongEntity) baseEntity).getData());
                if (Constant.UNREGISTERCR.equals(type)) {
                    //注销按钮
                    intent.putExtra("transType", 34);

                } else if (Constant.CRUPDATE.equals(type)) {
                    //绑定did
                    intent.putExtra("name", name);
                    intent.putExtra("CID", CID);
                    intent.putExtra("url", url);
                    intent.putExtra("code", code);
                    intent.putExtra("transType", 35);
                    intent.putExtra("ownerPublicKey", ownerPublicKey);
                }
                startActivity(intent);

                break;


            case "getCRDepositcoin":
                CRDePositcoinBean getdePositcoinBean = (CRDePositcoinBean) baseEntity;
                available = getdePositcoinBean.getData().getResult().getAvailable();
                //注销可提取
                sbtq.setVisibility(View.VISIBLE);
                break;
            case "DIDResolveWithTip":
                DIDDocument didDocument = (DIDDocument) ((CommmonObjEntity) baseEntity).getData();
                if (didDocument != null) {
                    //绑定 did
                    new CRSignUpPresenter().getFee(wallet.getWalletId(), MyWallet.ELA, "", "8USqenwzA5bSAvj1mG4SGTABykE9n5RzJQ", "0", Constant.CRUPDATE, this);
                }
                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(BusEvent result) {
        int integer = result.getCode();
        if (integer == RxEnum.TRANSFERSUCESS.ordinal()) {
            if (result.getName().equals("35")) {
                //同步中心化服务器
            } else {
                new DialogUtil().showTransferSucess(getBaseActivity(), new WarmPromptListener() {
                    @Override
                    public void affireBtnClick(View view) {
                        popBackFragment();
                    }
                });
            }

        }
    }


}
