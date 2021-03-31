import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './project.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IProjectDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ProjectDetail = (props: IProjectDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { projectEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="projectDetailsHeading">Project</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{projectEntity.id}</dd>
          <dt>
            <span id="projectUrl">Project Url</span>
          </dt>
          <dd>{projectEntity.projectUrl}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{projectEntity.description}</dd>
          <dt>
            <span id="projectName">Project Name</span>
          </dt>
          <dd>{projectEntity.projectName}</dd>
          <dt>
            <span id="comment">Comment</span>
          </dt>
          <dd>{projectEntity.comment}</dd>
          <dt>
            <span id="status">Status</span>
          </dt>
          <dd>{projectEntity.status}</dd>
          <dt>
            <span id="created">Created</span>
          </dt>
          <dd>{projectEntity.created ? <TextFormat value={projectEntity.created} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="edited">Edited</span>
          </dt>
          <dd>{projectEntity.edited ? <TextFormat value={projectEntity.edited} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/project" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/project/${projectEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ project }: IRootState) => ({
  projectEntity: project.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ProjectDetail);
