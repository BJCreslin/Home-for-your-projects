package ru.bjcreslin.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bjcreslin.domain.Project;
import ru.bjcreslin.repository.ProjectRepository;
import ru.bjcreslin.service.ProjectService;

/**
 * Service Implementation for managing {@link Project}.
 */
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Project save(Project project) {
        log.debug("Request to save Project : {}", project);
        return projectRepository.save(project);
    }

    @Override
    public Optional<Project> partialUpdate(Project project) {
        log.debug("Request to partially update Project : {}", project);

        return projectRepository
            .findById(project.getId())
            .map(
                existingProject -> {
                    if (project.getProjectUrl() != null) {
                        existingProject.setProjectUrl(project.getProjectUrl());
                    }
                    if (project.getDescription() != null) {
                        existingProject.setDescription(project.getDescription());
                    }
                    if (project.getProjectName() != null) {
                        existingProject.setProjectName(project.getProjectName());
                    }
                    if (project.getComment() != null) {
                        existingProject.setComment(project.getComment());
                    }
                    if (project.getStatus() != null) {
                        existingProject.setStatus(project.getStatus());
                    }
                    if (project.getCreated() != null) {
                        existingProject.setCreated(project.getCreated());
                    }
                    if (project.getEdited() != null) {
                        existingProject.setEdited(project.getEdited());
                    }

                    return existingProject;
                }
            )
            .map(projectRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Project> findAll(Pageable pageable) {
        log.debug("Request to get all Projects");
        return projectRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Project> findOne(Long id) {
        log.debug("Request to get Project : {}", id);
        return projectRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Project : {}", id);
        projectRepository.deleteById(id);
    }
}
