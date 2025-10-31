package com.example.saratogapizza.services;

import com.example.saratogapizza.configs.RabbitConfig;
import com.example.saratogapizza.domains.OrderStatus;
import com.example.saratogapizza.domains.PaymentDetail;
import com.example.saratogapizza.domains.PaymentStatus;
import com.example.saratogapizza.domains.UserRole;
import com.example.saratogapizza.entities.Address;
import com.example.saratogapizza.entities.Cart;
import com.example.saratogapizza.entities.Order;
import com.example.saratogapizza.entities.User;
import com.example.saratogapizza.exceptions.AddressException;
import com.example.saratogapizza.exceptions.AuthException;
import com.example.saratogapizza.exceptions.OrderException;
import com.example.saratogapizza.repositories.AddressRepository;
import com.example.saratogapizza.repositories.CartRepository;
import com.example.saratogapizza.repositories.OrderRepository;
import com.example.saratogapizza.repositories.UserRepository;
import com.example.saratogapizza.requests.CreateOrderRequest;
import com.example.saratogapizza.responses.*;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;

    private final CartRepository cartRepository;

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    private final CustomerService customerService;

    @Autowired
    private RabbitTemplate rabbitTemplate;



    public CreateOrderResponse createOrder(Long userId, CreateOrderRequest request) {

        validateRequest(request);

        User user = userRepository.findById(userId).orElseThrow(() -> new AuthException("User not found"));

        Cart cart = cartRepository.findByUserAndCheckedOutFalse(user).orElseThrow(() -> new AuthException("Cart not found"));

        if (cart.getCartItems().isEmpty()) throw new OrderException("Cart is empty so you can not create order");

        Order order = new Order();

        Address address = addressRepository.findByAddressId(request.getShippingAddressId());

        if (address == null)  throw new AddressException("Address not found");

        order.setShippingAddress(address);

        order.setCart(cart);

        cart.setCheckedOut(true);
        cartRepository.save(cart);

//        rabbitTemplate.convertAndSend(
//                RabbitConfig.EXCHANGE,
//                RabbitConfig.ORDER_CREATED_ROUTING_KEY,
//                userId
//        );


        order.setOrderStatus(OrderStatus.PENDING);

        order.setOrderDate(LocalDateTime.now());

        order.setDeliverDate(LocalDateTime.now().plusMinutes(40));

        order.setNotes(request.getNotes());

        orderRepository.save(order);

        CreateOrderResponse createOrderResponse = new CreateOrderResponse();

        createOrderResponse.setId(order.getId());

        createOrderResponse.setMessage("Order successfully created");

        Optional<GetCustomerAddressesResponse> getCustomerAddressesResponse = customerService.getAddressInfo(userId).stream()
                        .filter(a -> a.getAddressName().equals(address.getAddressName()))
                .findFirst();


        if (getCustomerAddressesResponse.isEmpty()) throw new AddressException("Address not found");


        createOrderResponse.setGetCustomerAddresses(getCustomerAddressesResponse.get());



//        GetCustomerCartResponse response = cartService.getMyActiveCart(userId);

        GetCustomerCartResponse getCustomerCartResponse = new GetCustomerCartResponse();

        CouponResponseToCart couponResponseToCart = cartService.mapCouponResponseToCart(cart.getCoupon());
        getCustomerCartResponse.setCoupon(couponResponseToCart);

        getCustomerCartResponse.setDiscount(cart.getDiscount());

        getCustomerCartResponse.setTotalItem(cart.getTotalItem()); // if i am wrong i ll fix later

        getCustomerCartResponse.setTotalSellingPrice(cart.getTotalSellingPrice());


        Set<CartItemResponseToCart> cartItemResponseToCarts = cart.getCartItems().stream()
                .map(cartService::mapCartItemToResponse)
                .collect(Collectors.toSet());

        getCustomerCartResponse.setCartItems(cartItemResponseToCarts);

        createOrderResponse.setCart(getCustomerCartResponse);

        createOrderResponse.setOrderDate(LocalDateTime.now());

        createOrderResponse.setOrderStatus(OrderStatus.PENDING);

        createOrderResponse.setNotes(request.getNotes());

        createOrderResponse.setDeliverDate(LocalDateTime.now().plusMinutes(40));

        return createOrderResponse;

    }
    // kullanicaya bildirim gidecek -> senin siparisi aldim
    // admine bildirim gidecek -> sana siparis geldi
    // inventory service de stok azaltilmali

    private void validateRequest(CreateOrderRequest createOrderRequest) {

        if (createOrderRequest.getShippingAddressId() == null) {
            throw new AddressException("Shipping Address not found");
        }

    }
}
