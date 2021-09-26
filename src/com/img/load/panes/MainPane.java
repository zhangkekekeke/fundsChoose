package com.img.load.panes;

import com.img.load.bean.NewShareFund;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import load.ChooseData;
import load.ChooseType;
import load.ChooseTypeContants;
import load.ChooseTypeContantsKt;
import load.module.FundCallback;
import load.module.FundModule;
import load.util.TextUtilsKt;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainPane implements EventHandler<MouseEvent> {
    Pane root;
    MainController controller;
    FundModule fundModule;

    public MainPane() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(XmlLoader.class.getResource("mainpane.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller = loader.getController();
        init();
    }

    private void init() {
        controller.btn_TotalPage.setOnMouseClicked(this);
        fundModule = new FundModule();
        controller.container.heightProperty().addListener((observable, oldValue, newValue)
                -> {
            controller.scrollPane.setPrefHeight((newValue.doubleValue() - controller.scrollPane.getLayoutY() - 16));
        });
        controller.container.widthProperty().addListener((observable, oldValue, newValue)
                -> controller.scrollPane.setPrefWidth(newValue.doubleValue() * 0.92d));

        controller.cb_keyWord.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        controller.cb_keyWord.setItems(new ObservableListWrapper<>(Arrays.asList(ChooseTypeContantsKt.getKeyWords())));
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getSource() == controller.btn_TotalPage) {
            dowork();
        }
    }

    public void dowork() {
        String time = controller.et_earningTime.getText();
        ChooseData chooseData = ChooseTypeContants.INSTANCE.getChooseData();
        if (!TextUtilsKt.isEmpty(time)) {
            //设置 收益时间段
            chooseData.setEarningTime(Integer.parseInt(time));
        }
        ObservableList<String> selectWords = controller.cb_keyWord.getSelectionModel().getSelectedItems();
        if (selectWords != null && selectWords.size() > 0) {
            ChooseTypeContants.INSTANCE.getChooseData().setFuzzyName(selectWords.toArray(new String[]{}));
        }

        new FundModule().filterNewShareFunds(ChooseTypeContants.INSTANCE.getChooseData(), new FundCallback() {
            @Override
            public void onFundsFilter(List<NewShareFund> filterFunds) {
                controller.book_container.getChildren().clear();
                for (NewShareFund fund : filterFunds) {
                    float unIPORatio = fund.getSharesUnIPO().getSUMPLACE() * 100 / fund.getTotalEndNav();
                    Calendar setupDate = Calendar.getInstance();
                    setupDate.setTime(fund.getSetupDate());
                    int year = setupDate.get(Calendar.YEAR);

                    int nameSize=fund.getSHORTNAME().length();

                    String msg = String.format("%s；%s ", fund.getFCODE(), fund.getSHORTNAME().substring(0, Math.min(nameSize, 8)))
                            + String.format(", 成立：%d ", year)
                            + String.format(", 规模：%.1f亿 ", fund.getTotalEndNav()/100000000)
                            + String.format(", 未上市：%.2f%% ", unIPORatio / fund.getShareRatio())
                            + String.format(", %d个月打新：%.2f%% ", fund.getEarningTime(), fund.getEarningRateByTime())
                            + String.format(", 股占:%.2f%% ", fund.getShareRatio() * 100)
//                            + String.format(", 未上市/总:%.2f%% ", unIPORatio)
//                            + "，一标差:" + fund.getSd().getOneYear() + "%"
//                            + "，一信息:" + fund.getInfoR().getOneYear()
                            + "，一年夏普:" + fund.getSr().getOneYear()
                            + "，二年夏普:" + fund.getSr().getTwoYear()
                            + "，三年夏普:" + ((fund.getSr().getThreeYear() ==-1)?"无":fund.getSr().getThreeYear())
                            ;
                    TextField label = new TextField(msg);
                    label.setFont(new Font(14));

                    ChooseData chooseData = ChooseTypeContants.INSTANCE.getChooseData();
                    if (chooseData.getChooseType() == ChooseType.FUND_500) {
                        //标记颜色
                        boolean srOK = false, infoOK = false;
                        if (fund.getInfoR().getOneYear() != null && fund.getInfoR().getOneYear() > 0.3f) {//信息比率
                            infoOK = true;
//                            label.setStyle("-fx-text-inner-color: #D2691E;");
                        }
                        if (infoOK && fund.getSr().getOneYear() > 1.7f) {
                            srOK = true;
//                            label.setStyle("-fx-text-inner-color: red;");
                        }
                        if (srOK && fund.getTravErrorR() != null && fund.getAverErrorR() != null
                                && fund.getTravErrorR() <= fund.getAverErrorR()) {//跟踪误差
                            label.setStyle("-fx-text-inner-color: blue;");
                        }
                    } else if (chooseData.getChooseType() == ChooseType.NEW_SHARE) {
                        //标记颜色
//                        if (fund.getSd().getOneYear() < 3 || fund.getSd().getTwoYear() < 3 || fund.getSd().getThreeYear() < 3) {
//                            label.setStyle("-fx-text-inner-color: red;");
//                        }
                        if (fund.getSr().getOneYear() > 1.7f || fund.getSr().getTwoYear() > 1.7f || fund.getSr().getThreeYear() > 1.7f) {
                            label.setStyle("-fx-text-inner-color: red;");
                        }
                    }


                    label.prefWidthProperty().bind(controller.scrollPane.widthProperty());
                    controller.book_container.getChildren().add(label);
                }
                controller.text_filterFund.setText(filterFunds.size() + "");
            }

            @Override
            public void onFundsCount(int count) {
                controller.text_totalPage.setText(count + "");
            }
        });
    }

    public Pane getRoot() {
        return root;
    }
}
