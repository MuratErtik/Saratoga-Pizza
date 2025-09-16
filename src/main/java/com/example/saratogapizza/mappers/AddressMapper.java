package com.example.saratogapizza.mappers;

import com.example.saratogapizza.entities.Address;
import com.example.saratogapizza.responses.GetCustomerAddressesResponse;

public class AddressMapper {

    public static  GetCustomerAddressesResponse mapToAddressResponse(Address address){

        GetCustomerAddressesResponse response = new GetCustomerAddressesResponse();
        response.setStreet(address.getStreet());
        response.setCity(address.getCity());
        response.setDistrict(address.getDistrict());
        response.setCountry(address.getCountry());
        response.setFloorNo(address.getFloorNo());
        response.setApartmentNo(address.getApartmentNo());
        response.setAddressNote(address.getAddressNote());
        response.setBuildingNo(address.getBuildingNo());
        response.setAddressName(address.getAddressName());
        return response;
    }
}
