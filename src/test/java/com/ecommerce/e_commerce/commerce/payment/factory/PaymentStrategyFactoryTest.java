package com.ecommerce.e_commerce.commerce.payment.factory;

import com.ecommerce.e_commerce.commerce.payment.enums.PaymentMethod;
import com.ecommerce.e_commerce.commerce.payment.strategy.offline.OfflinePaymentStrategy;
import com.ecommerce.e_commerce.commerce.payment.strategy.online.OnlinePaymentStrategy;
import com.ecommerce.e_commerce.common.exception.UnsupportedPaymentMethodException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.ecommerce.e_commerce.common.utils.Constants.PAYMENT_SUPPORT_IS_MISSING_FOR_THIS_METHOD;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentStrategyFactoryTest {
    @Mock
    private OnlinePaymentStrategy paypalStrategy;
    @Mock
    private OfflinePaymentStrategy codStrategy;
    private PaymentStrategyFactory factory;

    @BeforeEach
    void setUp() {
        when(paypalStrategy.getPaymentMethod()).thenReturn(PaymentMethod.PAYPAL);
        when(codStrategy.getPaymentMethod()).thenReturn(PaymentMethod.COD);
        factory = new PaymentStrategyFactory(List.of(paypalStrategy, codStrategy));
    }

    @Test
    void getOnlineStrategy_ShouldReturnCorrectStrategy_WhenMethodExists() {
        // Act
        OnlinePaymentStrategy result = factory.getOnlineStrategy(PaymentMethod.PAYPAL);
        // Assert
        assertThat(result).isEqualTo(paypalStrategy);
    }

    @Test
    void getOnlineStrategy_ShouldThrowException_WhenMethodNotRegistered() {
        // Act & Assert
        assertThatThrownBy(() -> factory.getOnlineStrategy(PaymentMethod.COD))
                .isInstanceOf(UnsupportedPaymentMethodException.class)
                .hasMessageContaining(PAYMENT_SUPPORT_IS_MISSING_FOR_THIS_METHOD);
    }

    @Test
    void getOfflineStrategy_ShouldReturnCorrectStrategy_WhenMethodExists() {
        // Act
        OfflinePaymentStrategy result = factory.getOfflineStrategy(PaymentMethod.COD);
        // Assert
        assertThat(result).isEqualTo(codStrategy);
    }

    @Test
    void getOfflineStrategy_ShouldThrowException_WhenMethodNotRegistered() {
        // Act & Assert
        assertThatThrownBy(() -> factory.getOfflineStrategy(PaymentMethod.PAYPAL))
                .isInstanceOf(UnsupportedPaymentMethodException.class)
                .hasMessageContaining(PAYMENT_SUPPORT_IS_MISSING_FOR_THIS_METHOD);
    }

    @Test
    void constructor_ShouldThrowException_WhenEmptyStrategyList() {
        // Arrange
        PaymentStrategyFactory emptyFactory = new PaymentStrategyFactory(List.of());
        // Act & Assert
        assertThatThrownBy(() -> emptyFactory.getOnlineStrategy(PaymentMethod.PAYPAL))
                .isInstanceOf(UnsupportedPaymentMethodException.class);
        assertThatThrownBy(() -> emptyFactory.getOfflineStrategy(PaymentMethod.COD))
                .isInstanceOf(UnsupportedPaymentMethodException.class);
    }
}