package com.cheche365.cheche.wechat.message.json;

import java.util.List;

/**
 * Created by liqiang on 3/27/15.
 */
public class Menu {
    private List<Button> button;

    public List<Button> getButton() {
        return button;
    }

    public void setButton(List<Button> button) {
        this.button = button;
    }

    public static class Button {
        private String name;
        private List<SubButton> sub_button;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<SubButton> getSub_button() {
            return sub_button;
        }

        public void setSub_button(List<SubButton> sub_button) {
            this.sub_button = sub_button;
        }
    }

    public static class SubButton {
        private String type;
        private String name;
        private String key;
        private String url;
        private List<SubButton> sub_button;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List<SubButton> getSub_button() {
            return sub_button;
        }

        public void setSub_button(List<SubButton> sub_button) {
            this.sub_button = sub_button;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
