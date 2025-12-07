package com.ecommerce.e_commerce.commerce.review.service;

import com.ecommerce.e_commerce.commerce.product.enums.ProductStatus;
import com.ecommerce.e_commerce.commerce.product.model.Product;
import com.ecommerce.e_commerce.commerce.product.repository.ProductRepository;
import com.ecommerce.e_commerce.commerce.product.service.ProductService;
import com.ecommerce.e_commerce.commerce.review.dto.ReviewRequest;
import com.ecommerce.e_commerce.commerce.review.dto.ReviewResponse;
import com.ecommerce.e_commerce.commerce.review.mapper.ReviewMapper;
import com.ecommerce.e_commerce.commerce.review.model.Review;
import com.ecommerce.e_commerce.commerce.review.repository.ReviewRepository;
import com.ecommerce.e_commerce.common.dto.PaginatedResponse;
import com.ecommerce.e_commerce.common.exception.DuplicateItemException;
import com.ecommerce.e_commerce.common.exception.ItemNotFoundException;
import com.ecommerce.e_commerce.common.exception.UnauthorizedException;
import com.ecommerce.e_commerce.user.profile.model.User;
import com.ecommerce.e_commerce.user.profile.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ecommerce.e_commerce.common.utils.Constants.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {
    @Mock
    private ProductService productService;
    @Mock
    private UserService userService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ReviewMapper reviewMapper;
    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review review;
    private User user;
    private Product product;
    private ReviewRequest reviewRequest;
    private ReviewResponse reviewResponse;

    @BeforeEach
    void setUp() {
        product = Product
                .builder()
                .productId(1L)
                .productName("test product")
                .description("test description")
                .price(BigDecimal.valueOf(99.99))
                .quantity(10)
                .status(ProductStatus.AVAILABLE)
                .reviewCount(0)
                .discount(BigDecimal.ZERO)
                .averageRating(BigDecimal.ZERO)
                .orderItems(new ArrayList<>())
                .cartItems(new ArrayList<>())
                .reviews(new ArrayList<>())
                .deleted(false)
                .build();

        user = User
                .builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .phone("1234567890")
                .phoneCode(20)
                .orders(new ArrayList<>())
                .build();

        review = Review
                .builder()
                .reviewId(1L)
                .product(product)
                .user(user)
                .title("Excellent!")
                .comment("Great product!")
                .rating(5)
                .deleted(false)
                .build();

        // Maintain bidirectional relationship
        product.getReviews().add(review);

        reviewRequest = new ReviewRequest();
        reviewRequest.setComment("Great product!");
        reviewRequest.setRating(5);

        reviewResponse = ReviewResponse
                .builder()
                .reviewId(1L)
                .productId(1L)
                .userId(1L)
                .rating(5)
                .title("Excellent!")
                .comment("Great product!")
                .build();
    }

    @Test
    void createReview_ShouldCreateReview_WhenValidRequest() {
        // Arrange
        when(userService.getUserByRequest(httpRequest)).thenReturn(user);
        when(productService.getNonDeletedProductById(1L)).thenReturn(product);
        when(reviewRepository.existsByProduct_ProductIdAndUser_IdAndDeletedFalse(1L, 1L)).thenReturn(false);
        when(reviewMapper.toEntity(reviewRequest, product, user)).thenReturn(review);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewRepository.calculateAverageRating(1L)).thenReturn(Optional.of(BigDecimal.valueOf(5)));
        when(reviewRepository.countByProduct_ProductIdAndDeletedFalse(1L)).thenReturn(1L);
        when(reviewMapper.toResponse(review)).thenReturn(reviewResponse);
        // Act
        ReviewResponse result = reviewService.createReview(1L, reviewRequest, httpRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(5);
        assertThat(result.getComment()).isEqualTo("Great product!");

        verify(userService).getUserByRequest(httpRequest);
        verify(productService).getNonDeletedProductById(1L);
        verify(reviewRepository).existsByProduct_ProductIdAndUser_IdAndDeletedFalse(1L, 1L);
        verify(reviewMapper).toEntity(reviewRequest, product, user);
        verify(reviewRepository).save(any(Review.class));
        verify(reviewRepository).calculateAverageRating(1L);
        verify(reviewRepository).countByProduct_ProductIdAndDeletedFalse(1L);
        verify(productRepository).updateReviewStats(eq(1L), any(BigDecimal.class), eq(1L));
        verify(reviewMapper).toResponse(review);
    }

    @Test
    void createReview_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        when(productService.getNonDeletedProductById(99L))
                .thenThrow(new ItemNotFoundException(PRODUCT_NOT_FOUND));
        // Act & Assert
        assertThatThrownBy(() -> reviewService.createReview(99L, reviewRequest, httpRequest))
                .isInstanceOf(ItemNotFoundException.class);
        verify(productService).getNonDeletedProductById(99L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_ShouldThrowException_WhenUserAlreadyReviewedProduct() {
        // Arrange
        when(userService.getUserByRequest(httpRequest)).thenReturn(user);
        when(productService.getNonDeletedProductById(1L)).thenReturn(product);
        when(reviewRepository.existsByProduct_ProductIdAndUser_IdAndDeletedFalse(1L, 1L)).thenReturn(true);
        // Act & Assert
        assertThatThrownBy(() -> reviewService.createReview(1L, reviewRequest, httpRequest))
                .isInstanceOf(DuplicateItemException.class);
        verify(userService).getUserByRequest(httpRequest);
        verify(productService).getNonDeletedProductById(1L);
        verify(reviewRepository).existsByProduct_ProductIdAndUser_IdAndDeletedFalse(1L, 1L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void updateReview_ShouldUpdateReview_WhenValidRequest() {
        // Arrange
        reviewRequest.setRating(4);
        reviewRequest.setComment("updated comment :)");
        when(productService.getNonDeletedProductById(1L)).thenReturn(product);
        when(reviewRepository.findByReviewIdAndDeletedFalse(1L)).thenReturn(Optional.of(review));
        when(userService.getUserId(httpRequest)).thenReturn(1L);
        doAnswer(invocation -> {
            ReviewRequest request = invocation.getArgument(0);
            Review r = invocation.getArgument(1);
            r.setComment(request.getComment());
            r.setRating(request.getRating());
            return null;
        }).when(reviewMapper).updateReviewFromRequest(any(), any());
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewRepository.calculateAverageRating(1L)).thenReturn(Optional.of(BigDecimal.valueOf(4.0)));
        when(reviewRepository.countByProduct_ProductIdAndDeletedFalse(1L)).thenReturn(1L);
        when(reviewMapper.toResponse(any(Review.class))).thenAnswer(invocation -> {
            Review r = invocation.getArgument(0);
            return ReviewResponse.builder()
                    .comment(r.getComment())
                    .rating(r.getRating())
                    .build();
        });
        // Act
        ReviewResponse result = reviewService.updateReview(1L, 1L, reviewRequest, httpRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(4);
        assertThat(result.getComment()).isEqualTo("updated comment :)");
        verify(productService).getNonDeletedProductById(1L);
        verify(reviewRepository).findByReviewIdAndDeletedFalse(1L);
        verify(userService).getUserId(httpRequest);
        verify(reviewMapper).updateReviewFromRequest(reviewRequest, review);
        verify(reviewRepository).save(review);
        verify(reviewRepository).calculateAverageRating(1L);
        verify(reviewRepository).countByProduct_ProductIdAndDeletedFalse(1L);
        verify(productRepository).updateReviewStats(eq(1L), any(BigDecimal.class), eq(1L));
        verify(reviewMapper).toResponse(review);
    }

    @Test
    void updateReview_shouldThrowException_WhenReviewNotFound() {
        // Arrange
        when(productService.getNonDeletedProductById(1L)).thenReturn(product);
        when(reviewRepository.findByReviewIdAndDeletedFalse(99L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> reviewService.updateReview(1L, 99L, reviewRequest, httpRequest))
                .isInstanceOf(ItemNotFoundException.class);
        verify(productService).getNonDeletedProductById(1L);
        verify(reviewRepository).findByReviewIdAndDeletedFalse(99L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void updateReview_ShouldThrowException_WhenReviewDoesNotBelongToProduct() {
        // Arrange
        Product newProduct = Product
                .builder()
                .productId(10L)
                .build();
        review.setProduct(newProduct);
        when(productService.getNonDeletedProductById(1L)).thenReturn(product);
        when(reviewRepository.findByReviewIdAndDeletedFalse(1L)).thenReturn(Optional.of(review));
        when(userService.getUserId(httpRequest)).thenReturn(1L);
        // Act & Assert
        assertThatThrownBy(() -> reviewService.updateReview(1L, 1L, reviewRequest, httpRequest))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(REVIEW_NOT_FOUND_FOR_THIS_PRODUCT);
        verify(productService).getNonDeletedProductById(1L);
        verify(reviewRepository).findByReviewIdAndDeletedFalse(1L);
        verify(userService).getUserId(httpRequest);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void updateReview_ShouldThrowException_WhenUserIsNotReviewOwner() {
        // Arrange
        when(productService.getNonDeletedProductById(1L)).thenReturn(product);
        when(reviewRepository.findByReviewIdAndDeletedFalse(1L)).thenReturn(Optional.of(review));
        when(userService.getUserId(httpRequest)).thenReturn(10L); // different user
        // Act & Assert
        assertThatThrownBy(() -> reviewService.updateReview(1L, 1L, reviewRequest, httpRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining(YOU_CAN_ONLY_ACCESS_YOUR_OWN_REVIEWS);
        verify(productService).getNonDeletedProductById(1L);
        verify(reviewRepository).findByReviewIdAndDeletedFalse(1L);
        verify(userService).getUserId(httpRequest);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void deleteReview_ShouldSoftDeleteReview_WhenValidRequest() {
        // Arrange
        when(productService.getNonDeletedProductById(1L)).thenReturn(product);
        when(reviewRepository.findByReviewIdAndDeletedFalse(1L)).thenReturn(Optional.of(review));
        when(userService.getUserId(httpRequest)).thenReturn(1L);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewRepository.calculateAverageRating(1L)).thenReturn(Optional.of(BigDecimal.ZERO));
        when(reviewRepository.countByProduct_ProductIdAndDeletedFalse(1L)).thenReturn(0L);
        // Act
        reviewService.deleteReview(1L, 1L, httpRequest);
        //Assert
        verify(productService).getNonDeletedProductById(1L);
        verify(reviewRepository).findByReviewIdAndDeletedFalse(1L);
        verify(userService).getUserId(httpRequest);
        verify(reviewRepository).save(argThat(Review::isDeleted));
        verify(productRepository).updateReviewStats(eq(1L), any(BigDecimal.class), eq(0L));
    }

    @Test
    void deleteReview_ShouldThrowException_WhenReviewNotFound() {
        // Arrange
        when(productService.getNonDeletedProductById(1L)).thenReturn(product);
        when(reviewRepository.findByReviewIdAndDeletedFalse(99L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> reviewService.deleteReview(1L, 99L, httpRequest))
                .isInstanceOf(ItemNotFoundException.class);
        verify(productService).getNonDeletedProductById(1L);
        verify(reviewRepository).findByReviewIdAndDeletedFalse(99L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void deleteReview_ShouldThrowException_WhenUserIsNotReviewOwner() {
        // Arrange
        when(productService.getNonDeletedProductById(1L)).thenReturn(product);
        when(reviewRepository.findByReviewIdAndDeletedFalse(1L)).thenReturn(Optional.of(review));
        when(userService.getUserId(httpRequest)).thenReturn(10L); // different user
        // Act & Assert
        assertThatThrownBy(() -> reviewService.deleteReview(1L, 1L, httpRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining(YOU_CAN_ONLY_ACCESS_YOUR_OWN_REVIEWS);
        verify(productService).getNonDeletedProductById(1L);
        verify(reviewRepository).findByReviewIdAndDeletedFalse(1L);
        verify(userService).getUserId(httpRequest);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void getReviewById_ShouldReturnReview_WhenValidRequest() {
        // Arrange
        when(productService.getNonDeletedProductById(1L)).thenReturn(product);
        when(reviewRepository.findByReviewIdAndDeletedFalse(1L)).thenReturn(Optional.of(review));
        when(reviewMapper.toResponse(review)).thenReturn(reviewResponse);
        // Act
        ReviewResponse result = reviewService.getReviewById(1L, 1L);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getReviewId()).isEqualTo(1L);
        verify(productService).getNonDeletedProductById(1L);
        verify(reviewRepository).findByReviewIdAndDeletedFalse(1L);
        verify(reviewMapper).toResponse(review);
    }

    @Test
    void getReviewById_ShouldThrowException_WhenReviewNotFound() {
        // Arrange
        when(productService.getNonDeletedProductById(1L)).thenReturn(product);
        when(reviewRepository.findByReviewIdAndDeletedFalse(99L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> reviewService.getReviewById(99L, 1L))
                .isInstanceOf(ItemNotFoundException.class);
        verify(productService).getNonDeletedProductById(1L);
        verify(reviewRepository).findByReviewIdAndDeletedFalse(99L);
    }

    @Test
    void getReviewById_ShouldThrowException_WhenReviewDoesNotBelongToProduct() {
        // Arrange
        Product newProduct = Product
                .builder()
                .productId(10L)
                .build();
        review.setProduct(newProduct);
        when(productService.getNonDeletedProductById(1L)).thenReturn(product);
        when(reviewRepository.findByReviewIdAndDeletedFalse(1L)).thenReturn(Optional.of(review));
        // Act & Assert
        assertThatThrownBy(() -> reviewService.getReviewById(1L, 1L))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(REVIEW_NOT_FOUND_FOR_THIS_PRODUCT);
        verify(productService).getNonDeletedProductById(1L);
        verify(reviewRepository).findByReviewIdAndDeletedFalse(1L);
    }

    @Test
    void getProductReviews_ShouldReturnPaginatedReviews_WhenReviewsExist() {
        // Arrange
        Review review2 = Review
                .builder()
                .reviewId(2L)
                .product(product)
                .user(user)
                .rating(4)
                .comment("good product")
                .deleted(false)
                .build();
        ReviewResponse reviewResponse2 = ReviewResponse
                .builder()
                .reviewId(2L)
                .rating(4)
                .comment("good product")
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewPage = new PageImpl<>(List.of(review, review2), pageable, 2);

        when(reviewRepository.findByProduct_ProductIdAndDeletedFalse(1L, pageable)).thenReturn(reviewPage);
        when(reviewMapper.toResponse(review)).thenReturn(reviewResponse);
        when(reviewMapper.toResponse(review2)).thenReturn(reviewResponse2);

        // Act
        PaginatedResponse<ReviewResponse> result = reviewService.getProductReviews(1L, pageable);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPayload()).hasSize(2);
        assertThat(result.getTotal()).isEqualTo(2);
        assertThat(result.getPayload().get(0).getRating()).isEqualTo(5);
        assertThat(result.getPayload().get(1).getRating()).isEqualTo(4);
        verify(reviewRepository).findByProduct_ProductIdAndDeletedFalse(1L, pageable);
        verify(reviewMapper, times(2)).toResponse(any(Review.class));
    }

    @Test
    void getProductReviews_ShouldReturnEmptyList_WhenNoReviewsExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(reviewRepository.findByProduct_ProductIdAndDeletedFalse(1L, pageable)).thenReturn(emptyPage);
        // Act
        PaginatedResponse<ReviewResponse> result = reviewService.getProductReviews(1L, pageable);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPayload()).isEmpty();
        assertThat(result.getTotal()).isEqualTo(0);
        verify(reviewRepository).findByProduct_ProductIdAndDeletedFalse(1L, pageable);
    }
}