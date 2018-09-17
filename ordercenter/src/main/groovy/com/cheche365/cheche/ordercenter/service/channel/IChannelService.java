package com.cheche365.cheche.ordercenter.service.channel;

import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.ordercenter.web.model.channel.ChannelViewData;

/**
 * Created by wangfei on 2015/5/22.
 */
public interface IChannelService {
    /**
     * add new channel
     * @param viewData
     * @return
     */
    boolean addChannel(ChannelViewData viewData);

    /**
     * list channel by page
     * @param currentPage
     * @param pageSize
     * @param keyword
     * @return
     */
    PageViewModel<ChannelViewData> listChannels(Integer currentPage, Integer pageSize, String keyword);

    /**
     * find channel by id
     * @param id
     * @return
     */
    ChannelViewData findById(Long id);

    /**
     * update channel
     * @param viewData
     * @return
     */
    boolean updateChannel(ChannelViewData viewData);

    /**
     * delete channel by id
     * @param id
     * @return
     */
    boolean deleteChannel(Long id);

}
