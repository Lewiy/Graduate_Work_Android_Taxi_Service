package com.lewgmail.romanenko.taxiservice.presenter;

import com.lewgmail.romanenko.taxiservice.model.DTO.DataGoogleMapDTO;
import com.lewgmail.romanenko.taxiservice.model.dataManager.ManagerOrderApiCust;
import com.lewgmail.romanenko.taxiservice.model.pojo.AddOrderN;
import com.lewgmail.romanenko.taxiservice.model.pojo.CalculatePrice;
import com.lewgmail.romanenko.taxiservice.model.pojo.OrderUpdate;
import com.lewgmail.romanenko.taxiservice.model.pojo.Price;
import com.lewgmail.romanenko.taxiservice.view.activity.AddOrder;
import com.lewgmail.romanenko.taxiservice.view.activity.AddOrderUpdate;
import com.lewgmail.romanenko.taxiservice.view.activity.EditOrderInterface;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Lev on 20.11.2016.
 */

public class CustomerPresenter {

    private int codeMsg;
    private String responseMsg;
    private EditOrderInterface view;
    private AddOrder viewAddOrder;
    private AddOrderUpdate viewAddOrderUpdate;

    private ManagerOrderApiCust managerOrderApiCust = new ManagerOrderApiCust(this);
    private DataGoogleMapDTO dataGoogleMapDTOPres;

    public CustomerPresenter(EditOrderInterface view) {
        this.view = view;
    }

    public CustomerPresenter() {

    }

    public CustomerPresenter(AddOrder viewAddOrder) {
        this.viewAddOrder = viewAddOrder;
    }

    public CustomerPresenter(AddOrderUpdate viewAddOrderUpdate) {
        this.viewAddOrderUpdate = viewAddOrderUpdate;
    }

    public EditOrderInterface getView() {
        return view;
    }

    public void addOrder() {

        Observable<Response<ResponseBody>> observer = managerOrderApiCust.addOrder(createOrder());
        observer.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException)
                            viewAddOrder.responseAddorder(e.getMessage());
                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {
                        viewAddOrder.responseAddorder(new String(Integer.toString(responseBodyResponse.code())));
                    }
                });

    }

    public void calculatePrice() {

        Observable<Price> observer = managerOrderApiCust.calculatePrice(createCalculatePriceObject());

        observer.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Price>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (viewAddOrder != null)
                        viewAddOrder.frag2responseError(e.toString());
                        else viewAddOrderUpdate.responseError(e.toString());
                    }

                    @Override
                    public void onNext(Price price) {
                        if (viewAddOrderUpdate != null) {
                            viewAddOrder.frag2responseCalculatedPrice(price.getPrice().toString());
                            viewAddOrder.frag2responseDistance(price.getDistance().toString());
                            viewAddOrder.frag2responseDuration(price.getDuration().toString());
                        } else {
                            viewAddOrderUpdate.responsePrice(price.getPrice());
                            viewAddOrderUpdate.responseDistance(price.getDistance());
                            viewAddOrderUpdate.responseDuration(price.getDuration().toString());
                        }
                    }
                });
    }


    public void deleteOrder(long orderId) {
        Observable<Response<ResponseBody>> observer = managerOrderApiCust.deleteOrder(orderId);
        observer.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException)
                            viewAddOrderUpdate.responseError(e.toString());
                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {
                        viewAddOrderUpdate.responseAddorder(new String(Integer.toString(responseBodyResponse.code())));
                    }
                });
    }

    public void updateOrder(long orderId) {
        Observable<Response<ResponseBody>> observer = managerOrderApiCust.updateOrder(createOrderUpdate(), orderId);
        observer.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException)
                            viewAddOrderUpdate.responseAddorder(e.getMessage());
                    }

                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {
                        viewAddOrderUpdate.responseAddorder(new String(Integer.toString(responseBodyResponse.code())));
                    }
                });
    }

    public void onFinishRequest(int codeMsg, String responseMsg) {

        this.codeMsg = codeMsg;
        this.responseMsg = responseMsg;

    }

    public int getCodeMsg() {
        return codeMsg;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setObserverPriseResponse(Observable<Price> observer) {

        observer.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Price>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException)
                            view.showError(e.getMessage().toString());
                        else
                            view.showError(e.getMessage().toString());
                    }

                    @Override
                    public void onNext(Price price) {
                        view.setPrice(price.getPrice());
                    }
                });
    }

    private AddOrderN createOrder() {
        AddOrderN addOrderN = new AddOrderN();
        addOrderN.setComment(viewAddOrder.getComment());
        addOrderN.setStartTime(viewAddOrder.getTime());
        addOrderN.setAdditionalRequirementN(viewAddOrder.getAdditionalRequirementNs());
        addOrderN.setRoutePoint(viewAddOrder.getRoute());
        return addOrderN;
    }

    private OrderUpdate createOrderUpdate() {
        OrderUpdate orderUpdate = new OrderUpdate();
        orderUpdate.setQuickRequest(false);
        orderUpdate.setComment(viewAddOrderUpdate.getComment());
        orderUpdate.setStartTime(viewAddOrderUpdate.getTime());
        orderUpdate.setAdditionalRequirementN(viewAddOrderUpdate.getAdditionalRequirementNs());
        orderUpdate.setRoutePoint(viewAddOrderUpdate.getRoute());
        return orderUpdate;
    }

    private CalculatePrice createCalculatePriceObject() {

        CalculatePrice price = new CalculatePrice();
        price.setRoutePoint(viewAddOrder.getRoute());
        price.setAdditionalRequirementN(viewAddOrder.getAdditionalRequirementNs());

        return price;
    }

}
