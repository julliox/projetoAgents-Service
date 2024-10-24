package br.com.octopus.undergroundFiber.security;


import br.com.octopus.undergroundFiber.entity.UserEntity;
import br.com.octopus.undergroundFiber.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity usuario = userService.findByEmail(email);
        return UserDetailsImpl.build(usuario);
    }

}
