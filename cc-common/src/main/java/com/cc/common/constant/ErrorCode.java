package com.cc.common.constant;

/**
 * 统一错误码定义
 */
public interface ErrorCode {

    // 通用错误码
    int SUCCESS = 200;
    int BAD_REQUEST = 400;
    int UNAUTHORIZED = 401;
    int FORBIDDEN = 403;
    int NOT_FOUND = 404;
    int INTERNAL_ERROR = 500;

    // 用户服务错误码 1001-1999
    int USER_NOT_FOUND = 1001;
    int USER_ALREADY_EXISTS = 1002;
    int INVALID_PASSWORD = 1003;
    int INVALID_TOKEN = 1004;
    int TOKEN_EXPIRED = 1005;
    int ADDRESS_NOT_FOUND = 1006;

    // 商品服务错误码 2001-2999
    int PRODUCT_NOT_FOUND = 2001;
    int INSUFFICIENT_STOCK = 2002;

    // 订单服务错误码 3001-3999
    int ORDER_NOT_FOUND = 3001;
    int ORDER_ALREADY_PAID = 3002;
    int INVALID_ORDER_STATUS = 3003;

    // 支付服务错误码 4001-4999
    int PAYMENT_NOT_FOUND = 4001;
    int PAYMENT_FAILED = 4002;
}