package com.ecommerce.e_commerce.security.auth.config;

public final class EndpointGroups {
    private EndpointGroups() {
    } // prevent instantiation

    private static final String API = "/api/v1";
    private static final String API_PRODUCT = API + "/product/**";
    private static final String API_PRODUCTS_REVIEWS = API + "/products/*/reviews/**";
    private static final String API_CATEGORY = API + "/category/**";
    private static final String API_BRAND = API + "/brand/**";
    private static final String API_ORDER = API + "/order/**";
    private static final String API_ORDER_PAYMENT_COD_COMPLETE = API + "/order/*/payment/cod/complete";
    private static final String API_ORDER_PAYPAL_SUCCESS = API + "/order/*/payment/paypal/success";
    private static final String API_ORDER_PAYPAL_CANCEL = API + "/order/*/payment/paypal/cancel";
    private static final String API_CART = API + "/cart/**";
    private static final String API_WISHLIST = API + "/wishlist/**";

    // -------------------- PUBLIC --------------------
    public static final String[] PUBLIC_ENDPOINTS = {
            "/auth/**",
            "/uploads/**",
            "/api-docs/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            API_ORDER_PAYPAL_SUCCESS,
            API_ORDER_PAYPAL_CANCEL
    };

    public static final String[] PUBLIC_GET_ENDPOINTS = {
            API_PRODUCT,
            API_CATEGORY,
            API_BRAND,
            API_PRODUCTS_REVIEWS
    };

    // -------------------- SELLER --------------------
    public static final String[] SELLER_POST = {API_PRODUCT};
    public static final String[] SELLER_PATCH = {API_PRODUCT};
    public static final String[] SELLER_DELETE = {API_PRODUCT};

    // -------------------- CUSTOMER --------------------
    public static final String[] CUSTOMER_ANY = {API_CART, API_WISHLIST};
    public static final String[] CUSTOMER_POST = {API_ORDER, API_PRODUCTS_REVIEWS};
    public static final String[] CUSTOMER_GET = {API_ORDER};
    public static final String[] CUSTOMER_PATCH = {API_PRODUCTS_REVIEWS};
    public static final String[] CUSTOMER_DELETE = {API_PRODUCTS_REVIEWS};

    // -------------------- ADMIN --------------------
    public static final String[] ADMIN_POST = {API_CATEGORY, API_BRAND};
    public static final String[] ADMIN_PATCH = {API_CATEGORY, API_BRAND, API_ORDER_PAYMENT_COD_COMPLETE};
    public static final String[] ADMIN_DELETE = {API_CATEGORY, API_BRAND};
}