package com.example.saratogapizza.services;

import com.example.saratogapizza.entities.Address;
import com.example.saratogapizza.entities.User;
import com.example.saratogapizza.exceptions.AuthException;
import com.example.saratogapizza.repositories.AddressRepository;
import com.example.saratogapizza.repositories.UserRepository;
import com.example.saratogapizza.repsonses.CompleteRegisterResponse;
import com.example.saratogapizza.requests.CustomerCompleteInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

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

}
