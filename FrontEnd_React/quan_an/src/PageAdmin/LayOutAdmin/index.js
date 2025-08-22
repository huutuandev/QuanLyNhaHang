import { Badge, Layout, Menu, Avatar, Dropdown, Space } from "antd";
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
import { useState, useEffect } from "react";
import axios from "axios";

const { Content, Sider } = Layout;

function LayoutAdmin() {
  const [collapsed, setcollapsed] = useState(false);
  const navigate = useNavigate();
  const [fullname, setFullName] = useState("");

  useEffect(() => {
    axios.get(`/api/users/profile`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
    })
      .then(res => {
        setFullName(res.data.fullname);
      })
      .catch(err => console.error(err));
  }, []);

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
    {
      key: "5", icon: <TableOutlined />,
      label: "Quản Lý Đặt Bàn",
      children: [
        { key: "5-1", label: "Danh Sách Đặt Bàn" },
        { key: "5-2", label: "Danh Sách Bàn" },

      ],
    },
    { key: "6", icon: <MessageOutlined />, label: "Tư vấn với khách hàng" },
  ];

  const router = {
    "1": "/admin",
    "2-1": "/admin/Food-Category",
    "2-2": "/admin/Food-List",
    "2-3": "/admin/Delete-Food",
    "3": "/admin/Post-Manager",
    "4": "/admin/Account-Manager",
    "5-1": "/admin/Booking-Manager",
    "5-2": "/admin/Table-Manager",
    "6": "/admin/Customer-Support",
  };

  const userMenu = (
    <Menu
      items={[
        { key: "hello", label: `Xin chào,${fullname}` },
        {
          key: "logout", label: "Đăng xuất", onClick: () => {
            localStorage.removeItem("token");
            navigate("/login");
          }
        }
      ]}
    />
  )

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
            <Dropdown overlay={userMenu} trigger={['click']} placement="bottomRight">
              <Space style={{ cursor: "pointer" }}>
                <Avatar
                  src=""
                  size={32}
                  alt="admin avatar"
                  icon={<UserOutlined />}
                />
              </Space>
            </Dropdown>
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
