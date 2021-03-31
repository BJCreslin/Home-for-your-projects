import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, reset } from './user-info.reducer';
import { IUserInfo } from 'app/shared/model/user-info.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IUserInfoUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const UserInfoUpdate = (props: IUserInfoUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { userInfoEntity, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/user-info');
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
        ...userInfoEntity,
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
          <h2 id="homeForYourProjectsApp.userInfo.home.createOrEditLabel" data-cy="UserInfoCreateUpdateHeading">
            Create or edit a UserInfo
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : userInfoEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="user-info-id">ID</Label>
                  <AvInput id="user-info-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="emailLabel" for="user-info-email">
                  Email
                </Label>
                <AvField
                  id="user-info-email"
                  data-cy="email"
                  type="text"
                  name="email"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="gitHubIdLabel" for="user-info-gitHubId">
                  Git Hub Id
                </Label>
                <AvField id="user-info-gitHubId" data-cy="gitHubId" type="text" name="gitHubId" />
              </AvGroup>
              <AvGroup>
                <Label id="nameLabel" for="user-info-name">
                  Name
                </Label>
                <AvField
                  id="user-info-name"
                  data-cy="name"
                  type="text"
                  name="name"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="hoursLabel" for="user-info-hours">
                  Hours
                </Label>
                <AvField
                  id="user-info-hours"
                  data-cy="hours"
                  type="string"
                  className="form-control"
                  name="hours"
                  validate={{
                    min: { value: 0, errorMessage: 'This field should be at least 0.' },
                    max: { value: 169, errorMessage: 'This field cannot be more than 169.' },
                    number: { value: true, errorMessage: 'This field should be a number.' },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="statusLabel" for="user-info-status">
                  Status
                </Label>
                <AvInput
                  id="user-info-status"
                  data-cy="status"
                  type="select"
                  className="form-control"
                  name="status"
                  value={(!isNew && userInfoEntity.status) || 'WAITING_FOR_A_TASK'}
                >
                  <option value="WAITING_FOR_A_TASK">WAITING_FOR_A_TASK</option>
                  <option value="BUSY">BUSY</option>
                  <option value="TEMPORARILY_INACTIVE">TEMPORARILY_INACTIVE</option>
                  <option value="ACADEMIC_LEAVE">ACADEMIC_LEAVE</option>
                  <option value="DELETED">DELETED</option>
                  <option value="BANNED">BANNED</option>
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label id="birthdayLabel" for="user-info-birthday">
                  Birthday
                </Label>
                <AvField id="user-info-birthday" data-cy="birthday" type="date" className="form-control" name="birthday" />
              </AvGroup>
              <AvGroup>
                <Label id="commentLabel" for="user-info-comment">
                  Comment
                </Label>
                <AvField id="user-info-comment" data-cy="comment" type="text" name="comment" />
              </AvGroup>
              <AvGroup>
                <Label id="createdLabel" for="user-info-created">
                  Created
                </Label>
                <AvInput
                  id="user-info-created"
                  data-cy="created"
                  type="datetime-local"
                  className="form-control"
                  name="created"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.userInfoEntity.created)}
                />
              </AvGroup>
              <AvGroup>
                <Label id="editedLabel" for="user-info-edited">
                  Edited
                </Label>
                <AvInput
                  id="user-info-edited"
                  data-cy="edited"
                  type="datetime-local"
                  className="form-control"
                  name="edited"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.userInfoEntity.edited)}
                />
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/user-info" replace color="info">
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
  userInfoEntity: storeState.userInfo.entity,
  loading: storeState.userInfo.loading,
  updating: storeState.userInfo.updating,
  updateSuccess: storeState.userInfo.updateSuccess,
});

const mapDispatchToProps = {
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(UserInfoUpdate);
