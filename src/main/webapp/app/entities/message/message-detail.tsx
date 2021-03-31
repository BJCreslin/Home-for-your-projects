import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './message.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IMessageDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const MessageDetail = (props: IMessageDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { messageEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="messageDetailsHeading">Message</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{messageEntity.id}</dd>
          <dt>
            <span id="author">Author</span>
          </dt>
          <dd>{messageEntity.author}</dd>
          <dt>
            <span id="recepient">Recepient</span>
          </dt>
          <dd>{messageEntity.recepient}</dd>
          <dt>
            <span id="text">Text</span>
          </dt>
          <dd>{messageEntity.text}</dd>
          <dt>
            <span id="status">Status</span>
          </dt>
          <dd>{messageEntity.status}</dd>
          <dt>
            <span id="created">Created</span>
          </dt>
          <dd>{messageEntity.created ? <TextFormat value={messageEntity.created} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="edited">Edited</span>
          </dt>
          <dd>{messageEntity.edited ? <TextFormat value={messageEntity.edited} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/message" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/message/${messageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ message }: IRootState) => ({
  messageEntity: message.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(MessageDetail);
