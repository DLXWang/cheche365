package com.cheche365.cheche.ordercenter.web.model.quote;

import java.util.List;

/**
 * Created by wangfei on 2015/10/15.
 */
public class QuoteViewModel {
    private List<QuotePhoneViewModel> phones;

    public List<QuotePhoneViewModel> getPhones() {
        return phones;
    }

    public void setPhones(List<QuotePhoneViewModel> phones) {
        this.phones = phones;
    }

    private List<QuotePhotoViewModel> photos;

    public List<QuotePhotoViewModel> getPhotos() {
        return photos;
    }

    public void setPhotos(List<QuotePhotoViewModel> photos) {
        this.photos = photos;
    }
}
