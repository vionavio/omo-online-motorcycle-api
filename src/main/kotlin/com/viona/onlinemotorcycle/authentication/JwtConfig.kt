package com.viona.onlinemotorcycle.authentication

import com.viona.onlinemotorcycle.customer.entity.Customer
import com.viona.onlinemotorcycle.driver.entity.Driver
import com.viona.onlinemotorcycle.utils.Constant
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

@EnableWebSecurity
@Configuration
class JwtConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var authenticationFilter: AuthenticationFilter

    override fun configure(http: HttpSecurity) {
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .csrf().disable()
            .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, *postPermit.toTypedArray()).permitAll()
            .anyRequest().authenticated()
    }

    companion object {
        val postPermit = listOf(
            "/api/customer/login",
            "/api/customer/register",
            "/api/driver/register",
            "/api/driver/login"
        )

        fun generateToken(user: Customer): String {
            val subject = user.id
            val expired = Date(System.currentTimeMillis() + (60_000 * 60 * 24))
            val granted = AuthorityUtils.commaSeparatedStringToAuthorityList(user.username)
            val grantedStream = granted.stream().map { it.authority }.collect(Collectors.toList())

            return Jwts.builder()
                .setSubject(subject)
                .claim(Constant.CLAIMS, grantedStream)
                .setExpiration(expired)
                .signWith(Keys.hmacShaKeyFor(Constant.SECRET.toByteArray()), SignatureAlgorithm.HS256)
                .compact()
        }

        fun generateTokenDriver(driver: Driver): String {
            val subjectDriver = driver.id
            val expired = Date(System.currentTimeMillis() + (60_000 * 60 * 24))
            val granted = AuthorityUtils.commaSeparatedStringToAuthorityList(driver.username)
            val grantedStream = granted.stream().map { it.authority }.collect(Collectors.toList())

            return Jwts.builder()
                .setSubject(subjectDriver)
                .claim(Constant.CLAIMS, grantedStream)
                .setExpiration(expired)
                .signWith(Keys.hmacShaKeyFor(Constant.SECRET.toByteArray()), SignatureAlgorithm.HS256)
                .compact()
        }

        fun isPermit(request: HttpServletRequest): Boolean {
            val path = request.servletPath
            return postPermit.contains(path)
        }
    }
}

/*fun main(args: Array<String>) {
    val keys = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    val secret = Encoders.BASE64.encode(keys.encoded)
    println("secret is ->> $secret")
}*/
