package com.example.saratogapizza.services;

import com.example.saratogapizza.entities.*;
import com.example.saratogapizza.exceptions.AddressException;
import com.example.saratogapizza.exceptions.AuthException;
import com.example.saratogapizza.exceptions.BankDetailsException;
import com.example.saratogapizza.exceptions.VerifyException;
import com.example.saratogapizza.mappers.AddressMapper;
import com.example.saratogapizza.mappers.BankDetailsMapper;
import com.example.saratogapizza.repositories.AddressRepository;
import com.example.saratogapizza.repositories.BankDetailsRepository;
import com.example.saratogapizza.repositories.UserRepository;
import com.example.saratogapizza.requests.*;
import com.example.saratogapizza.responses.*;

import com.example.saratogapizza.utils.EmailVerifyCodeGenerator;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    private final StringRedisTemplate stringRedisTemplate;

    private final BankDetailsRepository bankDetailsRepository;

    private final EmailService emailService;

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

        Set<GetCustomerBankDetailsResponse> bankDetailsResponses = getBankDetailsInfo(user.getUserId());

        response.setBankDetails(bankDetailsResponses);

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


    //bankDetails CRUD starting...
    public Set<GetCustomerBankDetailsResponse> getBankDetailsInfo(Long userId) {

        User user = userRepository.findByUserId(userId);

        if(user == null){
            throw new AuthException("User not found with id "+userId);
        }
        Set<BankDetails> bankDetails = bankDetailsRepository.findByUser(user);

        return bankDetails.stream().map(this::mapToCustomerBankDetailsResponse).collect(Collectors.toSet());
    }

    private GetCustomerBankDetailsResponse mapToCustomerBankDetailsResponse(BankDetails bankDetails) {
        return BankDetailsMapper.mapToCustomerBankDetailsResponse(bankDetails);
    }

    public AddNewBankDetailsResponse addNewBankDetail(Long userId, BankDetailsRequest request) {

        User user = userRepository.findByUserId(userId);

        if(user == null){
            throw new AuthException("User not found with id "+userId);
        }



        BankDetails isSaved = bankDetailsRepository.findByUserAndAccountName(user,request.getAccountName());



        if(isSaved != null){
            throw new BankDetailsException("Bank details already exists");
        }

        BankDetails bankDetails = new BankDetails();

        bankDetails.setAccountName(request.getAccountName());
        bankDetails.setAccountNumber(request.getAccountNumber());
        bankDetails.setBankName(request.getBankName());
        bankDetails.setAccountHolderName(request.getAccountHolderName());
        bankDetails.setIban(request.getIban());
        bankDetails.setCvv(request.getCvv());
        bankDetails.setLastValidDate(request.getLastValidDate());
        bankDetails.setCreatedAt(LocalDateTime.now());
        bankDetails.setUpdatedAt(LocalDateTime.now());
        bankDetails.setUser(user);
        bankDetailsRepository.save(bankDetails);


        AddNewBankDetailsResponse response = new AddNewBankDetailsResponse();

        response.setMessage("Successfully bank details added!");

        return response;
    }

    public DeleteBankDetailsResponse deleteBankDetails(Long userId, Long bankDetailsId) {

        User user = userRepository.findByUserId(userId);

        if(user == null){
            throw new AuthException("User not found with id "+userId);
        }



        BankDetails bankDetails = bankDetailsRepository.findByBankDetailsId(bankDetailsId);

        if(bankDetails == null){
            throw new BankDetailsException("Bank details not found");
        }

        bankDetailsRepository.delete(bankDetails);


        DeleteBankDetailsResponse response = new DeleteBankDetailsResponse();

        response.setMessage("Successfully deleted");

        return response;
    }

    public ChangeBankDetailsResponse changeBankDetails(Long userId, Long bankDetailsId, BankDetailsRequest request) {

        User user = userRepository.findByUserId(userId);

        BankDetails bankDetails = bankDetailsRepository.findByBankDetailsId(bankDetailsId);

//        BankDetails c = bankDetailsRepository.findByUserAndAccountName(user,request.getAccountName());

//        if (c == null) {
//            throw new BankDetailsException("No bank details found with given account name");
//        }

//        if (!Objects.equals(c.getBankDetailsId(), bankDetails.getBankDetailsId())) {
//            throw new BankDetailsException("Does not match bank details");
//        }



        if(user == null){
            throw new AuthException("User not found with id "+userId);
        }

        if(bankDetails == null){
            throw new BankDetailsException("Bank details not found");
        }

        if(request.getAccountHolderName()!=null){
            bankDetails.setAccountHolderName(request.getAccountHolderName());
        }

        if(request.getBankName()!=null){
            bankDetails.setBankName(request.getBankName());
        }
        if(request.getIban()!=null){
            bankDetails.setIban(request.getIban());
        }
        if(request.getAccountNumber()!=null){
            bankDetails.setAccountNumber(request.getAccountNumber());
        }
        if(request.getAccountName()!=null){
            bankDetails.setAccountNumber(request.getAccountName());
        }
        if(request.getLastValidDate()!=null){
            bankDetails.setLastValidDate(request.getLastValidDate());
        }
        if(request.getCvv()!=null){
            bankDetails.setCvv(request.getCvv());
        }

        bankDetails.setUser(user);

        bankDetails.setUpdatedAt(LocalDateTime.now());

        bankDetailsRepository.save(bankDetails);


        ChangeBankDetailsResponse response = new ChangeBankDetailsResponse();

        response.setMessage("Successfully changed bank details");

        return response;
    }
    //bankDetails CRUD ending...

    public ChangeUserInfoResponse changeOtherDetails(Long userId, ChangeUserInfoRequest request) throws MessagingException {

        User user = userRepository.findByUserId(userId);

        if(user == null){
            throw new AuthException("User not found with id "+userId);
        }

        if (request.getName()!=null){
            user.setName(request.getName());
        }

        if (request.getLastname()!=null){
            user.setLastname(request.getLastname());
        }

        if (request.getEmail()!=null){
            user.setEmail(request.getEmail());
            //when user request the mail changes it ll be -> change mail,set verification as false and verify service should proceed.
            user.setVerified(false);
            String code = String.valueOf((int)(Math.random() * 900000) + 100000);
            emailService.sendVerificationOfEmailChange(user.getEmail(), code,user.getName(),user.getLastname());

        }

        if (request.getMobileNo()!=null){
            user.setMobileNo(request.getMobileNo());
        }

        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        ChangeUserInfoResponse response = new ChangeUserInfoResponse();

        response.setMessage("Successfully changed other details");

        return response;

    }

    //review CRUD starting...

    //review CRUD ending...




    //change user info

    //PUT /api/users/me/password → Kullanıcı kendi şifresini günceller.
    //POST /api/users/me/password/reset-request → Şifre sıfırlama için mail/token gönderme (opsiyonel)
    //POST /api/users/me/password/reset → Token doğrulanarak yeni şifre belirleme





}
