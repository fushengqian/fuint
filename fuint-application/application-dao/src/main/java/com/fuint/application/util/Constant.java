package com.fuint.application.util;

/**
 * Created by Evan on 2017/2/21.
 */
public interface Constant {
    /**
     * 关键字
     */
    public static final String KEYWORD = "keyword";
    /**
     * 关键字中文
     * */
    public static final String KEYWORD_CN = "keyword.cn";
    /**
     * 关键字英文
     */
    public static final String KEYWORD_EN = "keyword.en";
    /**
     * 关键字拼音
     */
    public static final String KEYWORD_PINYIN = "keyword.pinyin";
    /**
     * 渠道
     */
    public static final String SHOPID = "shopId";
    /**
     * index前缀
     * */
    public static final String INDEX_PREFIX = "shop";
    /**
     * 平台ID
     */
    public static final String PLATFORMID = "platformId";
    /**
     * 平台ID
     */
    public static final String PLATFORMIDS = "platformIds";
    /**
     * 渠道IDS
     */
    public static final String CHANNELIDS = "channelIds";
    /**
     * 成功返回码
     */
    public static final String SUCCESS_CODE = "000000";
    /**
     * 成功返回码
     */
    public static final String FAILURE_CODE = "999999";
    /**
     * 智能联想列表
     */
    public static final String ASSOCIATE_LIST = "associateList";

    /**
     * 一级分类
     */
    public static final String CLASS1ID = "class1Id";
    public static final String CLASS1NAME = "class1Name";
    public static final String CLASS1NAME_PINYIN = "class1Name.pinyin";

    /**
     * 二级分类
     */
    public static final String CLASS2ID = "class2Id";
    public static final String CLASS2NAME = "class2Name";
    public static final String CLASS2NAME_PINYIN = "class2Name.pinyin";

    /**
     * 三级分类
     */
    public static final String CLASS3ID = "class3Id";
    public static final String CLASS3NAME = "class3Name";
    public static final String CLASS3NAME_PINYIN = "class3Name.pinyin";

    /**
     * 品牌
     */
    public static final String BRANDID = "brandId";
    public static final String BRANDENNAME = "brandEnName";
    public static final String BRANDNAME_EN = "brandEnName.en";
    public static final String BRANDCNNAME = "brandCnName";
    public static final String BRANDNAME_PINYIN = "brandCnName.pinyin";

    /**
     * 1级分类列表
     */
    public static final String CLASS1LIST = "class1List";
    /**
     * 2级分类列表
     */
    public static final String CLASS2LIST = "class2List";
    /**
     * 三级分类列表
     */
    public static final String CLASS3LIST = "class3List";
    /**
     * 品牌列表
     */
    public static final String BRANDLIST = "brandList";
    /**
     * 动态属性列表
     */
    public static final String PROPERTYLIST = "propertyList";
    /**
     * 动态属性名
     */
    public static final String PROPERTYNAME = "propertyName";
    /**
     * 动态属性值
     */
    public static final String PROPERTYVALUE = "propertyValue";

    /**
     * 推荐关键词列表
     */
    public static final String DATALIST = "dataList";
    /**
     * 页码
     */
    public static final String PAGENO = "pageNo";

    /**
     * 显示数量
     */
    public static final String PAGESIZE = "pageSize";
    /**
     * 总行数
     */
    public static final String TOTALROWS = "totalRows";
    /**
     * 总页数
     */
    public static final String TOTALPAGES = "totalPages";
    /**
     * 是否首页
     */

    public static final String FIRSTPAGE = "firstPage";
    /**
     * 是否尾页
     */
    public static final String LASTPAGE = "lastPage";
    /**
     * 最低价
     */
    public static final String MINPRICE = "minPrice";
    /**
     * 最高价
     */
    public static final String MAXPRICE = "maxPrice";
    /**
     * 是否有货 0：无货 1：有货
     */
    public static final String AVAILABLE = "available";
    /**
     * 序类型
     * 0：权重降序(默认)
     * 1：销量升序 -1：销量降序
     * 2：上架时间升序 -2：上架时间降序
     * 3：价格升序 -3：价格降序
     */
    public static final String SORTTYPE = "sortType";

    /**
     * 商品列表
     */
    public static final String GOODS_LIST = "goodsList";
    public static final String ZERO = "0";
    public static final String ONE = "1";
    public static final String TWO = "2";
    public static final String THREE = "3";

    /**
     * 商品条码复数
     */
    public static final String BARCODES = "barCodes";
    /**
     * 商品条码单数
     */
    public static final String BARCODE = "barCode";

    /**
     * 推荐关键词
     */
    public static final String RECOMMENDED_KEYWORDS = "recommendedKeywords";

    /**
     * 分类名称.
     */
    public static final String CLASS_NAME = "className";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String COUNT = "count";
    public static final String FLAG = "flag";
    public static final String NUM = "num";
    public static final String VALUE = "value";

    /**
     * 热词
     */
    public static final String HOT_WORD = "hotWord";

    /**
     * 热词列表
     */
    public static final String HOT_WORD_LIST = "hotWordList";

    public static final String KEYWORD_LIST = "keywordList";

    /**
     * 商品信息
     */
    public static final String GOODS = "goods";


    /**
     * 商品名称
     */
    public static final String GOODSNAME = "goodsName";
    public static final String GOODSNAME_PINYIN = "goodsName.pinyin";

    /**
     * UTF-8
     */
    public static final String UTF8 = "utf-8";
    /**
     * 销售价格
     */
    public static final String SALEPRICE = "salePrice";
    /**
     * 最大价格
     * */
    public static final String MAXUNDERLINEPRICE = "max_salePrice";
    /**
     * 最小价格
     */
    public static final String MINUNDERLINEPRICE = "min_salePrice";
    /**
     * 库存
     */
    public static final String STOCK = "stock";
    /**
     * 库存状态
     * */
    public static final String STOCKFLAG = "stockFlag";
    /**
     * 销售数量
     */
    public static final String SALENUM = "saleNum";
    /**
     * 上架时间
     */

    public static final String SHELVETIME = "shelveTime";

    //public static final String SHOPLIST = "shopList";
    /**
     * 点
     */
    public static final String POINT = ".";
    /**
     * 价格列表
     */
    public static final String PRICELIST = "priceList";
    /**
     * 逗号
     */
    public static final String COMMA = ",";
    /**
     * 活动列表
     */
    public static final String ACTIVITYLIST = "activityList";
    /**
     * 活动ID
     */
    public static final String ACTIVITYID = "activityId";
    /**
     * 活动名称
     */
    public static final String ACTIVITYNAME = "activityName";
    /**
     * 活动类型
     */
    public static final String ACTIVITYTYPE = "activityType";
    /**
     * 活动标识
     */
    public static final String ACTIVITYFLAG = "activityFlag";
    /**
     * 活动数量
     */
    public static final String ACTIVITYCOUNT = "activityCount";
    /**
     * 活动优先级
     */
    public static final String ACTIVITYPRIORITY = "activityPriority";
    /**
     * 活动促销规则
     */
    public static final String PROMOTIONRULES = "promotionRules";
    /**
     * 是否显示活动标记
     */
    public static final String PROMOTIONTAG = "promotionTag";
    /**
     * 活动开始时间
     */
    public static final String STARTTIME = "startTime";
    /**
     * 结束时间
     */
    public static final String ENDTIME = "endTime";
    /**
     * 操作人
     */
    public static final String OPERATOR = "operator";
    /**
     * 动态属性排序字段
     */
    public static final String PROPERTYSORTORDER = "propertyList.sortOrder";
    /**
     * 排序
     */
    public static final String SORTORDER = "sortOrder";
    /**
     * 空格
     */
    public static final String SPACE = " ";
    /**
     * 库存
     */
    public static final String SHOPSTOCK = "stock";
    /**
     * 上架状态
     */
    public static final String SHELVESTATUS = "shelveStatus";


    /**
     * 更新时间
     */
    public static final String UPDATE_TIME = "updateTime";

    /**
     * 范围规则项ID
     */
    public static final String ITEM_IDS = "itemIds";
}
