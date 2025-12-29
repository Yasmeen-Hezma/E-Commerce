package com.ecommerce.e_commerce.user.profile.controller;

import com.ecommerce.e_commerce.common.dto.PaginatedResponse;
import com.ecommerce.e_commerce.user.profile.dto.ChangePasswordRequest;
import com.ecommerce.e_commerce.user.profile.dto.OrderSummeryResponse;
import com.ecommerce.e_commerce.user.profile.dto.ProfileResponse;
import com.ecommerce.e_commerce.user.profile.dto.UpdateProfileRequest;
import com.ecommerce.e_commerce.user.profile.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Tag(name = "User Profile", description = "User Profile Management APIs")
@RequestMapping("/api/v1/user/profile/")
public class ProfileController {
    private final ProfileService profileService;

    @Operation(summary = "Get current user profile")
    @GetMapping
    public ResponseEntity<ProfileResponse> getMyProfile(HttpServletRequest request) {
        return ResponseEntity.ok(profileService.getMyProfile(request));
    }

    @Operation(summary = "Update current user profile")
    @PatchMapping
    public ResponseEntity<ProfileResponse> updateMyProfile(HttpServletRequest request,
                                                           @RequestBody @Valid UpdateProfileRequest profileRequest) {
        return ResponseEntity.ok(profileService.updateProfile(request, profileRequest));
    }

    @Operation(summary = "Change current user password")
    @PatchMapping("password")
    public ResponseEntity<Void> changePassword(HttpServletRequest request,
                                               @RequestBody @Valid ChangePasswordRequest passwordRequest) {
        profileService.changePassword(request, passwordRequest);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get current user orders with pagination")
    @GetMapping("orders")
    public ResponseEntity<PaginatedResponse<OrderSummeryResponse>> getMyOrders(HttpServletRequest request,
                                                                               @RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "10") int size,
                                                                               @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                               @RequestParam(defaultValue = "DESC") String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return ResponseEntity.ok(profileService.getMyOrders(request, pageable));
    }
}
