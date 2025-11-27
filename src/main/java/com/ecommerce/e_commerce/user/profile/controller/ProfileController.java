package com.ecommerce.e_commerce.user.profile.controller;

import com.ecommerce.e_commerce.common.dto.PaginatedResponse;
import com.ecommerce.e_commerce.user.profile.dtos.ChangePasswordRequest;
import com.ecommerce.e_commerce.user.profile.dtos.OrderSummeryResponse;
import com.ecommerce.e_commerce.user.profile.dtos.ProfileResponse;
import com.ecommerce.e_commerce.user.profile.dtos.UpdateProfileRequest;
import com.ecommerce.e_commerce.user.profile.service.ProfileService;
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
@RequestMapping("/api/v1/user/profile/")
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getMyProfile(HttpServletRequest request) {
        return ResponseEntity.ok(profileService.getMyProfile(request));
    }

    @PatchMapping
    public ResponseEntity<ProfileResponse> updateMyProfile(HttpServletRequest request,
                                                           @RequestBody @Valid UpdateProfileRequest profileRequest) {
        return ResponseEntity.ok(profileService.updateProfile(request, profileRequest));
    }

    @PatchMapping("password")
    public ResponseEntity<Void> changePassword(HttpServletRequest request,
                                               @RequestBody @Valid ChangePasswordRequest passwordRequest) {
        profileService.changePassword(request, passwordRequest);
        return ResponseEntity.noContent().build();
    }

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
