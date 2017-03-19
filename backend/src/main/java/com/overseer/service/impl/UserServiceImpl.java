package com.overseer.service.impl;

import com.overseer.auth.service.SecurityContextService;
import com.overseer.dao.UserDao;
import com.overseer.exception.RemovingYourselfException;
import com.overseer.exception.entity.EntityAlreadyExistsException;
import com.overseer.exception.entity.NoSuchEntityException;
import com.overseer.model.Role;
import com.overseer.model.User;
import com.overseer.service.EmailBuilder;
import com.overseer.service.EmailService;
import com.overseer.service.RequestService;
import com.overseer.service.UserService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;


/**
 * Implementation of {@link UserService} interface.
 */
@Service
@Slf4j
@PropertySource("classpath:security.properties")
public class UserServiceImpl extends CrudServiceImpl<User> implements UserService {

    @Value("${password.length}")
    private Integer newPasswordLength;

    private SecurityContextService securityContextService;

    private EmailService emailService;
    private EmailBuilder<User> emailStrategy;
    private UserDao userDao;
    private RequestService requestService;

    public UserServiceImpl(UserDao userDao,
                           RequestService requestService,
                           SecurityContextService securityContextService,
                           EmailService emailService,
                           @Qualifier("recoverBuilderImpl") EmailBuilder<User> emailStrategy) {
        super(userDao);
        this.userDao = userDao;
        this.requestService = requestService;
        this.securityContextService = securityContextService;
        this.emailService = emailService;
        this.emailStrategy = emailStrategy;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public User create(User user) throws EntityAlreadyExistsException {
        if (!this.emailAvailable(user)) {
            throw new EntityAlreadyExistsException("Supplied email is already taken: " + user.getEmail());
        }
        return super.create(user);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void delete(Long id) {
        User currentUser = securityContextService.currentUser();
        if (id.equals(currentUser.getId())) {
            throw new RemovingYourselfException("Id of current user is equals to id of user that is deleting ");
        }
        requestService.closeAllRequestsOfGivenReporter(id);
        super.delete(id);
    }

    /**
     * Checks if supplied email is already in the database.
     *
     * @param user user to check email for.
     * @return true if email available, false otherwise.
     */
    private boolean emailAvailable(User user) {
        val email = user.getEmail();
        return this.findByEmail(email) == null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void changePassword(String email) throws NoSuchEntityException {
        Assert.notNull(email, "email must not be null");
        val user = this.findByEmail(email);
        log.debug("Generating new password for user with email: {}", email);
        val newPassword = RandomStringUtils.randomAlphanumeric(newPasswordLength);
        user.setPassword(newPassword);
        val message = this.emailStrategy.buildMessage(user);
        emailService.sendMessage(message);
        userDao.save(user);
    }

    @Override
    public User findByEmail(String email) throws NoSuchEntityException {
        Assert.notNull(email, "email must not be null");
        User user = userDao.findByEmail(email);
        log.debug("Retrieving user with email: {}", email);
        return user;
    }

    @Override
    public List<User> findByRole(Role role, int pageNumber) {
        Assert.notNull(role, "role must not be null");
        val list = userDao.findByRole(role, this.DEFAULT_PAGE_SIZE, pageNumber);
        log.debug("Fetched {} users with role: {} for page number: {}", list.size(), role, pageNumber);
        return list;
    }

    @Override
    public List<User> findManagersByEmployee(Long employeeId) {
        val list = userDao.findManagersByEmployee(employeeId);
        log.debug("Fetched {} managers for employee with id: {}", list.size(), employeeId);
        return list;
    }

    @Override
    public List<User> findUsersByManager(Long managerId) {
        val list = userDao.findUsersByManager(managerId);
        log.debug("Fetched {} employees for manager with id: {}", list.size(), managerId);
        return list;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<User> findAllDeactivated(int pageNumber) {
        val list = userDao.findAllDeactivated(this.DEFAULT_PAGE_SIZE, pageNumber);
        log.debug("Fetched {} deactivated users for page number: {}", list.size(), pageNumber);
        return list;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void activate(Long id) {
        Assert.notNull(id, "id must not be null");
        log.debug("Activating entity with id: {}", id);
        userDao.activate(id);
    }
}
