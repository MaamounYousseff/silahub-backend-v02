package com.example.useradmin.web;

import com.example.useradmin.domain.model.User;
import com.example.useradmin.logic.dto.UserDto;
import com.example.useradmin.logic.service.UserAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@Controller
@RequestMapping("/admin/v0/users")
public class UserAdminController {

    @Autowired
    private UserAdminService userAdminService;


    @GetMapping({"", "/"})
    public String redirectToDashboard() {
        return "redirect:/admin/v0/users/dashboard";
    }

    // Dashboard - List all users with pagination
    @GetMapping("/dashboard")
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            Model model) {

        if(size > 6)
            size = 6;


        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,size,sort);

        Page<UserDto> userDtoPageable = this.userAdminService.getUserDtos(pageable);
        model.addAttribute("users",userDtoPageable.getContent());
        model.addAttribute("totalPages",userDtoPageable.getTotalPages()-1);
        model.addAttribute("totalRows",userDtoPageable.getTotalElements());

        // Implementation will return paginated users
        return "dashboard";
    }


    @PostMapping("/disable")
    public String disableUser(@RequestParam  UUID userId, Model model)
    {
        User user  = this.userAdminService.disableUser(userId);
        model.addAttribute("user",user);

        if(user == null)
            return "partials/user-not-disabled";

        return "partials/user-disabled";
    }


    @PostMapping("/enable")
    public String enableUser(@RequestParam UUID userId, Model model)
    {
        User user = this.userAdminService.enableUser(userId);
        model.addAttribute("user",user);
        if(user == null)
            return "partials/user-not-enabled";

        return "partials/user-enabled";

    }






}