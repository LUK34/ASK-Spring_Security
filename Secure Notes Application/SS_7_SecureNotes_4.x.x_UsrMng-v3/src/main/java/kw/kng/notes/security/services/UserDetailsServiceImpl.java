package kw.kng.notes.security.services;

import kw.kng.notes.entities.User;
import kw.kng.notes.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * ============================================================================================
 * CUSTOM USER DETAILS SERVICE IMPLEMENTATION
 * ============================================================================================
 *
 * PURPOSE:
 *
 * UserDetailsImpl tells Spring Security HOW our custom application User should
 * be represented.
 *
 * This class tells Spring Security HOW that User should be LOADED from our
 * application's persistent storage.
 *
 * The two classes work together:
 *
 *     UserDetailsServiceImpl
 *              |
 *              | loads
 *              v
 *         User Entity
 *              |
 *              | converts using
 *              v
 *     UserDetailsImpl.build(user)
 *              |
 *              v
 *        UserDetails
 *              |
 *              v
 *      Spring Security
 *
 * UserDetailsService is a core Spring Security interface.
 *
 * Its main method is:
 *
 *     loadUserByUsername(String username)
 *
 * Spring Security's username/password authentication infrastructure can call
 * this method when it needs to retrieve a user.
 *
 * ============================================================================================
 */


/*
 * @Service registers this class as a Spring-managed service bean.
 *
 * Because it becomes a Spring bean, it can participate in dependency injection
 * and can be used by Spring Security as a UserDetailsService implementation.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService 
{
    /*
     * Repository used to retrieve our custom User entity from the database.
     *
     * The transcript describes this class as the place where we tell
     * Spring Security:
     *
     *     "This is how you load my application's user."
     *
     * UserRepository is therefore the link between this security service
     * and our persistent User data.
     */
    @Autowired
    UserRepository userRepository;

    /*
     * ========================================================================================
     * LOAD USER BY USERNAME
     * ========================================================================================
     *
     * This method comes from the UserDetailsService interface.
     *
     * Spring Security calls this method when it needs to locate a user by
     * username during authentication.
     *
     * Input:
     *
     *     username
     *
     * Output:
     *
     *     UserDetails
     *
     * In our application, the returned UserDetails object is actually a
     * UserDetailsImpl instance.
     *
     * Complete flow:
     *
     *     Username entered by user
     *              |
     *              v
     *     loadUserByUsername(username)
     *              |
     *              v
     *       UserRepository
     *              |
     *              v
     *         Database
     *              |
     *              v
     *          User entity
     *              |
     *              v
     *     UserDetailsImpl.build(user)
     *              |
     *              v
     *        UserDetailsImpl
     *              |
     *              v
     *          UserDetails
     *              |
     *              v
     *      Spring Security
     *
     * @Transactional ensures that the database work performed while loading
     * the user happens within a transaction.
     *
     * This can also be useful if related data such as the user's role is
     * lazily loaded while the UserDetails object is being constructed.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
    {
        /*
         * Search our application's User table/model using the repository.
         *
         * findByUserName(username) returns an Optional<User>.
         *
         * If a matching user exists:
         *
         *     Optional contains User
         *
         * If no matching user exists:
         *
         *     Optional is empty
         */
        User user = userRepository.findByUserName(username)
					        		 /*
					                 * If the repository cannot find the requested username,
					                 * throw Spring Security's UsernameNotFoundException.
					                 *
					                 * This signals that authentication cannot continue because
					                 * there is no user matching the supplied username.
					                 */
                				  .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        
        
        /*
         * Convert our custom User entity into UserDetailsImpl.
         *
         * UserDetailsImpl.build(user) maps:
         *
         *     user ID
         *     username
         *     email
         *     password
         *     2FA flag
         *     role/authority
         *
         * into the format expected by Spring Security.
         *
         * UserDetailsImpl implements UserDetails, so it can be returned from
         * this method even though the method's declared return type is
         * UserDetails.
         */
        return UserDetailsImpl.build(user);
    }




}
