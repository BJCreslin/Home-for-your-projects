package ru.bjcreslin.service.impl;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bjcreslin.domain.UserInfo;
import ru.bjcreslin.repository.UserInfoRepository;
import ru.bjcreslin.service.UserInfoService;

/**
 * Service Implementation for managing {@link UserInfo}.
 */
@Service
@Transactional
public class UserInfoServiceImpl implements UserInfoService {

    private final Logger log = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    private final UserInfoRepository userInfoRepository;

    public UserInfoServiceImpl(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public UserInfo save(UserInfo userInfo) {
        log.debug("Request to save UserInfo : {}", userInfo);
        return userInfoRepository.save(userInfo);
    }

    @Override
    public Optional<UserInfo> partialUpdate(UserInfo userInfo) {
        log.debug("Request to partially update UserInfo : {}", userInfo);

        return userInfoRepository
            .findById(userInfo.getId())
            .map(
                existingUserInfo -> {
                    if (userInfo.getEmail() != null) {
                        existingUserInfo.setEmail(userInfo.getEmail());
                    }
                    if (userInfo.getGitHubId() != null) {
                        existingUserInfo.setGitHubId(userInfo.getGitHubId());
                    }
                    if (userInfo.getName() != null) {
                        existingUserInfo.setName(userInfo.getName());
                    }
                    if (userInfo.getHours() != null) {
                        existingUserInfo.setHours(userInfo.getHours());
                    }
                    if (userInfo.getStatus() != null) {
                        existingUserInfo.setStatus(userInfo.getStatus());
                    }
                    if (userInfo.getBirthday() != null) {
                        existingUserInfo.setBirthday(userInfo.getBirthday());
                    }
                    if (userInfo.getComment() != null) {
                        existingUserInfo.setComment(userInfo.getComment());
                    }
                    if (userInfo.getCreated() != null) {
                        existingUserInfo.setCreated(userInfo.getCreated());
                    }
                    if (userInfo.getEdited() != null) {
                        existingUserInfo.setEdited(userInfo.getEdited());
                    }

                    return existingUserInfo;
                }
            )
            .map(userInfoRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserInfo> findAll() {
        log.debug("Request to get all UserInfos");
        return userInfoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserInfo> findOne(Long id) {
        log.debug("Request to get UserInfo : {}", id);
        return userInfoRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserInfo : {}", id);
        userInfoRepository.deleteById(id);
    }
}
