package com.xc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xc.common.ServerResponse;
import com.xc.dao.SiteArticleMapper;
import com.xc.pojo.SiteArticle;
import com.xc.pojo.SiteNews;
import com.xc.service.ISiteArticleService;

import java.util.Date;
import java.util.List;

import com.xc.utils.DateTimeUtil;
import com.xc.utils.HttpRequest;
import com.xc.utils.PropertiesUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("iSiteArticleService")
public class SiteArticleServiceImpl
        implements ISiteArticleService {

    private static final Logger log = LoggerFactory.getLogger(SiteArticleServiceImpl.class);

    @Autowired
    SiteArticleMapper siteArticleMapper;

    public ServerResponse<PageInfo> listByAdmin(String artTitle, String artType, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SiteArticle> siteArticles = this.siteArticleMapper.listByAdmin(artTitle, artType);
        PageInfo pageInfo = new PageInfo(siteArticles);
        return ServerResponse.createBySuccess(pageInfo);
    }


    public ServerResponse add(SiteArticle siteArticle) {
        if (StringUtils.isBlank(siteArticle.getArtTitle()) ||
                StringUtils.isBlank(siteArticle.getArtType()) ||
                StringUtils.isBlank(siteArticle.getArtCnt()) || siteArticle
                .getIsShow() == null) {
            return ServerResponse.createByErrorMsg("标题正文类型必填");
        }

        siteArticle.setAddTime(new Date());

        int insertCount = this.siteArticleMapper.insert(siteArticle);
        if (insertCount > 0) {
            return ServerResponse.createBySuccessMsg("添加成功");
        }
        return ServerResponse.createByErrorMsg("添加失败");
    }


    public ServerResponse update(SiteArticle siteArticle) {
        if (siteArticle.getId() == null) {
            return ServerResponse.createByErrorMsg("修改id必传");
        }
        int updateCount = this.siteArticleMapper.updateByPrimaryKeySelective(siteArticle);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMsg("修改成功");
        }
        return ServerResponse.createByErrorMsg("修改失败");
    }


    public ServerResponse detail(Integer artId) {
        return ServerResponse.createBySuccess(this.siteArticleMapper.selectByPrimaryKey(artId));
    }


    public ServerResponse list(String artTitle, String artType, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SiteArticle> siteArticles = this.siteArticleMapper.list(artTitle, artType);
        PageInfo pageInfo = new PageInfo(siteArticles);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /*top最新公告*/
    @Override
    public ServerResponse getTopArtList(int pageSize){
        List<SiteNews> listData = this.siteArticleMapper.getTopArtList(pageSize);
        PageInfo pageInfo = new PageInfo();
        pageInfo.setList(listData);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /*公告-抓取*/
    @Override
    public int grabArticle() {
        int ret = 0;

        return ret;
    }

}
