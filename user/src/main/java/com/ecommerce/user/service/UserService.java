package com.ecommerce.user.service;


import com.ecommerce.user.dto.AddressDto;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.model.Address;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> fetchAllUsers()
    {
        return userRepository.findAll().stream().map(this::mapToUserResponse).collect(Collectors.toList());
    }

    public UserResponse addUser(UserRequest user)
    {

        User savedUser=userRepository.save(mapToUser(user));
        return mapToUserResponse(savedUser);
    }


    public UserResponse fetchUser(String id) {

        Optional<User> user=userRepository.findById(String.valueOf(id));
        return user.map(this::mapToUserResponse).orElse(null);
    }

    public boolean updateUser(String id,UserRequest updatedUser)
    {
        return userRepository.findById(String.valueOf(id))
                .map(user->
                        {
                            user.setFirstName(updatedUser.getFirstName());
                            user.setLastName(updatedUser.getLastName());
                            userRepository.save(user);
                            return true;
                        }).orElse(false);
    }


    private User mapToUser(UserRequest userRequest)
    {
        User user=new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        if(userRequest.getAddress()!=null)
        {
            Address address=new Address();
            address.setStreet(userRequest.getAddress().getStreet());
            address.setCity(userRequest.getAddress().getCity());
            address.setState(userRequest.getAddress().getState());
            address.setCountry(userRequest.getAddress().getCountry());
            address.setZipcode(userRequest.getAddress().getZipcode());
            user.setAddress(address);
        }
        return user;
    }



    private UserResponse mapToUserResponse(User user)
    {
        UserResponse userResponse=new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setRole(user.getRole());
        if(user.getAddress()!=null)
        {
            AddressDto address=new AddressDto();
            address.setStreet(user.getAddress().getStreet());
            address.setCity(user.getAddress().getCity());
            address.setState(user.getAddress().getState());
            address.setCountry(user.getAddress().getCountry());
            address.setZipcode(user.getAddress().getZipcode());
            userResponse.setAddress(address);
        }
        return userResponse;
    }



}
