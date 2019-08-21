package com.accessibility.service.page

/**
 * Description:搜索方式
 * 1:根据关键字搜索
 * 2:根据店铺名字搜索
 * 3:根据浏览器搜索
 * Created by Quinin on 2019-08-21.
 **/
class SearchType {
    companion object {
        const val KEYWORD = 0
        const val MALLNAME = 1
        const val BROWSER = 2
    }
}