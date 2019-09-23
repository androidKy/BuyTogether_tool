package com.accessibility.service.page

/**
 *   评论任务的类型：
 *    0：未签收
 *    1: 评论成功
 *    2：评论失败
 */
class CommentStatus {

    companion object{
        const val NOT_SIGNED:Int = 0
        const val COMMENT_SUCCESS:Int = 1
        const val COMMENT_FAILED:Int = 2

    }
}