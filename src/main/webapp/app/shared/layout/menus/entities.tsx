import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';

import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown icon="th-list" name="Entities" id="entity-menu" data-cy="entity" style={{ maxHeight: '80vh', overflow: 'auto' }}>
    <MenuItem icon="asterisk" to="/project">
      Project
    </MenuItem>
    <MenuItem icon="asterisk" to="/task">
      Task
    </MenuItem>
    <MenuItem icon="asterisk" to="/comment">
      Comment
    </MenuItem>
    <MenuItem icon="asterisk" to="/user-info">
      User Info
    </MenuItem>
    <MenuItem icon="asterisk" to="/message">
      Message
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
