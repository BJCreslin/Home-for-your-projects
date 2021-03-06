import './footer.scss';

import React from 'react';

import { Col, Row } from 'reactstrap';

const Footer = props => (
  <div className="footer page-content">
    <Row>
      <Col md="12">
        <p>
          2021. <a href="mailto:bjcreslin@gmail.com">E-mail: bjcreslin@gmail.com</a>{' '}
          <a href="http://www.apache.org/licenses/LICENSE-2.0">APACHE LICENSE, VERSION 2.0</a>
        </p>
      </Col>
    </Row>
  </div>
);

export default Footer;
