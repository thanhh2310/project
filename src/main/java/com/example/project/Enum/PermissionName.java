package com.example.project.Enum;

import lombok.Getter;

@Getter
public enum PermissionName {
    // --- 1. AUTH & USER MANAGEMENT (Quản lý người dùng & Phân quyền) ---
    USER_VIEW("Xem danh sách người dùng"),
    USER_CREATE("Tạo người dùng mới (Admin tạo staff)"),
    USER_UPDATE("Cập nhật thông tin người dùng"),
    USER_DELETE("Xóa/Khóa người dùng"),

    ROLE_VIEW("Xem danh sách vai trò"),
    ROLE_CREATE("Tạo vai trò mới"),
    ROLE_UPDATE("Cập nhật quyền hạn cho vai trò"),
    ROLE_DELETE("Xóa vai trò"),

    // --- 2. PRODUCT CATALOG (Quản lý sản phẩm) ---
    // Bao gồm cả Product, ProductSku, ProductSpec
    PRODUCT_VIEW("Xem chi tiết sản phẩm (Admin view)"),
    PRODUCT_CREATE("Tạo sản phẩm mới"),
    PRODUCT_UPDATE("Cập nhật sản phẩm (Giá, Tồn kho, SKU)"),
    PRODUCT_DELETE("Xóa/Ẩn sản phẩm"),

    // --- 3. CATEGORY & BRAND & ATTRIBUTE (Danh mục & Thương hiệu) ---
    CATEGORY_MANAGE("Quản lý danh mục (Tạo/Sửa/Xóa)"),
    BRAND_MANAGE("Quản lý thương hiệu (Tạo/Sửa/Xóa)"),
    ATTRIBUTE_MANAGE("Quản lý thuộc tính động (Màu, Size, Chip...)"),

    // --- 4. ORDER MANAGEMENT (Quản lý đơn hàng) ---
    ORDER_VIEW("Xem danh sách đơn hàng"),
    ORDER_UPDATE_STATUS("Cập nhật trạng thái đơn (Duyệt, Giao hàng, Hoàn thành)"),
    ORDER_CANCEL("Hủy đơn hàng (Quyền đặc biệt cho Manager)"),
    ORDER_DELETE("Xóa lịch sử đơn hàng (Rất hạn chế dùng)"),

    // --- 5. MARKETING (Khuyến mãi) ---
    COUPON_VIEW("Xem danh sách mã giảm giá"),
    COUPON_CREATE("Tạo mã giảm giá mới"),
    COUPON_UPDATE("Sửa mã giảm giá"),
    COUPON_DELETE("Xóa mã giảm giá"),

    // --- 6. CONTENT & REVIEWS (Đánh giá & Bình luận) ---
    REVIEW_VIEW("Xem các đánh giá"),
    REVIEW_APPROVE("Duyệt đánh giá (Cho phép hiện lên web)"),
    REVIEW_DELETE("Xóa đánh giá vi phạm (Spam/Tục tĩu)"),
    REVIEW_REPLY("Trả lời đánh giá của khách"),

    // --- 7. SETTINGS & SYSTEM (Cấu hình hệ thống) ---
    SHIPPING_MANAGE("Quản lý phương thức vận chuyển & Phí ship"),
    PAYMENT_MANAGE("Quản lý phương thức thanh toán"),
    SYSTEM_SETTINGS("Cấu hình hệ thống chung"),

    // --- 8. REPORT & ANALYTICS (Báo cáo thống kê) ---
    DASHBOARD_VIEW("Xem Dashboard tổng quan"),
    REPORT_REVENUE("Xem báo cáo doanh thu"),
    REPORT_PRODUCT("Xem báo cáo sản phẩm bán chạy"),
    REPORT_USER("Xem báo cáo tăng trưởng người dùng");

    private final String description;

    PermissionName(String description) {
        this.description = description;
    }
}
