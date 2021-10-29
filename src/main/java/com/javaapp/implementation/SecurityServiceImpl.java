package com.javaapp.implementation;

import com.javaapp.entity.User;
import com.javaapp.entity.common.UserPrincipal;
import com.javaapp.repository.UserRepository;
import com.javaapp.service.SecurityService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

    private UserRepository userRepository;

    public SecurityServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        User user = userRepository.findByUserName(s);

        if(user==null){
            throw new UsernameNotFoundException("This user does not exists");
        }

        return new UserPrincipal(user);
    }
}
