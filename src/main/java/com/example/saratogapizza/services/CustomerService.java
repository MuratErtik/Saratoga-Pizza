package com.example.saratogapizza.services;

import com.example.saratogapizza.entities.*;
import com.example.saratogapizza.exceptions.AddressException;
import com.example.saratogapizza.exceptions.AuthException;
import com.example.saratogapizza.exceptions.VerifyException;
import com.example.saratogapizza.mappers.AddressMapper;
import com.example.saratogapizza.repositories.AddressRepository;
import com.example.saratogapizza.repositories.UserRepository;
import com.example.saratogapizza.requests.AddressRequest;
import com.example.saratogapizza.responses.*;

import com.example.saratogapizza.requests.CustomerCompleteInfoRequest;
import com.example.saratogapizza.requests.VerifyAccountRequest;
import com.example.saratogapizza.utils.EmailVerifyCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    private final StringRedisTemplate stringRedisTemplate;

    public CompleteRegisterResponse completeRegister(CustomerCompleteInfoRequest request,Long userId){

        User user = userRepository.findByUserId(userId);

        if(user == null){
            throw new AuthException("User not found with id "+userId);
        }

        if (request.getAddress() == null) {
            throw new AuthException("Address is required");
        }

        user.setMobileNo(request.getMobileNo());

        Set<Address> addresses = user.getAddresses();

        if (addresses == null) {
            addresses = new HashSet<>();
        }

        Address isSaved = addressRepository.findByUserAndAddressName(user,request.getAddress().getAddressName());

        if(isSaved != null){
            throw new AuthException("Address is already registered");
        }

        Address address = new Address();

        address.setAddressName(request.getAddress().getAddressName());

        address.setStreet(request.getAddress().getStreet());

        address.setCity(request.getAddress().getCity());

        address.setBuildingNo(request.getAddress().getBuildingNo());

        address.setDistrict(request.getAddress().getDistrict());

        address.setCountry(request.getAddress().getCountry());

        address.setFloorNo(request.getAddress().getFloorNo());

        address.setApartmentNo(request.getAddress().getApartmentNo());

        address.setAddressNote(request.getAddress().getAddressNote());

        address.setUser(user);

        addressRepository.save(address);

        addresses.add(address);

        user.setAddresses(addresses);

        userRepository.save(user);

        CompleteRegisterResponse response = new CompleteRegisterResponse();

        response.setMessage("Successfully registered");

        return response;


    }



    //verify mail
    public VerifyAccountResponse verifyAccount(VerifyAccountRequest request, Long userId) {

        String key = "email:verify:" + userId;

        String redisCode = stringRedisTemplate.opsForValue().get(key);

        if (redisCode == null)  throw new VerifyException("Invalid verification code") ;

        if (!redisCode.equals(request.getCode())) throw new VerifyException("Invalid verification code") ;

        User user = userRepository.findByUserId(userId);

        String code = EmailVerifyCodeGenerator.generateCode();

        if(user == null){
            throw new AuthException("User not found with id "+userId);
        }

        if (user.isVerified()) {
            throw new AuthException("User is already verified");
        }


        user.setVerified(true);

        userRepository.save(user);

        stringRedisTemplate.delete(key);

        VerifyAccountResponse response = new VerifyAccountResponse();

        response.setMessage("Successfully verified");

        return response;
    }


    //getuserinfo
    public GetCustomerInfoResponse getAllInfo(Long userId) {

        User user = userRepository.findByUserId(userId);

        if (user == null) throw new AuthException("User not found with id "+userId);

        GetCustomerInfoResponse response = mapToCustomerInfoResponse(user);

        return response;


    }

    private GetCustomerInfoResponse mapToCustomerInfoResponse(User user) {

        GetCustomerInfoResponse response = new GetCustomerInfoResponse();

        response.setName(user.getName());

        response.setEmail(user.getEmail());

        response.setMobileNo(user.getMobileNo());

        Set<GetCustomerAddressesResponse> addresses = getAddressInfo(user.getUserId());

        response.setAddresses(addresses);

        response.setLastname(user.getLastname());

        response.setVerified(user.isVerified());

        response.setCreatedAt(user.getCreatedAt());

        response.setLastLoginAt(user.getLastLoginAt());

//        response.setBankDetails();

//        response.setOrders();
//
//        response.setReviews();

        response.setUsedCoupons(user.getUsedCoupons());

        response.setLoyaltyPoints(user.getLoyaltyPoints());

        return response;

    }

    //address CRUD starting...
    public Set<GetCustomerAddressesResponse> getAddressInfo(Long userId) {

        User user = userRepository.findByUserId(userId);

        Set<Address> addresses = user.getAddresses();

        Set<GetCustomerAddressesResponse> responses = addresses.stream().map(this::mapToAddressResponse).collect(Collectors.toSet());

        return responses;

    }

    private GetCustomerAddressesResponse mapToAddressResponse(Address address){
        return AddressMapper.mapToAddressResponse(address);
    }

    public AddNewAddressResponse addNewAddress(Long userId, AddressRequest request) {

        User user = userRepository.findByUserId(userId);

        if(user == null){
            throw new AuthException("User not found with id "+userId);
        }



        Set<Address> addresses = user.getAddresses();

        if (addresses == null) {
            addresses = new HashSet<>();
        }

        Address isSaved = addressRepository.findByUserAndAddressName(user,request.getAddressName());

        if(isSaved != null){
            throw new AuthException("Address is already registered");
        }

        Address address = new Address();

        address.setAddressName(request.getAddressName());

        address.setStreet(request.getStreet());

        address.setCity(request.getCity());

        address.setBuildingNo(request.getBuildingNo());

        address.setDistrict(request.getDistrict());

        address.setCountry(request.getCountry());

        address.setFloorNo(request.getFloorNo());

        address.setApartmentNo(request.getApartmentNo());

        address.setAddressNote(request.getAddressNote());

        address.setUser(user);

        addressRepository.save(address);

        addresses.add(address);

        user.setAddresses(addresses);

        userRepository.save(user);

        AddNewAddressResponse response = new AddNewAddressResponse();

        response.setMessage("Successfully registered");

        return response;

    }

    public DeleteNewAddressResponse deleteNewAddress(Long userId, Long addressId) {

        User user = userRepository.findByUserId(userId);

        if(user == null){
            throw new AuthException("User not found with id "+userId);
        }



        Set<Address> addresses = user.getAddresses();

        if (addresses == null) {
            throw new AddressException("There is no any address to delete");
        }

        Address address = addressRepository.findByAddressId(addressId);

        if(address == null){
            throw new AddressException("Address not found with id "+addressId);
        }

        addressRepository.delete(address);

        addresses.remove(address);

        user.setAddresses(addresses);

        userRepository.save(user);

        DeleteNewAddressResponse response = new DeleteNewAddressResponse();

        response.setMessage("Successfully registered");

        return response;


    }

    public ChangeNewAddressResponse changeAddress(Long userId, Long addressId,AddressRequest request) {

        User user = userRepository.findByUserId(userId);

        if(user == null){
            throw new AuthException("User not found with id "+userId);
        }



        Set<Address> addresses = user.getAddresses();

        if (addresses == null) {
            throw new AddressException("There is no any address to change");
        }

        Address address = addressRepository.findByAddressId(addressId);

        if(address == null){
            throw new AddressException("Address not found with id "+addressId);
        }

        if(request.getAddressName()!=null){
            address.setAddressName(request.getAddressName());
        }

        if(request.getStreet()!=null){
            address.setStreet(request.getStreet());
        }
        if(request.getCity()!=null){
            address.setCity(request.getCity());
        }
        if(request.getBuildingNo()!=null){
            address.setBuildingNo(request.getBuildingNo());
        }
        if(request.getDistrict()!=null){
            address.setDistrict(request.getDistrict());
        }
        if(request.getCountry()!=null){
            address.setCountry(request.getCountry());
        }
        if(request.getFloorNo()!=null){
            address.setFloorNo(request.getFloorNo());
        }
        if(request.getApartmentNo()!=null){
            address.setApartmentNo(request.getApartmentNo());
        }
        if(request.getAddressNote()!=null){
            address.setAddressNote(request.getAddressNote());
        }

        address.setUser(user);

        addressRepository.save(address);

        ChangeNewAddressResponse response = new ChangeNewAddressResponse();

        response.setMessage("Successfully changed address");

        return response;
    }
    //address CRUD ending...



    //change user info
    //PUT /api/users/me/password → Kullanıcı kendi şifresini günceller.
    //POST /api/users/me/password/reset-request → Şifre sıfırlama için mail/token gönderme (opsiyonel)
    //POST /api/users/me/password/reset → Token doğrulanarak yeni şifre belirleme





}
