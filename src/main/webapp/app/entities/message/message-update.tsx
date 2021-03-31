import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, reset } from './message.reducer';
import { IMessage } from 'app/shared/model/message.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IMessageUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const MessageUpdate = (props: IMessageUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { messageEntity, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/message' + props.location.search);
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
        ...messageEntity,
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
          <h2 id="homeForYourProjectsApp.message.home.createOrEditLabel" data-cy="MessageCreateUpdateHeading">
            Create or edit a Message
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : messageEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="message-id">ID</Label>
                  <AvInput id="message-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="authorLabel" for="message-author">
                  Author
                </Label>
                <AvField id="message-author" data-cy="author" type="text" name="author" />
              </AvGroup>
              <AvGroup>
                <Label id="recepientLabel" for="message-recepient">
                  Recepient
                </Label>
                <AvField id="message-recepient" data-cy="recepient" type="text" name="recepient" />
              </AvGroup>
              <AvGroup>
                <Label id="textLabel" for="message-text">
                  Text
                </Label>
                <AvField id="message-text" data-cy="text" type="text" name="text" />
              </AvGroup>
              <AvGroup>
                <Label id="statusLabel" for="message-status">
                  Status
                </Label>
                <AvInput
                  id="message-status"
                  data-cy="status"
                  type="select"
                  className="form-control"
                  name="status"
                  value={(!isNew && messageEntity.status) || 'NEW'}
                >
                  <option value="NEW">NEW</option>
                  <option value="READED">READED</option>
                  <option value="EDITED">EDITED</option>
                  <option value="DELETED">DELETED</option>
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label id="createdLabel" for="message-created">
                  Created
                </Label>
                <AvInput
                  id="message-created"
                  data-cy="created"
                  type="datetime-local"
                  className="form-control"
                  name="created"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.messageEntity.created)}
                />
              </AvGroup>
              <AvGroup>
                <Label id="editedLabel" for="message-edited">
                  Edited
                </Label>
                <AvInput
                  id="message-edited"
                  data-cy="edited"
                  type="datetime-local"
                  className="form-control"
                  name="edited"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.messageEntity.edited)}
                />
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/message" replace color="info">
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
  messageEntity: storeState.message.entity,
  loading: storeState.message.loading,
  updating: storeState.message.updating,
  updateSuccess: storeState.message.updateSuccess,
});

const mapDispatchToProps = {
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(MessageUpdate);
