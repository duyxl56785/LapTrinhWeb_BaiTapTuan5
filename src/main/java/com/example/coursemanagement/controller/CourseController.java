package com.example.coursemanagement.controller;

import com.example.coursemanagement.dto.CourseFilterDto;
import com.example.coursemanagement.dto.CourseFormDto;
import com.example.coursemanagement.entity.Course;
import com.example.coursemanagement.exception.BusinessRuleException;
import com.example.coursemanagement.service.CourseCategoryService;
import com.example.coursemanagement.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping({"/", "/courses"})
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final CourseCategoryService categoryService;

    /**
     * Chức năng 2: Danh sách và tra cứu khóa học
     * Hỗ trợ tìm kiếm theo tên, loại, trạng thái, khoảng học phí, sắp xếp.
     * Giữ lại các giá trị đã nhập trên Form sau khi submit.
     */
    @GetMapping({"", "/courses"})
    public String listCourses(@ModelAttribute("filter") CourseFilterDto filter, Model model) {
        // Đảm bảo giá trị mặc định cho filter nếu null
        if (filter.getStatus() == null) {
            filter.setStatus("all");
        }

        try {
            List<Course> courses = courseService.findAll(filter);
            model.addAttribute("courses", courses);
        } catch (BusinessRuleException ex) {
            // Xử lý lỗi khoảng học phí min > max mà không làm văng 500
            model.addAttribute("filterError", ex.getMessage());
            model.addAttribute("courses", Collections.emptyList());
        }

        model.addAttribute("categories", categoryService.findAll());
        return "courses/list";
    }

    /**
     * Trang xem chi tiết khóa học và quản lý ảnh (Chức năng 4)
     */
    @GetMapping("/courses/{id}")
    public String courseDetail(@PathVariable("id") Long id, Model model) {
        Course course = courseService.findById(id);
        model.addAttribute("course", course);
        return "courses/detail";
    }

    /**
     * Chức năng 1: Trang Create khóa học
     */
    @GetMapping("/courses/create")
    public String showCreateForm(Model model) {
        model.addAttribute("courseForm", new CourseFormDto());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("isEdit", false);
        return "courses/form";
    }

    /**
     * Xử lý thêm mới khóa học kèm Validation
     */
    @PostMapping("/courses/create")
    public String createCourse(
            @Valid @ModelAttribute("courseForm") CourseFormDto formDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("isEdit", false);
            return "courses/form";
        }

        try {
            courseService.save(formDto);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm mới khóa học thành công!");
            return "redirect:/courses";
        } catch (BusinessRuleException ex) {
            bindingResult.rejectValue("name", "duplicate", ex.getMessage());
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("isEdit", false);
            return "courses/form";
        }
    }

    /**
     * Chức năng 1: Trang Edit khóa học
     */
    @GetMapping("/courses/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Course course = courseService.findById(id);
        model.addAttribute("courseForm", courseService.toFormDto(course));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("isEdit", true);
        return "courses/form";
    }

    /**
     * Xử lý cập nhật khóa học kèm Validation
     */
    @PostMapping("/courses/{id}/edit")
    public String updateCourse(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("courseForm") CourseFormDto formDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        formDto.setId(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("isEdit", true);
            return "courses/form";
        }

        try {
            courseService.save(formDto);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khóa học thành công!");
            return "redirect:/courses";
        } catch (BusinessRuleException ex) {
            bindingResult.rejectValue("name", "duplicate", ex.getMessage());
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("isEdit", true);
            return "courses/form";
        }
    }

    /**
     * Xóa khóa học
     */
    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        courseService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa khóa học thành công!");
        return "redirect:/courses";
    }

    /**
     * Chức năng 4: Thêm ảnh cho khóa học bằng URL
     */
    @PostMapping("/courses/{id}/images")
    public String addImage(
            @PathVariable("id") Long id,
            @RequestParam("imageUrl") String imageUrl,
            @RequestParam(value = "isThumbnail", defaultValue = "false") Boolean isThumbnail,
            RedirectAttributes redirectAttributes) {

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đường dẫn URL ảnh không được để trống!");
            return "redirect:/courses/" + id;
        }

        courseService.addImage(id, imageUrl, isThumbnail);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm ảnh cho khóa học thành công!");
        return "redirect:/courses/" + id;
    }

    /**
     * Chức năng 4: Đổi ảnh đại diện
     */
    @PostMapping("/courses/{id}/images/{imageId}/set-thumbnail")
    public String setThumbnail(
            @PathVariable("id") Long id,
            @PathVariable("imageId") Long imageId,
            RedirectAttributes redirectAttributes) {

        courseService.setThumbnail(id, imageId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã đổi ảnh đại diện thành công!");
        return "redirect:/courses/" + id;
    }

    /**
     * Xóa ảnh khỏi khóa học
     */
    @PostMapping("/courses/{id}/images/{imageId}/delete")
    public String deleteImage(
            @PathVariable("id") Long id,
            @PathVariable("imageId") Long imageId,
            RedirectAttributes redirectAttributes) {

        courseService.deleteImage(id, imageId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa ảnh thành công!");
        return "redirect:/courses/" + id;
    }
}
