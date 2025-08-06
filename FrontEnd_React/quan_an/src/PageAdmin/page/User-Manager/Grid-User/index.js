import { Card, Col, Row } from 'antd';
import "./Grid-Users.scss"
import CardItem from '../CardItem';
import { Color } from 'antd/es/color-picker';
function GridUsers() {
  return (
    <>

      <div>
        <Row gutter={[16, 16]} className="mt-20">
          <Col xxl={6} xl={6} lg={6} md={12} sm={24} xs={24}>
            <CardItem title="box user" />
          </Col>
          <Col xxl={6} xl={6} lg={6} md={12} sm={24} xs={24}>
            <CardItem title="box 2" />
          </Col>
          <Col xxl={6} xl={6} lg={6} md={12} sm={24} xs={24}>
            <CardItem title="box 3" />
          </Col>
          <Col xxl={6} xl={6} lg={6} md={12} sm={24} xs={24}>
            <CardItem title="box 4" />
          </Col>
        </Row>

        <Row gutter={[20, 20]} className="mt-20">
          <Col xxl={16} xl={16} lg={16} md={24} sm={24} xs={24} >
            <CardItem title="box 5" style={{ height: "400px"}} />
          </Col>
          <Col xxl={8} xl={8} lg={8} md={24} sm={24} xs={24} >
            <CardItem title="box 6" style={{ height: "400px" }} />
          </Col>
        </Row>

        <Row gutter={[20, 20]} className="mt-20">
          <Col xxl={8} xl={8} lg={8} md={24} sm={24} xs={24} >
            <CardItem title="box 7" style={{ height: "400px" }} />
          </Col>
          <Col xxl={16} xl={16} lg={16} md={24} sm={24} xs={24}>
            <CardItem title="box 8" style={{ height: "400px" }} />
          </Col>
        </Row>
      </div>
    </>
  );
};

export default GridUsers;
