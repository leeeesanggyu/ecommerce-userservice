package com.userservice.service;

import com.userservice.client.OrderServiceClient;
import com.userservice.domain.dto.OrderRes;
import com.userservice.domain.dto.UserDto;
import com.userservice.domain.entity.UserEntity;
import com.userservice.repository.UserRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bcrypt;
    private final RestTemplate restTemplate;
    private final Environment env;
    private final OrderServiceClient orderServiceClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new User(user.getEmail(), user.getEncryptedPwd(),
                true, true, true, true,
                new ArrayList<>());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        final UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(bcrypt.encode(userDto.getPwd()));

        final UserEntity result = userRepository.save(userEntity);
        return modelMapper.map(result, UserDto.class);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        final UserDto userDto = modelMapper.map(userEntity, UserDto.class);

//        final ResponseEntity<List<OrderRes>> orderResList = restTemplate.exchange(
//                String.format(env.getProperty("orderService.uri"), userId),
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<List<OrderRes>>() {}
//        );

        List<OrderRes> orders = null;
        try {
            orders = orderServiceClient.getOrders(userId);
        } catch (FeignException e) {
            log.error("getOrders error", e);
        }
        userDto.setOrders(orders);
        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(IllegalStateException::new);
        return modelMapper.map(userEntity, UserDto.class);
    }
}
