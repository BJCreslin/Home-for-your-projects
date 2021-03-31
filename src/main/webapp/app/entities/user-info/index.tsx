import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import UserInfo from './user-info';
import UserInfoDetail from './user-info-detail';
import UserInfoUpdate from './user-info-update';
import UserInfoDeleteDialog from './user-info-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={UserInfoUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={UserInfoUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={UserInfoDetail} />
      <ErrorBoundaryRoute path={match.url} component={UserInfo} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={UserInfoDeleteDialog} />
  </>
);

export default Routes;
