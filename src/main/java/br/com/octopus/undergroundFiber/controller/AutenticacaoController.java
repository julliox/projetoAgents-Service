package br.com.octopus.undergroundFiber.controller;


import br.com.octopus.undergroundFiber.entity.UserEntity;
import br.com.octopus.undergroundFiber.security.JwtProvider;
import br.com.octopus.undergroundFiber.service.UserService;
import br.com.octopus.undergroundFiber.suport.dtos.JwtDTO;
import br.com.octopus.undergroundFiber.suport.dtos.LoginDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/authentication")
public class AutenticacaoController {


    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

//    @PostMapping
//    public ResponseEntity<Object> cadastrarNovo(@RequestBody @Valid UsuarioDTO usuarioDto) {
//        log.debug("POST cadastrarNovo usuarioDto recebido {} ", usuarioDto.toString());
//        if (usuarioService.possuiUsuarioComCpf(usuarioDto.getCpf())) {
//            log.warn("Username {} Já existe um cadastro com este cpf: ", usuarioDto.getCpf());
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Já existe um cadastro com este CPF");
//        }
//        if (usuarioService.possuiUsuarioComEmail(usuarioDto.getEmail())) {
//            log.warn("Email {} Já existe um cadastro com este email ", usuarioDto.getEmail());
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Já existe um cadastro com este email");
//        }
//        usuarioDto.setSenha(passwordEncoder.encode(usuarioDto.getSenha()));
//        UsuarioResponseDTO usuarioSalvo = usuarioService.cadastrar(usuarioDto);
//
//        log.info("Usuario cadastrado com sucesso cpf:  {} ", usuarioSalvo.getCpf());
//        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
//    }

    @PostMapping("/login")
    public ResponseEntity<JwtDTO> authenticateUser(@Valid @RequestBody LoginDTO loginDto) {
        passwordEncoder.encode(loginDto.getSenha());
        UserEntity userEntity = userService.findByEmail(loginDto.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getSenha()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwt(authentication, userEntity);
        return ResponseEntity.ok(new JwtDTO(jwt));
    }

    @GetMapping("/")
    public String index() {
        log.trace("TRACE");
        log.debug("DEBUG");
        log.info("INFO");
        log.warn("WARN");
        log.error("ERROR");
        return "Logging Spring Boot...";
    }
}
