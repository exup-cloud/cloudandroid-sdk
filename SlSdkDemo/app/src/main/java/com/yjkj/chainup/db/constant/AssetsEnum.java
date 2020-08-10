package com.yjkj.chainup.db.constant;

/**
 * @Description: 资产页面 tab类型常量值
 * @Author: wanghao
 * @CreateDate: 2019-10-25 18:57
 * @UpdateUser: wanghao
 * @UpdateDate: 2019-10-25 18:57
 * @UpdateRemark: 更新说明
 */
public enum AssetsEnum {

    COIN_ACCOUNT("币币交易账户", 0),
    FIA_COIN_ACCOUNT("法币交易账户", 1),
    CONTRACT_COIN_ACCOUNT("币币交易账户", 2),
    LEVERAGE_COIN_ACCOUNT("杠杆交易账户", 3);

    private String name;
    private int value;

    private AssetsEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
