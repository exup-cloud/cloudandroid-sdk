package com.yjkj.chainup.app

class AppConstant {

    companion object {

        val SECRET: String = "jiaoyisuo@2017"


        //*/ 手势密码点的状态
        val POINT_STATE_NORMAL: Int = 0 // 正常状态

        val POINT_STATE_SELECTED: Int = 1 // 按下状态

        val POINT_STATE_WRONG: Int = 2 // 错误状态


        /******************手机短信类型：START***********************/

        /**
         * 手机号码注册
         */
        val REGISTER_MOBILE = 1

        /**
         * 绑定手机号码
         */
        val BIND_MOBILE = 2

        /**
         * 修改手机号码
         */
        val CHANGE_MOBILE = 3

        /**
         * 绑定邮箱
         */
        val BIND_EMAIL = 4

        /**
         * 设置资金密码
         */
        val SET_CAPITAL_PWD = 6


        /**
         * 修改资金密码
         */
        val CHANGE_CAPITAL_PWD = 7


        /**
         *修改密码
         */

        val CHANGE_PWD = 9


        /**
         * 添加数字货币地址
         */

        val ADD_WITHDRAW_ADDRESS = 11

        /**
         * 修改&删除数字货币地址
         */
        val CHANGE_WITHDRAW_ADDRESS = 12


        /**
         * 数字货币提现
         */

        val CRYPTO_WITHDRAW = 13

        /**
         * 关闭手机验证
         */
        val CLOSE_MOBILE_VERIFY = 14


        /**
         * 修改邮箱
         */

        val CHANGE_EMAIL = 15

        /**
         * 找回密码
         */
        val FIND_PWD_MOBILE = 24


        /**
         * 手机登录
         */
        val MOBILE_LOGIN = 25

        /**
         * 关闭Google认证
         */

        val CLOASE_GOOGLE_VERIFY = 26

        /**
         * 开启或关闭手势密码
         */
        val GESTURE_PWD = 27


        /**
         * 邮箱注册
         */
        val REGISTER_EMAIL = 1

        /**
         * 找回密码
         */
        val FIND_PWD_EMAIL = 3

        /**
         * 邮箱登录
         */
        val EMAIL_LOGIN = 4

        /***********邮箱短信类型：END************/




        /************APP盘口类型************/
        /**
         * 默认盘口
         */
        const val DEFAULT_TAPE = 0
        /**
         * only 买盘
         */
        const val BUY_TAPE = 1
        /**
         * only 卖盘
         */
        const val SELL_TAPE = 2


    }

}
