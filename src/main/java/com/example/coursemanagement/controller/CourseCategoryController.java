package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.CourseCategory;
import com.example.coursemanagement.exception.BusinessRuleException;
import com.example.coursemanagement.service.CourseCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CourseCategoryController {

    private final CourseCategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("newCategory", new CourseCategory());
        return "categories/list";
    }

    @PostMapping("/create")
    public String createCategory(
            @Valid @ModelAttribute("newCategory") CourseCategory category,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "categories/list";
        }

        try {
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm loại khóa học thành công!");
            return "redirect:/categories";
        } catch (BusinessRuleException ex) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("errorMessage", ex.getMessage());
            return "categories/list";
        }
    }

    /**
     * Chức năng 5 - Ràng buộc 1 (Demo Yêu cầu 13):
     * Thử xóa một loại khóa học đang được sử dụng -> Bị chặn và báo lỗi thân thiện!
     */
    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa loại khóa học thành công!");
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/categories";
    }
}
