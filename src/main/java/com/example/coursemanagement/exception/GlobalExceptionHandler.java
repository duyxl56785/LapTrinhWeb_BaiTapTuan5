package com.example.coursemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý trường hợp không tìm thấy tài nguyên (CourseId, CategoryId, CourseImageId)
     * Trả về giao diện lỗi thân thiện, không làm sập ứng dụng hay văng Whitelabel Error Page.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorCode", 404);
        model.addAttribute("errorTitle", "Không tìm thấy dữ liệu (404 Not Found)");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("backUrl", "/courses");
        return "error/error";
    }

    /**
     * Xử lý các vi phạm ràng buộc nghiệp vụ (Business Rule)
     */
    @ExceptionHandler(BusinessRuleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBusinessRule(BusinessRuleException ex, Model model) {
        model.addAttribute("errorCode", 400);
        model.addAttribute("errorTitle", "Vi phạm ràng buộc nghiệp vụ (400 Bad Request)");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("backUrl", "/courses");
        return "error/error";
    }

    /**
     * Xử lý lỗi hệ thống bất ngờ (500)
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGlobalException(Exception ex, Model model) {
        model.addAttribute("errorCode", 500);
        model.addAttribute("errorTitle", "Lỗi xử lý hệ thống");
        model.addAttribute("errorMessage", "Hệ thống gặp sự cố trong quá trình xử lý: " + ex.getMessage());
        model.addAttribute("backUrl", "/courses");
        return "error/error";
    }
}
