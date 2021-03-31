import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './task.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ITaskDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const TaskDetail = (props: ITaskDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { taskEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="taskDetailsHeading">Task</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{taskEntity.id}</dd>
          <dt>
            <span id="author">Author</span>
          </dt>
          <dd>{taskEntity.author}</dd>
          <dt>
            <span id="implementer">Implementer</span>
          </dt>
          <dd>{taskEntity.implementer}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{taskEntity.name}</dd>
          <dt>
            <span id="text">Text</span>
          </dt>
          <dd>{taskEntity.text}</dd>
          <dt>
            <span id="comment">Comment</span>
          </dt>
          <dd>{taskEntity.comment}</dd>
          <dt>
            <span id="status">Status</span>
          </dt>
          <dd>{taskEntity.status}</dd>
          <dt>
            <span id="created">Created</span>
          </dt>
          <dd>{taskEntity.created ? <TextFormat value={taskEntity.created} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="edited">Edited</span>
          </dt>
          <dd>{taskEntity.edited ? <TextFormat value={taskEntity.edited} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>Project</dt>
          <dd>{taskEntity.project ? taskEntity.project.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/task" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/task/${taskEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ task }: IRootState) => ({
  taskEntity: task.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(TaskDetail);
