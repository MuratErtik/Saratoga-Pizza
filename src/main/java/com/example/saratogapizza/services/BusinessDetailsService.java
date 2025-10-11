package com.example.saratogapizza.services;

import com.example.saratogapizza.entities.Address;
import com.example.saratogapizza.entities.BusinessDetails;
import com.example.saratogapizza.entities.User;
import com.example.saratogapizza.exceptions.AuthException;
import com.example.saratogapizza.repositories.AddressRepository;
import com.example.saratogapizza.repositories.BusinessDetailsRepository;
import com.example.saratogapizza.repositories.UserRepository;
import com.example.saratogapizza.requests.BusinessAddressRequest;
import com.example.saratogapizza.requests.BusinessDetailsRequest;
import com.example.saratogapizza.responses.BusinessDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BusinessDetailsService {

    private final BusinessDetailsRepository businessDetailsRepository;

    private final ImageUploadService imageUploadService;

    private final AddressRepository addressRepository;

    private final UserRepository userRepository;

    public BusinessDetailsResponse createBusiness(BusinessDetailsRequest request,Long userId) throws IOException {

        BusinessDetails business = new BusinessDetails();

        business.setBusinessName(request.getBusinessName());
        business.setDescription(request.getDescription());
        business.setPhone(request.getPhone());
        business.setEmail(request.getEmail());

        Address address = mapToAddress(request.getAddress());
        business.setAddress(address);
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new AuthException("User does not find");
        }
        address.setUser(user);
        addressRepository.save(address);




        business.setOpenHours(request.getOpenHours());
        business.setMinimumOrderAmount(request.getMinimumOrderAmount());
        business.setDeliveryFee(request.getDeliveryFee());
        business.setCreatedAt(LocalDateTime.now());
        business.setUpdatedAt(LocalDateTime.now());

        if (request.getLogo() != null && !request.getLogo().isEmpty()) {
            String logoUrl = imageUploadService.uploadImage(request.getLogo());
            business.setLogoUrl(logoUrl);
        }

        BusinessDetails saved = businessDetailsRepository.save(business);

        // Response dön
        BusinessDetailsResponse response = new BusinessDetailsResponse();
        response.setId(saved.getId());
        response.setBusinessName(saved.getBusinessName());
        response.setDescription(saved.getDescription());
        response.setPhone(saved.getPhone());
        response.setEmail(saved.getEmail());

        response.setLogoUrl(saved.getLogoUrl());
        return response;
    }

    public Address mapToAddress(BusinessAddressRequest request ) {
        Address address = new Address();
        address.setCity(request.getCity());
        address.setCountry(request.getCountry());
        address.setStreet(request.getStreet());
        address.setBuildingNo(request.getBuildingNo());
        address.setAddressName(request.getAddressName());
        address.setDistrict(request.getDistrict());

        return address;


    }

    public BusinessDetailsResponse updateBusiness(Long businessId, BusinessDetailsRequest request,Long userId) throws IOException {

        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new AuthException("User does not find");
        }

        BusinessDetails business = businessDetailsRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        if (request.getBusinessName() != null) {
            business.setBusinessName(request.getBusinessName());
        }
        if (request.getDescription() != null) {
            business.setDescription(request.getDescription());
        }
        if (request.getPhone() != null) {
            business.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            business.setEmail(request.getEmail());
        }


        if (request.getAddress() != null) {
            Address address = business.getAddress();
            if (address == null) {
                address = mapToAddress(request.getAddress());
                business.setAddress(address);
            } else {
                address.setStreet(request.getAddress().getStreet());
                address.setCity(request.getAddress().getCity());
                address.setCountry(request.getAddress().getCountry());
                address.setDistrict(request.getAddress().getDistrict());
                address.setBuildingNo(request.getAddress().getBuildingNo());
                address.setAddressName(request.getAddress().getAddressName());
            }
            addressRepository.save(address);
        }

        if (request.getOpenHours() != null) {
            business.setOpenHours(request.getOpenHours());
        }
        if (request.getMinimumOrderAmount() != null) {
            business.setMinimumOrderAmount(request.getMinimumOrderAmount());
        }
        if (request.getDeliveryFee() != null) {
            business.setDeliveryFee(request.getDeliveryFee());
        }

        business.setUpdatedAt(LocalDateTime.now());

        if (request.getLogo() != null && !request.getLogo().isEmpty()) {
            if (business.getLogoUrl() != null) {
                imageUploadService.deleteImage(business.getLogoUrl());
            }
            String logoUrl = imageUploadService.uploadImage(request.getLogo());
            business.setLogoUrl(logoUrl);
        }

        BusinessDetails updated = businessDetailsRepository.save(business);

        BusinessDetailsResponse response = new BusinessDetailsResponse();
        response.setId(updated.getId());
        response.setBusinessName(updated.getBusinessName());
        response.setDescription(updated.getDescription());
        response.setPhone(updated.getPhone());
        response.setEmail(updated.getEmail());
        response.setLogoUrl(updated.getLogoUrl());

        return response;
    }

    public void deleteBusiness(Long businessId, Long userId) throws IOException {
        BusinessDetails business = businessDetailsRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        if (!business.getAddress().getUser().getUserId().equals(userId)) {
            throw new AuthException("You are not authorized to delete this business");
        }

        if (business.getLogoUrl() != null) {
            imageUploadService.deleteImage(business.getLogoUrl());
        }

        businessDetailsRepository.delete(business);
    }


}
