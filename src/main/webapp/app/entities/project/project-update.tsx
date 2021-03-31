import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, reset } from './project.reducer';
import { IProject } from 'app/shared/model/project.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IProjectUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ProjectUpdate = (props: IProjectUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { projectEntity, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/project' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }
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
        ...projectEntity,
        ...values,
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
          <h2 id="homeForYourProjectsApp.project.home.createOrEditLabel" data-cy="ProjectCreateUpdateHeading">
            Create or edit a Project
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : projectEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="project-id">ID</Label>
                  <AvInput id="project-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="projectUrlLabel" for="project-projectUrl">
                  Project Url
                </Label>
                <AvField id="project-projectUrl" data-cy="projectUrl" type="text" name="projectUrl" />
              </AvGroup>
              <AvGroup>
                <Label id="descriptionLabel" for="project-description">
                  Description
                </Label>
                <AvField id="project-description" data-cy="description" type="text" name="description" />
              </AvGroup>
              <AvGroup>
                <Label id="projectNameLabel" for="project-projectName">
                  Project Name
                </Label>
                <AvField
                  id="project-projectName"
                  data-cy="projectName"
                  type="text"
                  name="projectName"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="commentLabel" for="project-comment">
                  Comment
                </Label>
                <AvField id="project-comment" data-cy="comment" type="text" name="comment" />
              </AvGroup>
              <AvGroup>
                <Label id="statusLabel" for="project-status">
                  Status
                </Label>
                <AvInput
                  id="project-status"
                  data-cy="status"
                  type="select"
                  className="form-control"
                  name="status"
                  value={(!isNew && projectEntity.status) || 'NEW'}
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
                <Label id="createdLabel" for="project-created">
                  Created
                </Label>
                <AvInput
                  id="project-created"
                  data-cy="created"
                  type="datetime-local"
                  className="form-control"
                  name="created"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.projectEntity.created)}
                />
              </AvGroup>
              <AvGroup>
                <Label id="editedLabel" for="project-edited">
                  Edited
                </Label>
                <AvInput
                  id="project-edited"
                  data-cy="edited"
                  type="datetime-local"
                  className="form-control"
                  name="edited"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.projectEntity.edited)}
                />
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/project" replace color="info">
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
  projectEntity: storeState.project.entity,
  loading: storeState.project.loading,
  updating: storeState.project.updating,
  updateSuccess: storeState.project.updateSuccess,
});

const mapDispatchToProps = {
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ProjectUpdate);
