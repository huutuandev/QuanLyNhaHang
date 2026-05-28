import { Table, Button, Image, Tag, message, Popconfirm, Input } from 'antd';
import "./Grid-Users.scss"
import { useEffect, useState, navigate } from 'react';
import axios from 'axios';
import { SearchOutlined } from '@ant-design/icons';
import { useNavigate } from "react-router-dom";
function GridUsers() {
  const navigate = useNavigate();
  const [user, setUser] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 5,
    total: 0,
  });

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(`/api/users`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      setUser(res.data);
      setPagination((prev) => ({
        ...prev,
        total: res.data.length
      }));
    } catch (err) {
      message.error("Lỗi khi tải danh sách");
    }
  };

  const handleDelete = async (id) => {
    try {
      const token = localStorage.getItem("token");
      await axios.delete(`/api/users/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      message.success("Xoá tài khoản thành công");
      fetchUsers();
    } catch (err) {
      message.error("Lỗi khi xóa tài khoản");
    }
  };

  const filteredUsers = user.filter(u =>
    u.fullname?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    u.phoneNumber?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const columns = [
    {
      title: 'STT',
      key: 'stt',
      render: (_, __, index) => (pagination.current - 1) * pagination.pageSize + index + 1
    },
    {
      title: 'Ảnh đại diện',
      dataIndex: 'imageUrl',
      key: 'imageUrl',
      render: (url) => (
        url ? <Image src={url} width={40} height={40} style={{ borderRadius: '50%' }} />
          : <Image src="/default-avatar.png" width={40} height={40} style={{ borderRadius: '50%' }} />
      )
    },
    {
      title: 'Tên đầy đủ',
      dataIndex: 'fullname',
      key: 'fullname',
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: 'SĐT',
      dataIndex: 'phoneNumber',
      key: 'phoneNumber',
    },
    {
      title: 'Loại người dùng',
      dataIndex: 'roleNames',
      key: 'roleNames',
      render: (roles) => (
        roles.map(role => {
          let color = 'blue';
          let text = '';
          if (role === 'ROLE_ADMIN') {
            color = 'orange';
            text = 'Admin';
          } else if (role === 'ROLE_STAFF') {
            color = 'blue';
            text = 'Nhân Viên';
          } else if (role === 'ROLE_CUSTOMER') {
            color = 'purple';
            text = 'Khách Hàng';
          }
          return <Tag color={color} key={role}>{text}</Tag>
        })
      )
    },
    {
      title: 'Thao tác',
      key: 'action',
      render: (_, record) => (
        <Popconfirm
          title="Bạn có chắc chắn muốn xóa tài khoản này?"
          onConfirm={() => handleDelete(record.id)}
          okText="Xóa"
          cancelText="Hủy"
        >
          <Button danger>Xóa</Button>
        </Popconfirm>
      )
    }
  ];

  return (
    <>
      <div style={{ margin: '20px' }}>
        <div className='User-header'>
          <h3>quản lý tài khoản</h3>
        </div>
        <div className='User-Control'>
          <Input
            placeholder="Tìm kiếm theo tên hoặc số điện thoại..."
            prefix={<SearchOutlined />}
            style={{ marginBottom: 16, width: 350, borderRadius: 20 }}
            value={searchTerm}
            onChange={(e) => {
              setSearchTerm(e.target.value);
              setPagination((prev) => ({ ...prev, current: 1 }));
            }}
          />
          <div className="btn-group">
            <Button
              type="primary"
              onClick={() => navigate("/admin/Add-User")}
            >Thêm tài Khoản</Button>
          </div>
        </div>
      </div>

      <Table
        columns={columns}
        dataSource={filteredUsers.slice(
          (pagination.current - 1) * pagination.pageSize,
          pagination.current * pagination.pageSize
        )}
        rowKey="id"
        pagination={{
          current: pagination.current,
          pageSize: pagination.pageSize,
          total: filteredUsers.length,
          onChange: (page) => setPagination((prev) => ({ ...prev, current: page })),
          showSizeChanger: false,
          position: ['bottomCenter'],
        }}
      />
    </>
  );
}

export default GridUsers;
