package com.ecommerce.e_commerce.commerce.payment.factory;

import com.ecommerce.e_commerce.commerce.payment.enums.PaymentMethod;
import com.ecommerce.e_commerce.commerce.payment.strategy.PaymentStrategy;
import com.ecommerce.e_commerce.commerce.payment.strategy.offline.OfflinePaymentStrategy;
import com.ecommerce.e_commerce.commerce.payment.strategy.online.OnlinePaymentStrategy;
import com.ecommerce.e_commerce.common.exception.UnsupportedPaymentMethodException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ecommerce.e_commerce.common.utils.Constants.PAYMENT_SUPPORT_IS_MISSING_FOR_THIS_METHOD;

@Component
public class PaymentStrategyFactory {
    private final Map<PaymentMethod, OnlinePaymentStrategy> onlineStrategies;
    private final Map<PaymentMethod, OfflinePaymentStrategy> offlineStrategies;

    public PaymentStrategyFactory(List<PaymentStrategy> strategyList) {
        this.onlineStrategies = strategyList
                .stream()
                .filter(s -> s instanceof OnlinePaymentStrategy)
                .collect(Collectors.toMap(PaymentStrategy::getPaymentMethod, s -> (OnlinePaymentStrategy) s));
        this.offlineStrategies = strategyList
                .stream()
                .filter(s -> s instanceof OfflinePaymentStrategy)
                .collect(Collectors.toMap(PaymentStrategy::getPaymentMethod, s -> (OfflinePaymentStrategy) s));
    }

    public OnlinePaymentStrategy getOnlineStrategy(PaymentMethod method) {
        return Optional.ofNullable(onlineStrategies.get(method))
                .orElseThrow(() -> new UnsupportedPaymentMethodException(PAYMENT_SUPPORT_IS_MISSING_FOR_THIS_METHOD));
    }

    public OfflinePaymentStrategy getOfflineStrategy(PaymentMethod method) {
        return Optional.ofNullable(offlineStrategies.get(method))
                .orElseThrow(() -> new UnsupportedPaymentMethodException(PAYMENT_SUPPORT_IS_MISSING_FOR_THIS_METHOD));
    }
}
