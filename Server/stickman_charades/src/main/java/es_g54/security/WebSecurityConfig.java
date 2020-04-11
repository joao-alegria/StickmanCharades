package es_g54.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/admin/**").hasRole("ADMIN")
            .antMatchers("/register").permitAll()
            .antMatchers("/login").authenticated()
            .antMatchers("/friends/**", "/session/**", "/game/**").hasRole("USER")
            .and().httpBasic()
            .and().logout().logoutUrl("/logout").invalidateHttpSession(true).deleteCookies("JSESSIONID").clearAuthentication(true)
            .and().csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder()).dataSource(dataSource)
            .usersByUsernameQuery("SELECT username, password, enabled FROM dbuser WHERE username=?")
            .authoritiesByUsernameQuery(
                    "SELECT dbuser.username, dbrole.name " +
                            "FROM dbuser JOIN dbrole_users ON dbuser.id = dbrole_users.users_id " +
                            "JOIN dbrole ON dbrole_users.roles_id = dbrole.id " +
                            "WHERE dbuser.username = ?"
            );
    }
}
