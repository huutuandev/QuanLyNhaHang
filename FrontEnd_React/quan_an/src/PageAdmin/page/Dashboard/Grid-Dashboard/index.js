import { Card, Col, Row } from 'antd';
import { useEffect, useState } from "react";
import "./Grid-Dashboard.scss"
import CardItem from '../CardItem';
import axios from 'axios';
import TKDoanhThu from '../Component/TK-DoanhThu';
import TKSoSanhDoanhThu from '../Component/TKSoSanhDoanhThu';
import {
  UserOutlined,
  FileTextOutlined,
  CoffeeOutlined,
  AppstoreOutlined,
} from "@ant-design/icons";
function GridDashboard() {
  const [Dashboard, setDasahboard] = useState(null);
  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const res = await axios.get("/api/dashboard", {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        });
        setDasahboard(res.data);
      } catch (err) {
        console.error("Failed to fetch dashboard", err);
      }
    };
    fetchDashboard();
  }, []);
  if (!Dashboard) return <div>Đang tải dữ liệu...</div>;
  return (
    <>
      <div className="main-Dashboard">
        <div className="main-Dashboard__content1">
          <h2>Dashboard</h2>
          <h4>THT admin </h4>
        </div>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={6}>
            <CardItem
              title="Số lượng tài khoản"
              count={Dashboard.totalUsers}
              icon={<UserOutlined />}
              color="#1890ff"
            />
          </Col>
          <Col xs={24} sm={12} md={6}>
            <CardItem
              title="Số lượng bài viết"
              count={Dashboard.totalPosts}
              icon={<FileTextOutlined />}
              color="#52c41a"
            />
          </Col>
          <Col xs={24} sm={12} md={6}>
            <CardItem
              title="Số lượng món ăn"
              count={Dashboard.totalFoods}
              icon={<CoffeeOutlined />}
              color="#36cfc9"
            />
          </Col>
          <Col xs={24} sm={12} md={6}>
            <CardItem
              title="Số lượng danh mục"
              count={Dashboard.totalCategories}
              icon={<AppstoreOutlined />}
              color="#fa8c16"
            />
          </Col>
        </Row>

        <Row gutter={[20, 20]} className="mt-20">
          <Col xxl={24} xl={24} lg={24} md={24} sm={24} xs={24} >
             <TKSoSanhDoanhThu />
          </Col>
        </Row>

        {/* <Row gutter={[20, 20]} className="mt-20">
          <Col xxl={24} xl={24} lg={24} md={24} sm={24} xs={24} >
            <CardItem title="box 6" style={{ height: "400px" }} />
          </Col>
        </Row> */}
        <Row gutter={[20, 20]} className="mt-20">
          <Col xxl={24} xl={24} lg={24} md={24} sm={24} xs={24} >
            <Card title="Thống kê doanh thu">
              <TKDoanhThu />
            </Card>

          </Col>
        </Row>
      </div>
    </>
  );
};

export default GridDashboard;
