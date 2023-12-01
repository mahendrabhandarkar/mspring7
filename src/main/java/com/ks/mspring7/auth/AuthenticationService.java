package com.ks.mspring7.auth;

import com.ks.mspring7.config.JwtService;
import com.ks.mspring7.token.Token;
import com.ks.mspring7.token.TokenRepository;
import com.ks.mspring7.token.TokenType;
import com.ks.mspring7.user.Role;
import com.ks.mspring7.user.User;
import com.ks.mspring7.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  /**
   * Returns an Image object that can then be painted on the screen.
   * The url argument must specify an absolute <a href="#{@link}">{@link URL}</a>. The name
   * argument is a specifier that is relative to the url argument.
   * <p>
   * This method always returns immediately, whether or not the
   * image exists. When this applet attempts to draw the image on
   * the screen, the data will be loaded. The graphics primitives
   * that draw the image will incrementally paint on the screen.
   *
   * @param  url  an absolute URL giving the base location of the image
   * @param  name the location of the image, relative to the url argument
   * @return      the image at the specified URL
   * @see         Image
   */

  public AuthenticationResponse register(RegisterRequest request) {
    var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .build();
    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  /**
   * Returns an Image object that can then be painted on the screen.
   * The url argument must specify an absolute <a href="#{@link}">{@link URL}</a>. The name
   * argument is a specifier that is relative to the url argument.
   * <p>
   * This method always returns immediately, whether or not the
   * image exists. When this applet attempts to draw the image on
   * the screen, the data will be loaded. The graphics primitives
   * that draw the image will incrementally paint on the screen.
   *
   * @param  url  an absolute URL giving the base location of the image
   * @param  name the location of the image, relative to the url argument
   * @return      the image at the specified URL
   * @see         Image
   */

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  /**
   * Returns an Image object that can then be painted on the screen.
   * The url argument must specify an absolute <a href="#{@link}">{@link URL}</a>. The name
   * argument is a specifier that is relative to the url argument.
   * <p>
   * This method always returns immediately, whether or not the
   * image exists. When this applet attempts to draw the image on
   * the screen, the data will be loaded. The graphics primitives
   * that draw the image will incrementally paint on the screen.
   *
   * @param  url  an absolute URL giving the base location of the image
   * @param  name the location of the image, relative to the url argument
   * @return      the image at the specified URL
   * @see         Image
   */

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  /**
   * Returns an Image object that can then be painted on the screen.
   * The url argument must specify an absolute <a href="#{@link}">{@link URL}</a>. The name
   * argument is a specifier that is relative to the url argument.
   * <p>
   * This method always returns immediately, whether or not the
   * image exists. When this applet attempts to draw the image on
   * the screen, the data will be loaded. The graphics primitives
   * that draw the image will incrementally paint on the screen.
   *
   * @param  url  an absolute URL giving the base location of the image
   * @param  name the location of the image, relative to the url argument
   * @return      the image at the specified URL
   * @see         Image
   */

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  /**
   * Returns an Image object that can then be painted on the screen.
   * The url argument must specify an absolute <a href="#{@link}">{@link URL}</a>. The name
   * argument is a specifier that is relative to the url argument.
   * <p>
   * This method always returns immediately, whether or not the
   * image exists. When this applet attempts to draw the image on
   * the screen, the data will be loaded. The graphics primitives
   * that draw the image will incrementally paint on the screen.
   *
   * @param  url  an absolute URL giving the base location of the image
   * @param  name the location of the image, relative to the url argument
   * @return      the image at the specified URL
   * @see         Image
   */

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
}
