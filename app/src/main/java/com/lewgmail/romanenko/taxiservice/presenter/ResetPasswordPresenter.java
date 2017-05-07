package com.lewgmail.romanenko.taxiservice.presenter;

import com.lewgmail.romanenko.taxiservice.model.dataManager.ManagerResetPassword;
import com.lewgmail.romanenko.taxiservice.model.pojo.SendCodeResetPassword;
import com.lewgmail.romanenko.taxiservice.model.pojo.SendEmailResetPassword;
import com.lewgmail.romanenko.taxiservice.view.activity.ResetPassword;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Lev on 07.05.2017.
 */

public class ResetPasswordPresenter {

    private ResetPassword resetPasswordView;
    private String restoreIdRequest;
    private ManagerResetPassword managerResetPassword = new ManagerResetPassword();

    public ResetPasswordPresenter(ResetPassword resetPasswordView) {
        this.resetPasswordView = resetPasswordView;
    }

    public void sendEmailReq() {

        Observable<String> observer = managerResetPassword.sendEmail(createObjectSendEmail());
        observer.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException)
                            resetPasswordView.showError(e.toString());
                    }

                    @Override
                    public void onNext(String responseBodyResponse) {
                        // viewAddOrder.responseAddorder(new String(Integer.toString(responseBodyResponse.code())));
                        restoreIdRequest = responseBodyResponse;
                        showPasswordFields();
                    }
                });

    }

    public void sendNewPasswordReq() {
        Observable<Response<ResponseBody>> observer = managerResetPassword.sendNewPassword(createObjectSendCode(), restoreIdRequest);
        observer.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException)
                            resetPasswordView.showError(e.toString());
                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBody) {
                        resetPasswordView.doneOperation(responseBody.code(), new String(Integer.toString(responseBody.code())));
                    }
                });
    }

    public String readEmail() {
        return resetPasswordView.getEmailValueReset();
    }

    public String readCode() {
        return resetPasswordView.getResetPasswordCode();
    }

    public String readNewPassword() {
        return resetPasswordView.getResetPasswordNewPassword();
    }

    public void showPasswordFields() {
        resetPasswordView.showHideCodeNewPasswordField();
    }

    private SendEmailResetPassword createObjectSendEmail() {
        SendEmailResetPassword sendEmailResetPassword = new SendEmailResetPassword();
        sendEmailResetPassword.setEmail(readEmail());
        return sendEmailResetPassword;
    }

    private SendCodeResetPassword createObjectSendCode() {
        SendCodeResetPassword sendCodeResetPassword = new SendCodeResetPassword();
        sendCodeResetPassword.setCode(readCode());
        sendCodeResetPassword.setPassword(readNewPassword());
        return sendCodeResetPassword;
    }
}