import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './user-info.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IUserInfoDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const UserInfoDetail = (props: IUserInfoDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { userInfoEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userInfoDetailsHeading">UserInfo</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{userInfoEntity.id}</dd>
          <dt>
            <span id="email">Email</span>
          </dt>
          <dd>{userInfoEntity.email}</dd>
          <dt>
            <span id="gitHubId">Git Hub Id</span>
          </dt>
          <dd>{userInfoEntity.gitHubId}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{userInfoEntity.name}</dd>
          <dt>
            <span id="hours">Hours</span>
          </dt>
          <dd>{userInfoEntity.hours}</dd>
          <dt>
            <span id="status">Status</span>
          </dt>
          <dd>{userInfoEntity.status}</dd>
          <dt>
            <span id="birthday">Birthday</span>
          </dt>
          <dd>
            {userInfoEntity.birthday ? <TextFormat value={userInfoEntity.birthday} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="comment">Comment</span>
          </dt>
          <dd>{userInfoEntity.comment}</dd>
          <dt>
            <span id="created">Created</span>
          </dt>
          <dd>{userInfoEntity.created ? <TextFormat value={userInfoEntity.created} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="edited">Edited</span>
          </dt>
          <dd>{userInfoEntity.edited ? <TextFormat value={userInfoEntity.edited} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/user-info" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/user-info/${userInfoEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ userInfo }: IRootState) => ({
  userInfoEntity: userInfo.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(UserInfoDetail);
