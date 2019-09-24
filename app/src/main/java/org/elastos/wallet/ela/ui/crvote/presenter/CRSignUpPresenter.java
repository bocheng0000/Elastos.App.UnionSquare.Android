package org.elastos.wallet.ela.ui.crvote.presenter;

import org.elastos.wallet.ela.base.BaseFragment;
import org.elastos.wallet.ela.rxjavahelp.BaseEntity;
import org.elastos.wallet.ela.rxjavahelp.NewPresenterAbstract;
import org.elastos.wallet.ela.rxjavahelp.ObservableListener;
import org.json.JSONException;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class CRSignUpPresenter extends NewPresenterAbstract {
    //为了验证密码
    public void exportWalletWithMnemonic(String walletId, String pwd, BaseFragment baseFragment) {
        Observer observer = createObserver(baseFragment, "exportWalletWithMnemonic");
        Observable observable = createObservable(new ObservableListener() {
            @Override
            public BaseEntity subscribe() {
                return baseFragment.getMyWallet().exportWalletWithMnemonic(walletId, pwd);
            }
        });
        subscriberObservable(observer, observable, baseFragment);
    }

    public void getCROwnerDID(String masterWalletID, String chainID, BaseFragment baseFragment) {
        Observer observer = createObserver(baseFragment, "getCROwnerDID");
        Observable observable = createObservable(new ObservableListener() {
            @Override
            public BaseEntity subscribe() throws JSONException {
                return baseFragment.getMyWallet().getCROwnerDID(masterWalletID, chainID);
            }
        });
        subscriberObservable(observer, observable, baseFragment);
    }

    //验证交易
    public void generateCRInfoPayload(String masterWalletID, String chainID, String publicKey, String nickName, String url, long location, String payPasswd
            , BaseFragment baseFragment) {
        Observer observer = createObserver(baseFragment, "generateCRInfoPayload");
        Observable observable = createObservable(new ObservableListener() {
            @Override
            public BaseEntity subscribe() throws JSONException {
                return baseFragment.getMyWallet().generateCRInfoPayload(masterWalletID, chainID, publicKey, nickName, url, location, payPasswd);
            }
        });
        subscriberObservable(observer, observable, baseFragment);
    }


    //创建交易
    public void createRegisterCRTransaction(String masterWalletID, String chainID, String fromAddress, String payloadJson, String amount, String memo, boolean useVotedUTXO
            , BaseFragment baseFragment) {
        Observer observer = createObserver(baseFragment, "createRegisterCRTransaction");
        Observable observable = createObservable(new ObservableListener() {
            @Override
            public BaseEntity subscribe() {
                return baseFragment.getMyWallet().createRegisterCRTransaction(masterWalletID, chainID, fromAddress, payloadJson, amount, memo, useVotedUTXO);
            }
        });
        subscriberObservable(observer, observable, baseFragment);
    }


    //更新信息
    public void createUpdateCRTransaction(String masterWalletID, String chainID, String fromAddress, String payloadJson, String memo, boolean useVotedUTXO
            , BaseFragment baseFragment) {
        Observer observer = createObserver(baseFragment, "createUpdateCRTransaction");
        Observable observable = createObservable(new ObservableListener() {
            @Override
            public BaseEntity subscribe() {
                return baseFragment.getMyWallet().createUpdateCRTransaction(masterWalletID, chainID, fromAddress, payloadJson, memo, useVotedUTXO);
            }
        });
        subscriberObservable(observer, observable, baseFragment);
    }
}