import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IProject } from 'app/shared/model/project.model';
import { getEntities as getProjects } from 'app/entities/project/project.reducer';
import { getEntity, updateEntity, createEntity, reset } from './task.reducer';
import { ITask } from 'app/shared/model/task.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface ITaskUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const TaskUpdate = (props: ITaskUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { taskEntity, projects, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/task' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getProjects();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    values.created = convertDateTimeToServer(values.created);
    values.edited = convertDateTimeToServer(values.edited);

    if (errors.length === 0) {
      const entity = {
        ...taskEntity,
        ...values,
        project: projects.find(it => it.id.toString() === values.projectId.toString()),
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="homeForYourProjectsApp.task.home.createOrEditLabel" data-cy="TaskCreateUpdateHeading">
            Create or edit a Task
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : taskEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="task-id">ID</Label>
                  <AvInput id="task-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="authorLabel" for="task-author">
                  Author
                </Label>
                <AvField id="task-author" data-cy="author" type="text" name="author" />
              </AvGroup>
              <AvGroup>
                <Label id="implementerLabel" for="task-implementer">
                  Implementer
                </Label>
                <AvField id="task-implementer" data-cy="implementer" type="text" name="implementer" />
              </AvGroup>
              <AvGroup>
                <Label id="nameLabel" for="task-name">
                  Name
                </Label>
                <AvField
                  id="task-name"
                  data-cy="name"
                  type="text"
                  name="name"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="textLabel" for="task-text">
                  Text
                </Label>
                <AvField id="task-text" data-cy="text" type="text" name="text" />
              </AvGroup>
              <AvGroup>
                <Label id="commentLabel" for="task-comment">
                  Comment
                </Label>
                <AvField id="task-comment" data-cy="comment" type="text" name="comment" />
              </AvGroup>
              <AvGroup>
                <Label id="statusLabel" for="task-status">
                  Status
                </Label>
                <AvInput
                  id="task-status"
                  data-cy="status"
                  type="select"
                  className="form-control"
                  name="status"
                  value={(!isNew && taskEntity.status) || 'NEW'}
                >
                  <option value="NEW">NEW</option>
                  <option value="CLOSED">CLOSED</option>
                  <option value="ENDED">ENDED</option>
                  <option value="ACTIVE">ACTIVE</option>
                  <option value="DELETED">DELETED</option>
                  <option value="STOPED">STOPED</option>
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label id="createdLabel" for="task-created">
                  Created
                </Label>
                <AvInput
                  id="task-created"
                  data-cy="created"
                  type="datetime-local"
                  className="form-control"
                  name="created"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.taskEntity.created)}
                />
              </AvGroup>
              <AvGroup>
                <Label id="editedLabel" for="task-edited">
                  Edited
                </Label>
                <AvInput
                  id="task-edited"
                  data-cy="edited"
                  type="datetime-local"
                  className="form-control"
                  name="edited"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.taskEntity.edited)}
                />
              </AvGroup>
              <AvGroup>
                <Label for="task-project">Project</Label>
                <AvInput id="task-project" data-cy="project" type="select" className="form-control" name="projectId">
                  <option value="" key="0" />
                  {projects
                    ? projects.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/task" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  projects: storeState.project.entities,
  taskEntity: storeState.task.entity,
  loading: storeState.task.loading,
  updating: storeState.task.updating,
  updateSuccess: storeState.task.updateSuccess,
});

const mapDispatchToProps = {
  getProjects,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(TaskUpdate);
