package com.example.saratogapizza.services;

import com.example.saratogapizza.entities.Address;
import com.example.saratogapizza.entities.User;
import com.example.saratogapizza.exceptions.AuthException;
import com.example.saratogapizza.exceptions.VerifyException;
import com.example.saratogapizza.repositories.AddressRepository;
import com.example.saratogapizza.repositories.UserRepository;
import com.example.saratogapizza.responses.CompleteRegisterResponse;
import com.example.saratogapizza.responses.VerifyAccountResponse;
import com.example.saratogapizza.requests.CustomerCompleteInfoRequest;
import com.example.saratogapizza.requests.VerifyAccountRequest;
import com.example.saratogapizza.utils.EmailVerifyCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

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








    //change user info
    //PUT /api/users/me/password → Kullanıcı kendi şifresini günceller.
    //POST /api/users/me/password/reset-request → Şifre sıfırlama için mail/token gönderme (opsiyonel)
    //POST /api/users/me/password/reset → Token doğrulanarak yeni şifre belirleme




}
