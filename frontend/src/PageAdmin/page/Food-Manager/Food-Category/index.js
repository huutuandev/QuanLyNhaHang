import { Table, Button, Tag, message, Popconfirm } from 'antd';
import { useEffect, useState } from 'react';
import axios from 'axios';
import './FoodCatagory.scss';
import { useNavigate } from "react-router-dom";

function FoodCategory() {
  const navigate = useNavigate();
  const [categories, setCategories] = useState([]);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 5,
    total: 0,
  });

  // Gọi API lấy danh mục
  const fetchCategories = async () => {
    try {
      const res = await axios.get(`/api/categories`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
      });

      const data = res.data.map((cat, index) => ({
        ...cat,
        status: index % 2 === 0 ? 'Hoạt động' : 'Ngưng hoạt động', 
        createdAt: cat.createdAt || new Date().toLocaleString(),
        updatedAt: cat.updatedAt || new Date().toLocaleString(),
      }));

      setCategories(data);
      setPagination(prev => ({
        ...prev,
        total: data.length,
      }));
    } catch (err) {
      console.error('Lỗi khi fetch danh mục:', err);
    }
  };

  // Xóa danh mục
  const handleDelete = async (id, name) => {
    try {
      await axios.delete(`/api/categories/${id}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
      });
      message.success(`Đã xóa danh mục "${name}" thành công!`);
      fetchCategories();
    } catch (error) {
      console.error('Lỗi khi xóa danh mục:', error);
      message.error('Xóa thất bại!');
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  const columns = [
    {
      title: 'STT',
      key: 'stt',
      render: (text, record, index) =>
        (pagination.current - 1) * pagination.pageSize + index + 1,
      width: 70,
    },
    {
      title: 'Tên danh mục',
      dataIndex: 'name',
      key: 'name',
      render: text => <b>{text}</b>,
    },
    // {
    //   title: 'Trạng thái',
    //   dataIndex: 'status',
    //   key: 'status',
    //   render: status =>
    //     status === 'Hoạt động' ? (
    //       <Tag color="green">Hoạt động</Tag>
    //     ) : (
    //       <Tag color="red">Ngưng hoạt động</Tag>
    //     ),
    // },
    // {
    //   title: 'Ngày tạo',
    //   dataIndex: 'createdAt',
    //   key: 'createdAt',
    // },
    // {
    //   title: 'Ngày cập nhật',
    //   dataIndex: 'updatedAt',
    //   key: 'updatedAt',
    // },
    {
      title: 'Thao tác',
      key: 'action',
      render: (_, record) => (
        <>
          <Button
            type="primary"
            size="small"
            style={{ marginRight: 8 }}
            onClick={() => navigate(`/admin/edit-category`, { state: { id: record.id } })}
          >
            Sửa
          </Button>
          <Popconfirm
            title="Xóa danh mục này?"
            onConfirm={() => handleDelete(record.id, record.name)}
            okText="Xóa"
            cancelText="Hủy"
          >
            <Button danger size="small">Xóa</Button>
          </Popconfirm>
        </>
      ),
    },
  ];

  return (
    <>
      <div className="food-category-container">
        <h2 style={{ textAlign: 'center', marginBottom: 20 }}>
          Quản lý danh mục sản phẩm
        </h2>

        <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: 16 }}>
          <Button
            type="primary"
            onClick={() => navigate("/admin/edit-category")}
          >
            Thêm danh mục
          </Button>
        </div>

        <Table
          columns={columns}
          dataSource={categories.slice(
            (pagination.current - 1) * pagination.pageSize,
            pagination.current * pagination.pageSize
          )}
          rowKey="id"
          pagination={{
            current: pagination.current,
            pageSize: pagination.pageSize,
            total: pagination.total,
            onChange: (page) => setPagination(prev => ({ ...prev, current: page })),
            showSizeChanger: false,
            position: ['bottomCenter'],
          }}
        />
      </div>

    </>
  );
}

export default FoodCategory;
