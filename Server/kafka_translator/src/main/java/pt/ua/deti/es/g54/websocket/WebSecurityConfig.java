package pt.ua.deti.es.g54.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /* TODO
        http.authorizeRequests()
            .antMatchers("/admin/**").hasRole("ADMIN")
            .antMatchers("/game/**").hasRole("USER")
            .and().httpBasic()
            .and().csrf().disable();
        */
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        /* TODO
        auth
            .jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder()).dataSource(dataSource)
            .usersByUsernameQuery("SELECT username, password, enabled FROM dbuser WHERE username=?")
            .authoritiesByUsernameQuery(
                    "SELECT dbuser.username, dbrole.name " +
                            "FROM dbuser JOIN dbrole_users ON dbuser.id = dbrole_users.users_id " +
                            "JOIN dbrole ON dbrole_users.roles_id = dbrole.id " +
                            "WHERE dbuser.username = ?"
            );
         */
    }
}
