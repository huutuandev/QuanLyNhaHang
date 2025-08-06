import { Badge, Layout, Menu, Avatar } from "antd";
import { Outlet, useNavigate } from "react-router-dom";
import './LayOutAdmin.scss';
import logo from "../image/logo.png";
import logofalse from "../image/logo1.png";
import {
  SearchOutlined,
  MenuUnfoldOutlined,
  HomeOutlined,
  AppstoreOutlined,
  FileTextOutlined,
  UserOutlined,
  TableOutlined,
  MessageOutlined,
  BellOutlined
} from "@ant-design/icons";
import { Children, useState } from "react";

const { Content, Sider } = Layout;

function LayoutAdmin() {
  const [collapsed, setcollapsed] = useState(false);
  const navigate = useNavigate();

  const menuItems = [
    { key: "1", icon: <HomeOutlined />, label: "Dashboard" },
    {
      key: "2", icon: <AppstoreOutlined />,
      label: "Quản Lý Món Ăn",
      children: [
        { key: "2-1", label: "Danh Mục Món Ăn" },
        { key: "2-2", label: "Món Ăn" },
        { key: "2-3", label: "Món Ăn Tạm Xoá" },
      ],
    },
    { key: "3", icon: <FileTextOutlined />, label: "Quản Lý Bài Viết" },
    { key: "4", icon: <UserOutlined />, label: "Quản Lý Tài Khoản" },
    { key: "5", icon: <TableOutlined />, label: "Quản Lý Đặt Bàn" },
    { key: "6", icon: <MessageOutlined />, label: "Tư vấn với khách hàng" },
  ];

  const router = {
    "1": "/admin",
    "2-1": "/admin/Food-Category",
    "2-2": "/admin/Food-List",
    "2-3": "/admin/Delete-Food",
    "3": "/admin/Post-Manager",
    "4": "/admin/Account-Manager",
    "5": "/admin/Booking-Manager",
    "6": "/admin/Customer-Support",
  };

  return (
    <Layout className="layout-default">
      <header className="header-admin">
        <div className={`header-admin__logo ${collapsed ? "collapsed" : ""}`}>
          <img src={collapsed ? logofalse : logo} alt="logo" />
        </div>
        <div className="header-admin__nav">
          <div className="header-admin__nav-left">
            <div
              className="header-admin__collaspa"
              onClick={() => setcollapsed(!collapsed)}
            >
              <MenuUnfoldOutlined />
            </div>
            <div className="header-admin__search">
              <SearchOutlined />
            </div>
          </div>
          <div className="header-admin__nav-right">
            <Badge dot offset={[-2, 2]}>
              <BellOutlined style={{ fontSize: 20, cursor: "pointer" }} />
            </Badge>
            <div className="admin-user-info">
              <Avatar
                src=""
                size={32}
                alt="admin avatar"
              />
              <span className="admin-username">
                Hi , <strong>Huy admin</strong>
              </span>
            </div>
          </div>
        </div>
      </header>

      <Layout>
        <Sider
          className="sider-admin"
          collapsed={collapsed}
          theme="light"
        >
          <Menu
            mode="inline"
            defaultSelectedKeys={["1"]}
            items={menuItems}
            onSelect={({ key }) => navigate(router[key])}
          />
        </Sider>

        <Content className="content-admin">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}

export default LayoutAdmin;
