package com.cheche365.cheche.rest.model;

import com.cheche365.cheche.core.model.BusinessActivity;

/**
 * Created by mahong on 2015/9/14.
 * <p>
 * header: {menu: true},
 * home: true,
 * base: {footer: false},
 * mine: true,
 * confirm_order: {gift: true},
 * success: {btn: {home: true, m_orders: true}, app: false}
 */
public class ChannelConfigInfo {


    Header header;
    Object home;
    Base base;
    Boolean mine;
    ConfirmOrder confirm_order;
    Success success;

    public ChannelConfigInfo() {
    }

    public ChannelConfigInfo(Header header, Object home, Base base, Boolean mine, ConfirmOrder confirm_order, Success success) {
        this.header = header;
        this.home = home;
        this.base = base;
        this.mine = mine;
        this.confirm_order = confirm_order;
        this.success = success;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getHome() {
        return home;
    }

    public void setHome(Object home) {
        this.home = home;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public Boolean getMine() {
        return mine;
    }

    public void setMine(Boolean mine) {
        this.mine = mine;
    }

    public ConfirmOrder getConfirm_order() {
        return confirm_order;
    }

    public void setConfirm_order(ConfirmOrder confirm_order) {
        this.confirm_order = confirm_order;
    }

    public Success getSuccess() {
        return success;
    }

    public void setSuccess(Success success) {
        this.success = success;
    }

    public static class Header {
        Boolean menu;

        public Header() {
        }

        public Header(Boolean menu) {
            this.menu = menu;
        }

        public Boolean getMenu() {
            return menu;
        }

        public void setMenu(Boolean menu) {
            this.menu = menu;
        }
    }

    public static class Base {
        Boolean footer;

        public Base() {
        }

        public Base(Boolean footer) {
            this.footer = footer;
        }

        public Boolean getFooter() {
            return footer;
        }

        public void setFooter(Boolean footer) {
            this.footer = footer;
        }
    }

    public static class ConfirmOrder {
        Boolean gift;

        public ConfirmOrder() {
        }

        public ConfirmOrder(Boolean gift) {
            this.gift = gift;
        }

        public Boolean getGift() {
            return gift;
        }

        public void setGift(Boolean gift) {
            this.gift = gift;
        }
    }

    public static class Success {
        Button btn;
        Boolean app;

        public Success() {
        }

        public Success(Button btn, Boolean app) {
            this.btn = btn;
            this.app = app;
        }

        public Button getBtn() {
            return btn;
        }

        public void setBtn(Button btn) {
            this.btn = btn;
        }

        public Boolean getApp() {
            return app;
        }

        public void setApp(Boolean app) {
            this.app = app;
        }

        public static class Button {
            Boolean home;
            Boolean m_orders;

            public Button() {
            }

            public Button(Boolean home, Boolean m_orders) {
                this.home = home;
                this.m_orders = m_orders;
            }

            public Boolean getHome() {
                return home;
            }

            public void setHome(Boolean home) {
                this.home = home;
            }

            public Boolean getM_orders() {
                return m_orders;
            }

            public void setM_orders(Boolean m_orders) {
                this.m_orders = m_orders;
            }
        }
    }

    public static class HomeConfig {
        Header header;
        Boolean topbanner;
        Boolean gift;
        Boolean customer;
        Boolean bottombanner;
        Footer footer;
        Boolean fixedbtn;

        public HomeConfig() {
        }

        public HomeConfig(Header header, Boolean topbanner, Boolean gift, Boolean customer, Boolean bottombanner, Footer footer, Boolean fixedbtn) {
            this.header = header;
            this.topbanner = topbanner;
            this.gift = gift;
            this.customer = customer;
            this.bottombanner = bottombanner;
            this.footer = footer;
            this.fixedbtn = fixedbtn;
        }

        public Header getHeader() {
            return header;
        }

        public void setHeader(Header header) {
            this.header = header;
        }

        public Boolean getTopbanner() {
            return topbanner;
        }

        public void setTopbanner(Boolean topbanner) {
            this.topbanner = topbanner;
        }

        public Boolean getGift() {
            return gift;
        }

        public void setGift(Boolean gift) {
            this.gift = gift;
        }

        public Boolean getCustomer() {
            return customer;
        }

        public void setCustomer(Boolean customer) {
            this.customer = customer;
        }

        public Boolean getBottombanner() {
            return bottombanner;
        }

        public void setBottombanner(Boolean bottombanner) {
            this.bottombanner = bottombanner;
        }

        public Footer getFooter() {
            return footer;
        }

        public void setFooter(Footer footer) {
            this.footer = footer;
        }

        public Boolean getFixedbtn() {
            return fixedbtn;
        }

        public void setFixedbtn(Boolean fixedbtn) {
            this.fixedbtn = fixedbtn;
        }

        public static class Header {
            Boolean left;
            Boolean right;

            public Header() {
            }

            public Header(Boolean left, Boolean right) {
                this.left = left;
                this.right = right;
            }

            public Boolean getLeft() {
                return left;
            }

            public void setLeft(Boolean left) {
                this.left = left;
            }

            public Boolean getRight() {
                return right;
            }

            public void setRight(Boolean right) {
                this.right = right;
            }
        }

        public static class Footer {
            Boolean top;
            Boolean bottom;

            public Footer() {
            }

            public Footer(Boolean top, Boolean bottom) {
                this.top = top;
                this.bottom = bottom;
            }

            public Boolean getTop() {
                return top;
            }

            public void setTop(Boolean top) {
                this.top = top;
            }

            public Boolean getBottom() {
                return bottom;
            }

            public void setBottom(Boolean bottom) {
                this.bottom = bottom;
            }
        }
    }

    public static ChannelConfigInfo assembleConfigInfo(BusinessActivity activity) {
        if (activity == null) {
            return null;
        }

        ChannelConfigInfo configInfo = new ChannelConfigInfo();
            HomeConfig homeConfig = new HomeConfig();
            homeConfig.setHeader(new HomeConfig.Header(activity.isTopBrand(), activity.isMyCenter()));
            homeConfig.setTopbanner(activity.isTopCarousel());
            homeConfig.setGift(activity.isActivityEntry());
            homeConfig.setCustomer(activity.isOurCustomer());
            homeConfig.setBottombanner(activity.isBottomCarousel());
            homeConfig.setFooter(new HomeConfig.Footer(activity.isBottomInfo(), activity.isBottomInfo()));
            homeConfig.setFixedbtn(activity.isBottomDownload());
            configInfo.setHome(homeConfig);
            configInfo.setMine(activity.isMyCenter());
    

        configInfo.setHeader(new Header(activity.isDisplay()));
        configInfo.setBase(new Base(activity.isFooter()));
        configInfo.setConfirm_order(new ConfirmOrder(activity.isEnable()));
        Success.Button btn = new Success.Button();
        btn.setHome(activity.isBtn());
        btn.setM_orders(activity.isBtn());
        configInfo.setSuccess(new Success(btn, activity.isApp()));
        return configInfo;
    }
}
