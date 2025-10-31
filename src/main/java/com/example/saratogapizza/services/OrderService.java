package com.example.saratogapizza.services;

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
import com.example.saratogapizza.responses.CreateOrderResponse;
import com.example.saratogapizza.responses.GetCustomerAddressesResponse;
import com.example.saratogapizza.responses.GetCustomerCartResponse;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

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

        Cart newCart = new Cart();

        newCart.setUser(user);
        newCart.setCreatedAt(LocalDateTime.now());
        newCart.setCheckedOut(false);
        newCart.setTotalSellingPrice(BigDecimal.ZERO);
        newCart.setTotalItem(0);
        newCart.setDiscount(BigDecimal.ZERO);
        newCart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(newCart);


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

        GetCustomerCartResponse response = cartService.getMyActiveCart(userId);

        createOrderResponse.setCart(response);

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
